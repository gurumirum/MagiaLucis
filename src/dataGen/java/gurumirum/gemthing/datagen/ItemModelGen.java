package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.Gems;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.NormalOres;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

public class ItemModelGen extends ItemModelProvider {
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		handheld(ModItems.WAND.asItem())
				.override()
				.predicate(ResourceLocation.withDefaultNamespace("using"), 1)
				.model(getBuilder(ModItems.WAND.id().getPath() + "_using")
						.parent(new ModelFile.UncheckedModelFile(ModItems.WAND.id().withPrefix("item/")))
						.transforms()
						.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
						.rotation(-128.5f, 90, 0)
						.translation(-7, 2.2f, 1.13f)
						.scale(0.68f, 0.68f, 0.68f)
						.end()
						.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
						.rotation(-128.5f, -90, 0)
						.translation(-7, 2.2f, 1.13f)
						.scale(0.68f, 0.68f, 0.68f)
						.end()
						.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
						.rotation(-37.5f, -90, 0)
						.translation(0, -.5f, -5.75f)
						.scale(0.85f, 0.85f, 0.85f)
						.end()
						.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
						.rotation(-37.5f, 90, 0)
						.translation(0, -.5f, -5.75f)
						.scale(0.85f, 0.85f, 0.85f)
						.end()
						.end());

		basicItem(ModItems.SILVER_INGOT.asItem());
		basicItem(ModItems.SILVER_NUGGET.asItem());
		basicItem(ModItems.RAW_SILVER.asItem());
		basicItem(ModItems.WRENCH_WAND.asItem());

		basicItem(Gems.BRIGHTSTONE.asItem());

		basicItem(Gems.AMBER.asItem());
		basicItem(Gems.CITRINE.asItem());
		basicItem(Gems.AQUAMARINE.asItem());
		basicItem(Gems.PEARL.asItem());

		basicItem(Gems.PURIFIED_QUARTZ.asItem());
		basicItem(Gems.CRYSTALLIZED_REDSTONE.asItem());
		basicItem(Gems.POLISHED_LAPIS_LAZULI.asItem());
		basicItem(Gems.OBSIDIAN.asItem());

		registerOreModels(NormalOres.SILVER);
		itemBlock(ModBlocks.SILVER.id().getPath(), "silver");
		itemBlock(ModBlocks.RAW_SILVER_BLOCK.id().getPath());
		itemBlock(ModBlocks.RELAY.id().getPath());
	}

	@SuppressWarnings("UnusedReturnValue")
	private ItemModelBuilder handheld(Item item) {
		return handheld(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
	}

	private ItemModelBuilder handheld(ResourceLocation item) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
	}

	private void registerOreModels(NormalOres ore) {
		if (ore.hasOre()) itemBlock(ore.oreId());
		if (ore.hasDeepslateOre()) itemBlock(ore.deepslateOreId());
	}

	private ItemModelBuilder itemBlock(String id) {
		return itemBlock(id, id);
	}
	private ItemModelBuilder itemBlock(String id, String modelPath) {
		return getBuilder(id).parent(new ModelFile.UncheckedModelFile(id("block/" + modelPath)));
	}
}
