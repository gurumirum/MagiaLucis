package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.net.msgs.SetBeamCraftingInfoMsg;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class InWorldBeamCraftingManager {
	private InWorldBeamCraftingManager() {}

	private static final Map<UUID, @Nullable BlockPos> focus = new Object2ObjectOpenHashMap<>();
	private static final Map<ServerLevel, Object2IntMap<BlockPos>> beam = new Object2ObjectOpenHashMap<>();
	private static final Map<BlockState, Recipe> recipes = new Object2ObjectOpenHashMap<>();

	private static boolean init;

	public static void init() {
		if (init) return;
		init = true;

		addRecipe(Blocks.SAND, new Recipe(25, GemItems.BRIGHTSTONE));
		addRecipe(Blocks.RED_SAND, new Recipe(25, GemItems.RED_BRIGHTSTONE));

		addRecipe(Blocks.ICE, new Recipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.FROSTED_ICE, new Recipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.BLUE_ICE, new Recipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.PACKED_ICE, new Recipe(25, GemItems.ICY_BRIGHTSTONE));

		addRecipe(Blocks.SOUL_SAND, new Recipe(50, GemItems.SOUL_BRIGHTSTONE));

		addRecipe(Blocks.STONE, new Recipe(50, ModBuildingBlocks.LAPIS_MANALIS));
	}

	public static @NotNull @Unmodifiable Map<BlockState, Recipe> getRecipes() {
		return Collections.unmodifiableMap(recipes);
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

				for (ItemStack s : recipe.output) {
					ItemEntity itemEntity = new ItemEntity(level,
							focusPos.getX() + .5, focusPos.getY() + .5, focusPos.getZ() + .5,
							s.copy());
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

	public record Recipe(int processTicks, @NotNull @Unmodifiable List<ItemStack> output) {
		public Recipe(int processTicks, ItemLike itemLike) {
			this(processTicks, List.of(new ItemStack(itemLike)));
		}
	}
}
