package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.structure.TemplePiece;
import gurumirum.magialucis.contents.structure.TempleStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModStructures {
	private ModStructures() {}

	public static final DeferredHolder<StructureType<?>, StructureType<TempleStructure>> TEMPLE_STRUCTURE_TYPE =
			Contents.STRUCTURE_TYPES.register("temple", () -> () -> TempleStructure.CODEC);

	public static final DeferredHolder<StructurePieceType, StructurePieceType.StructureTemplateType> TEMPLE_PIECE_TYPE =
			Contents.STRUCTURE_PIECE_TYPES.register("temple", () -> TemplePiece::new);

	public static void init() {}
}
