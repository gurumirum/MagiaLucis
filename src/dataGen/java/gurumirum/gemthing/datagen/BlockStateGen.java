package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.NormalOres;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static gurumirum.gemthing.GemthingMod.MODID;

public class BlockStateGen extends BlockStateProvider {
	public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(ModBlocks.SILVER.block());
		simpleBlock(ModBlocks.RAW_SILVER_BLOCK.block());

		registerOre(NormalOres.SILVER);
	}

	private void registerOre(NormalOres ore){
		if (ore.hasOre()) simpleBlock(ore.ore());
		if (ore.hasDeepslateOre()) simpleBlock(ore.deepslateOre());
	}
}
