package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.Ore;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

public class BlockStateGen extends BlockStateProvider {
	public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(ModBlocks.SILVER.block());
		simpleBlock(ModBlocks.RAW_SILVER_BLOCK.block());

		directionalBlock(ModBlocks.RELAY.block(), models().getExistingFile(id("block/relay")));

		for (Ore ore : Ore.values()) ore.allOreBlocks().forEach(this::simpleBlock);
	}
}
