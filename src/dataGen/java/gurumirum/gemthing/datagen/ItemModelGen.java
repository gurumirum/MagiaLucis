package gurumirum.gemthing.datagen;

import gurumirum.gemthing.client.ClientInit;
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
		var beamChannelWand = getBuilder("beam_channel_wand")
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
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
				.end();

		var passiveChannelWand = getBuilder("passive_channel_wand")
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.transforms()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
				.rotation(4.86f, 13.85f, -44.18f)
				.translation(-3.75f, 6.2f, 1.13f)
				.scale(0.68f, 0.68f, 0.68f)
				.end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
				.rotation(4.86f, -13.85f, 44.18f)
				.translation(-3.75f, 6.2f, 1.13f)
				.scale(0.68f, 0.68f, 0.68f)
				.end()
				.end();

		handheld(Wands.ANCIENT_LIGHT.asItem())
				.override()
				.predicate(ClientInit.USING, 1)
				.model(getBuilder(Wands.ANCIENT_LIGHT.id().getPath() + "_using")
						.parent(beamChannelWand)
						.texture("layer0", Wands.ANCIENT_LIGHT.id().withPrefix("item/")));

		handheld(Wands.CONFIGURATION_WAND.asItem());

		handheld(Wands.AMBER_TORCH.asItem())
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.AMBER_TORCH.id().getPath() + "_no_charge")));

		handheld(Wands.RECALL_STAFF.asItem())
				.override()
				.predicate(ClientInit.USING, 1)
				.model(getBuilder(Wands.RECALL_STAFF.id().getPath() + "_using")
						.parent(passiveChannelWand)
						.texture("layer0", Wands.RECALL_STAFF.id().withPrefix("item/")))
				.end()
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.RECALL_STAFF.id().getPath() + "_no_charge")));

		handheld(Wands.HEAL_WAND.asItem())
				.override()
				.predicate(ClientInit.USING, 1)
				.model(getBuilder(Wands.HEAL_WAND.id().getPath() + "_using")
						.parent(passiveChannelWand)
						.texture("layer0", Wands.HEAL_WAND.id().withPrefix("item/")))
				.end()
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.HEAL_WAND.id().getPath() + "_no_charge")));

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
		itemBlock(ModBlocks.RELAY.id().getPath());
	}

	@SuppressWarnings("UnusedReturnValue")
	private ItemModelBuilder handheld(Item item) {
		return handheld(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
	}

	private ItemModelBuilder handheld(ResourceLocation item) {
		return handheld(item, item.withPrefix("item/"));
	}

	private ItemModelBuilder handheld(ResourceLocation item, ResourceLocation texture) {
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/handheld"))
				.texture("layer0", texture);
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
