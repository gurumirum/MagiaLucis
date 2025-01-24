package gurumirum.magialucis.contents.structure;

import com.mojang.serialization.MapCodec;
import gurumirum.magialucis.contents.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TempleStructure extends Structure {
	public static final MapCodec<TempleStructure> CODEC = simpleCodec(TempleStructure::new);

	public TempleStructure(Structure.StructureSettings settings) {
		super(settings);
	}

	@Override
	public @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.@NotNull GenerationContext context) {
		return onTopOfChunkCenter(context,
				Heightmap.Types.WORLD_SURFACE_WG,
				b -> generatePieces(b, context));
	}

	private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();

		int y = Integer.MIN_VALUE;

		for (int x = 1; x < 4; x++) {
			for (int z = 1; z < 4; z++) {
				y = sampleHeight(context, x * 4, z * 4);
			}
		}

		BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + 4, y - 1, chunkPos.getMinBlockZ() + 4);
		builder.addPiece(new TemplePiece(context.structureTemplateManager(), pos, Rotation.getRandom(context.random())));
	}

	private static int sampleHeight(Structure.GenerationContext context, int x, int z) {
		return context.chunkGenerator().getBaseHeight(
				context.chunkPos().getMinBlockX() + x,
				context.chunkPos().getMinBlockZ() + z,
				Heightmap.Types.WORLD_SURFACE_WG,
				context.heightAccessor(),
				context.randomState()
		);
	}

	@Override
	public @NotNull StructureType<?> type() {
		return ModStructures.TEMPLE_STRUCTURE_TYPE.get();
	}
}
