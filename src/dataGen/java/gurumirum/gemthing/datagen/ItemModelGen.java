package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Map;
import java.util.Objects;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

public class ItemModelGen extends ItemModelProvider {
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		handheld(Wands.ANCIENT_LIGHT.asItem())
				.override()
				.predicate(ResourceLocation.withDefaultNamespace("using"), 1)
				.model(getBuilder(Wands.ANCIENT_LIGHT.id().getPath() + "_using")
						.parent(new ModelFile.UncheckedModelFile(Wands.ANCIENT_LIGHT.id().withPrefix("item/")))
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

		handheld(Wands.AMBER_TORCH.asItem())
				.override()
				.predicate(ResourceLocation.withDefaultNamespace("no_charge"), 1)
				.model(getBuilder(Wands.AMBER_TORCH.id().getPath() + "_no_charge")
						.parent(new ModelFile.UncheckedModelFile(Wands.AMBER_TORCH.id().withPrefix("item/")))
						.texture("layer0", id("item/amber_torch_no_charge")));

		basicItem(ModItems.SILVER_INGOT.asItem());
		basicItem(ModItems.SILVER_NUGGET.asItem());
		basicItem(ModItems.RAW_SILVER.asItem());

		basicItem(GemItems.BRIGHTSTONE.asItem());
		basicItem(GemItems.RED_BRIGHTSTONE.asItem());
		basicItem(GemItems.SOUL_BRIGHTSTONE.asItem());

		basicItem(GemItems.AMBER.asItem());
		basicItem(GemItems.CITRINE.asItem());
		basicItem(GemItems.AQUAMARINE.asItem());
		basicItem(GemItems.PEARL.asItem());

		basicItem(GemItems.PURIFIED_QUARTZ.asItem());
		basicItem(GemItems.CRYSTALLIZED_REDSTONE.asItem());
		basicItem(GemItems.POLISHED_LAPIS_LAZULI.asItem());
		basicItem(GemItems.OBSIDIAN.asItem());

		basicItem(GemItems.TOPAZ.asItem());
		basicItem(GemItems.MOONSTONE.asItem());
		basicItem(GemItems.JET.asItem());
		basicItem(GemItems.RUBY.asItem());
		basicItem(GemItems.SAPPHIRE.asItem());

		for (Ore ore : Ore.values()) registerOreModels(ore);
		itemBlock(ModBlocks.SILVER.id().getPath(), "silver");
		itemBlock(ModBlocks.RAW_SILVER_BLOCK.id().getPath());
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

	private void registerOreModels(Ore ore) {
		ore.entries()
				.map(Map.Entry::getValue)
				.forEach(p -> {
					itemBlock(p.getSecond().getId().getPath(), p.getFirst().getId().getPath());
				});
	}

	private ItemModelBuilder itemBlock(String id) {
		return itemBlock(id, id);
	}
	private ItemModelBuilder itemBlock(String id, String modelPath) {
		return getBuilder(id).parent(new ModelFile.UncheckedModelFile(id("block/" + modelPath)));
	}
}
