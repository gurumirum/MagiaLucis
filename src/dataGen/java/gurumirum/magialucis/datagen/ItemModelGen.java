package gurumirum.magialucis.datagen;

import gurumirum.magialucis.client.ClientInit;
import gurumirum.magialucis.contents.*;
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

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

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
		handheld(Wands.RED_CONFIGURATION_WAND.asItem());
		handheld(Wands.ICY_CONFIGURATION_WAND.asItem());

		handheld(Wands.AMBER_TORCH.asItem())
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.AMBER_TORCH.id().getPath() + "_no_charge")));

		handheld(Wands.LESSER_ICE_STAFF.asItem())
				.override()
				.predicate(ClientInit.USING, 1)
				.model(getBuilder(Wands.LESSER_ICE_STAFF.id().getPath() + "_using")
						.parent(beamChannelWand) // TODO new transform?
						.texture("layer0", Wands.LESSER_ICE_STAFF.id().withPrefix("item/")))
				.end()
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.LESSER_ICE_STAFF.id().getPath() + "_no_charge")));

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

		basicItem(Wands.LAPIS_SHIELD.asItem())
				.transforms()
				.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
				.rotation(0, -90, 0)
				.translation(2, -3, 2)
				.scale(0.625f)
				.end()
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
				.rotation(0, -90, 0)
				.translation(2, -3, 2)
				.scale(0.625f)
				.end()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
				.rotation(0, -45, 0)
				.translation(0, 1, 2)
				.scale(0.75f)
				.end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
				.rotation(0, -45, 0)
				.translation(0, 1, 2)
				.scale(0.75f)
				.end()
				.end()
				.override()
				.predicate(ClientInit.USING, 1)
				.model(getBuilder(Wands.LAPIS_SHIELD.id().getPath() + "_using")
						.parent(new ModelFile.UncheckedModelFile(Wands.LAPIS_SHIELD.id().withPrefix("item/")))
						.transforms()
						.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
						.rotation(0, 5, 0)
						.translation(-2, 3, 2)
						.scale(0.75f)
						.end()
						.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
						.rotation(0, 5, 0)
						.translation(-2, 3, 2)
						.scale(0.75f)
						.end()
						.end())
				.end()
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(getBuilder(Wands.LAPIS_SHIELD.id().getPath() + "_no_charge")
						.parent(new ModelFile.UncheckedModelFile(Wands.LAPIS_SHIELD.id().withPrefix("item/")))
						.texture("layer0", Wands.LAPIS_SHIELD.id().withPrefix("item/").withSuffix("_no_charge")));

		handheld(Wands.DIAMOND_MACE.asItem())
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.DIAMOND_MACE.id().getPath() + "_no_charge")));

		handheld(Wands.ENDER_WAND.asItem())
				.override()
				.predicate(ClientInit.NO_CHARGE, 1)
				.model(handheld(id(Wands.ENDER_WAND.id().getPath() + "_no_charge")));

		basicItem(Accessories.WAND_BELT.asItem());

		basicItem(ModItems.ANCIENT_CORE.asItem());

		basicItem(ModItems.COPPER_NUGGET.asItem());

		basicItem(ModItems.SILVER_INGOT.asItem());
		basicItem(ModItems.SILVER_NUGGET.asItem());
		basicItem(ModItems.RAW_SILVER.asItem());

		basicItem(ModItems.ELECTRUM_INGOT.asItem());
		basicItem(ModItems.ELECTRUM_NUGGET.asItem());
		basicItem(ModItems.ROSE_GOLD_INGOT.asItem());
		basicItem(ModItems.ROSE_GOLD_NUGGET.asItem());
		basicItem(ModItems.STERLING_SILVER_INGOT.asItem());
		basicItem(ModItems.STERLING_SILVER_NUGGET.asItem());

		spawnEggItem(ModItems.TEMPLE_GUARDIAN_SPAWN_EGG.asItem());

		for (GemItems i : GemItems.values()) basicItem(i.asItem());

		for (Ore ore : Ore.values()) registerOreModels(ore);
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
