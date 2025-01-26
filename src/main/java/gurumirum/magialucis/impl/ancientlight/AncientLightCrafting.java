package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.net.msgs.SyncAncientLightProgressMsg;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class AncientLightCrafting {
	private AncientLightCrafting() {}

	private static final Map<ServerLevel, ServerAncientLightManager> managers = new Object2ObjectOpenHashMap<>();
	private static final Map<BlockState, AncientLightRecipe> recipes = new Object2ObjectOpenHashMap<>();

	private static @Nullable LocalAncientLightManager localManager;

	private static boolean init;

	public static void init() {
		if (init) return;
		init = true;

		addRecipe(Blocks.SAND, new AncientLightRecipe(25, GemItems.BRIGHTSTONE));
		addRecipe(Blocks.RED_SAND, new AncientLightRecipe(25, GemItems.RED_BRIGHTSTONE));

		addRecipe(Blocks.ICE, new AncientLightRecipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.FROSTED_ICE, new AncientLightRecipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.BLUE_ICE, new AncientLightRecipe(25, GemItems.ICY_BRIGHTSTONE));
		addRecipe(Blocks.PACKED_ICE, new AncientLightRecipe(25, GemItems.ICY_BRIGHTSTONE));

		addRecipe(Blocks.SOUL_SAND, new AncientLightRecipe(50, GemItems.SOUL_BRIGHTSTONE));

		addRecipe(Blocks.STONE, new AncientLightRecipe(50, ModBuildingBlocks.LAPIS_MANALIS));
		addRecipe(Blocks.DEEPSLATE, new AncientLightRecipe(50, ModBuildingBlocks.LAPIS_MANALIS));
	}

	public static @NotNull @Unmodifiable Map<BlockState, AncientLightRecipe> getRecipes() {
		return Collections.unmodifiableMap(recipes);
	}

	private static void addRecipe(BlockState blockState, AncientLightRecipe recipe) {
		recipes.put(blockState, recipe);
	}

	private static void addRecipe(Block block, AncientLightRecipe recipe) {
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			recipes.put(state, recipe);
		}
	}

	public static @Nullable ServerAncientLightManager tryGetManager(Level level) {
		return level instanceof ServerLevel serverLevel ? getManager(serverLevel) : null;
	}

	public static @NotNull ServerAncientLightManager getManager(ServerLevel level) {
		return managers.computeIfAbsent(level, l -> new ServerAncientLightManager());
	}

	public static @NotNull LocalAncientLightManager getLocalManager() {
		if (localManager == null) localManager = new LocalAncientLightManager();
		return localManager;
	}

	public static @Nullable LocalAncientLightManager tryGetLocalManager() {
		return localManager;
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		managers.clear();
	}

	@SuppressWarnings("SuspiciousMethodCalls") // wdym
	@SubscribeEvent
	public static void afterLevelTick(LevelTickEvent.Post event) {
		Level level = event.getLevel();
		if (level.isClientSide) {
			if (localManager != null) localManager.update(level);
		} else {
			ServerAncientLightManager updater = managers.get(level);
			if (updater != null) updater.update(level);
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // wdym
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		managers.remove(event.getLevel());
	}

	@SubscribeEvent
	public static void onChunkSent(ChunkWatchEvent.Sent event) {
		ServerAncientLightManager updater = managers.get(event.getLevel());
		if (updater == null) return;
		AncientLightRecord record = updater.lastSyncedRecord();
		if (record == null) return;
		SyncAncientLightProgressMsg msg = record.createUpdatePacket(event.getChunk().getPos().toLong());
		if (msg != null) PacketDistributor.sendToPlayer(event.getPlayer(), msg);
	}
}
