package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.Contents;
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

public class ItemModelGen extends ItemModelProvider {
	public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		handheld(Contents.Items.WAND.asItem())
				.override()
				.predicate(ResourceLocation.withDefaultNamespace("using"), 1)
				.model(getBuilder(Contents.Items.WAND.id().getPath() + "_using")
						.parent(new ModelFile.UncheckedModelFile(Contents.Items.WAND.id().withPrefix("item/")))
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
		basicItem(Contents.Items.GEM.asItem());
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
}
