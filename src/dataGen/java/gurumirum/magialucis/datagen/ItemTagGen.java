package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.*;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider {
	public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
	                  CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, MagiaLucisMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(ModItemTags.WANDS).add(Arrays.stream(Wands.values()).map(Wands::asItem).toArray(Item[]::new));

		curio(WandBeltItem.CURIO_SLOT).add(ModItems.WAND_BELT.asItem());

		c("ingots/silver").add(ModItems.SILVER_INGOT.asItem());
		c("nuggets/silver").add(ModItems.SILVER_NUGGET.asItem());
		c("raw_materials/silver").add(ModItems.RAW_SILVER.asItem());

		for (Ore ore : Ore.values()) copyCommonTag("ores/" + ore.oreId());
		copyCommonTag("storage_blocks/silver");
		copyCommonTag("storage_blocks/raw_silver");

		var gems = tag(Tags.Items.GEMS);
		for (GemItems gem : GemItems.values()) {
			if (gem.gem.hasTag()) {
				tag(gem.gem.tag()).add(gem.asItem());
				gems.add(gem.asItem());
			}
		}

		tag(ModItemTags.BRIGHTSTONES).add(GemItems.BRIGHTSTONE.asItem(), GemItems.RED_BRIGHTSTONE.asItem(), GemItems.ICY_BRIGHTSTONE.asItem());
		copy(ModBlockTags.LAPIDES_MANALIS, ModItemTags.LAPIDES_MANALIS);
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

	private IntrinsicTagAppender<Item> curio(String curioIdentifier) {
		return tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, curioIdentifier)));
	}
}
