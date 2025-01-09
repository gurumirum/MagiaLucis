package gurumirum.magialucis.contents;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public enum OreType {
	STONE,
	DEEPSLATE,
	NETHER,
	END;

	public String getOreId(String oreBaseName) {
		return switch (this) {
			case STONE -> oreBaseName + "_ore";
			case DEEPSLATE -> "deepslate_" + oreBaseName + "_ore";
			case NETHER -> "nether_" + oreBaseName + "_ore";
			case END -> "end_" + oreBaseName + "_ore";
		};
	}

	public BlockBehaviour.Properties getBlockProperties() {
		return switch (this) {
			case STONE -> BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE);
			case DEEPSLATE -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_GOLD_ORE);
			case NETHER -> BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_GOLD_ORE);
			case END -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(4.5f, 6)
			;
		};
	}
}
