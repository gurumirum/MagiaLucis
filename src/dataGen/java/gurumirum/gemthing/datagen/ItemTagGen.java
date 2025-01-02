package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.ModItemTags;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.NormalOres;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider {
	public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
	                  CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, GemthingMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(ModItemTags.WANDS).add(ModItems.WAND.asItem());

		c("ingots/silver").add(ModItems.SILVER_INGOT.asItem());
		c("nuggets/silver").add(ModItems.SILVER_NUGGET.asItem());
		c("raw_materials/silver").add(ModItems.RAW_SILVER.asItem());

		for (NormalOres ore : NormalOres.values()) copyCommonTag("ores/" + ore.oreId());
		copyCommonTag("storage_blocks/silver");
		copyCommonTag("storage_blocks/raw_silver");
	}

	private IntrinsicTagAppender<Item> c(String path) {
		return tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path)));
	}

	private void copyCommonTag(String path) {
		copyTag(ResourceLocation.fromNamespaceAndPath("c", path));
	}

	private void copyTag(ResourceLocation tag) {
		copy(BlockTags.create(tag), ItemTags.create(tag));
	}
}
