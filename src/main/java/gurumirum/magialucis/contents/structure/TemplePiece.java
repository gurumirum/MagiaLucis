package gurumirum.magialucis.contents.structure;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.ModEntities;
import gurumirum.magialucis.contents.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class TemplePiece extends TemplateStructurePiece {
	private static final ResourceLocation STRUCTURE = MagiaLucisMod.id("temple");
	private static final BlockPos PIVOT = new BlockPos(9 / 2, 11 / 2, 13 / 2);

	private static final int BASE_FILL_HEIGHT = 20;

	public TemplePiece(StructureTemplateManager manager, BlockPos pos, Rotation rotation) {
		super(ModStructures.TEMPLE_PIECE_TYPE.get(), 0, manager, STRUCTURE, STRUCTURE.toString(),
				makeSettings(rotation), pos);
	}

	public TemplePiece(StructureTemplateManager manager, CompoundTag tag) {
		super(ModStructures.TEMPLE_PIECE_TYPE.get(), tag, manager,
				p -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
	}

	private static StructurePlaceSettings makeSettings(Rotation rotation) {
		return new StructurePlaceSettings()
				.setRotation(rotation)
				.setMirror(Mirror.NONE)
				.setRotationPivot(PIVOT)
				.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
				.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tag) {
		super.addAdditionalSaveData(context, tag);
		tag.putString("Rot", this.placeSettings.getRotation().name());
	}

	@Override
	protected void handleDataMarker(@NotNull String name, @NotNull BlockPos pos,
	                                @NotNull ServerLevelAccessor level, @NotNull RandomSource random,
	                                @NotNull BoundingBox box) {
		if ("guardian".equals(name)) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			var templeGuardian = ModEntities.TEMPLE_GUARDIAN.get().create(level.getLevel());
			if (templeGuardian != null) {
				templeGuardian.moveTo(pos, 0, 0);
				EventHooks.finalizeMobSpawn(templeGuardian, level,
						level.getCurrentDifficultyAt(templeGuardian.blockPosition()),
						MobSpawnType.STRUCTURE, null);
				level.addFreshEntityWithPassengers(templeGuardian);
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			}
		}
	}

	@Override
	public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager,
	                        @NotNull ChunkGenerator generator, @NotNull RandomSource random, @NotNull BoundingBox box,
	                        @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
		super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);

		MutableBlockPos mpos = new MutableBlockPos();
		box = getBoundingBox();

		for (int x = box.minX(); x <= box.maxX(); x++) {
			for (int z = box.minZ(); z <= box.maxZ(); z++) {
				for (int y = 0; y < BASE_FILL_HEIGHT; y++) {
					if (level.isOutsideBuildHeight(y) || !level.getBlockState(mpos.set(x, box.minY() - 1 - y, z)).canBeReplaced()) {
						for (int y2 = 0; y2 < y; y2++) {
							level.setBlock(mpos.set(x, box.minY() - 1 - y2, z),
									ModBuildingBlocks.LAPIS_MANALIS.block().defaultBlockState(),
									3);
						}
						break;
					}
				}
			}
		}
	}
}
