package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.GemItems;
import gurumirum.gemthing.contents.Wands;
import gurumirum.gemthing.net.msgs.SetBeamCraftingInfoMsg;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = GemthingMod.MODID)
public final class InWorldBeamCraftingManager {
	private InWorldBeamCraftingManager() {}

	private static final Map<UUID, @Nullable BlockPos> focus = new Object2ObjectOpenHashMap<>();
	private static final Map<ServerLevel, Object2IntMap<BlockPos>> beam = new Object2ObjectOpenHashMap<>();
	private static final Map<BlockState, Recipe> recipes = new Object2ObjectOpenHashMap<>();

	private static boolean init;

	public static void init() {
		if (init) return;
		init = true;

		addRecipe(Blocks.SAND, new Recipe(25, LootTable.lootTable()
				.withPool(new LootPool.Builder()
						.add(LootItem.lootTableItem(GemItems.BRIGHTSTONE)))
				.build()));
	}

	private static void addRecipe(BlockState blockState, Recipe recipe) {
		recipes.put(blockState, recipe);
	}

	private static void addRecipe(Block block, Recipe recipe) {
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			recipes.put(state, recipe);
		}
	}

	public static void removeFocus(@NotNull Player player) {
		setFocus(player, null);
	}

	public static void setFocus(@NotNull Player player, @Nullable BlockPos blockPos) {
		if (player.level().isClientSide) {
			if (Objects.equals(focus.get(player.getUUID()), blockPos)) return;
			PacketDistributor.sendToServer(new SetBeamCraftingInfoMsg(blockPos));
		}
		if (blockPos != null) focus.put(player.getUUID(), blockPos);
		else focus.remove(player.getUUID());
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		focus.clear();
	}

	@SubscribeEvent
	public static void afterLevelTick(LevelTickEvent.Post event) {
		final int PLAYER_FOCUS_UPDATE_INTERVAL = 2;
		final int PROGRESS_DECAY_UPDATE_INTERVAL = PLAYER_FOCUS_UPDATE_INTERVAL * 10;
		final int DECAY_AMOUNT_PER_SEC = 5;

		long gameTime = event.getLevel().getGameTime();
		boolean focusUpdateTick = gameTime % PLAYER_FOCUS_UPDATE_INTERVAL == 0;
		boolean decayUpdateTick = gameTime % PROGRESS_DECAY_UPDATE_INTERVAL == 0;

		if ((!focusUpdateTick) || !(event.getLevel() instanceof ServerLevel level)) return;

		Object2IntMap<BlockPos> beamProgress = null;
		for (Player p : event.getLevel().players()) {
			if (p.isDeadOrDying() || !p.isUsingItem() || !p.getUseItem().is(Wands.ANCIENT_LIGHT.asItem())) {
				focus.remove(p.getUUID());
				continue;
			}

			BlockPos focusPos = focus.get(p.getUUID());
			if (focusPos == null || !level.isLoaded(focusPos)) continue;

			BlockState state = level.getBlockState(focusPos);
			Recipe recipe = recipes.get(state);
			if (recipe == null) continue;

			if (beamProgress == null) beamProgress = beam.computeIfAbsent(level, l -> new Object2IntOpenHashMap<>());

			int progress = beamProgress.getInt(focusPos) + PLAYER_FOCUS_UPDATE_INTERVAL;
			if (progress >= recipe.processTicks) {
				beamProgress.removeInt(focusPos);
				focus.remove(p.getUUID());
				level.destroyBlock(focusPos, false);

				ObjectArrayList<ItemStack> stacks = recipe.loot.getRandomItems(new LootParams.Builder(level)
						.create(LootContextParamSets.EMPTY));

				for (ItemStack s : stacks) {
					ItemEntity itemEntity = new ItemEntity(level,
							focusPos.getX() + .5, focusPos.getY() + .5, focusPos.getZ() + .5,
							s);
					level.addFreshEntity(itemEntity);
				}
			} else {
				// sus logic to counteract decay (im lazy)
				beamProgress.put(focusPos, decayUpdateTick ? progress + DECAY_AMOUNT_PER_SEC : progress);
			}
		}

		if (decayUpdateTick) {
			for (var m : beam.values()) {
				var it = m.object2IntEntrySet().iterator();
				while (it.hasNext()) {
					var e = it.next();
					if (e.getIntValue() - DECAY_AMOUNT_PER_SEC <= 0) it.remove();
					else e.setValue(e.getIntValue() - DECAY_AMOUNT_PER_SEC);
				}
			}
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // wdym
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		beam.remove(event.getLevel());
	}

	public record Recipe(int processTicks, @NotNull LootTable loot) {}
}
