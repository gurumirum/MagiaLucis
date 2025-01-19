package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.*;
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

		curio(ModCurioSlots.WAND_BELT).add(ModItems.WAND_BELT.asItem());

		for (var i : Accessories.values()) {
			curio(i.curioSlot()).add(i.asItem());
		}

		tag(ModItemTags.COPPER_NUGGETS).add(ModItems.COPPER_NUGGET.asItem());

		tag(ModItemTags.SILVER_INGOTS).add(ModItems.SILVER_INGOT.asItem());
		tag(ModItemTags.SILVER_NUGGETS).add(ModItems.SILVER_NUGGET.asItem());
		tag(ModItemTags.SILVER_RAW_MATERIALS).add(ModItems.RAW_SILVER.asItem());

		tag(ModItemTags.ELECTRUM_INGOTS).add(ModItems.ELECTRUM_INGOT.asItem());
		tag(ModItemTags.ELECTRUM_NUGGETS).add(ModItems.ELECTRUM_NUGGET.asItem());
		tag(ModItemTags.ROSE_GOLD_INGOTS).add(ModItems.ROSE_GOLD_INGOT.asItem());
		tag(ModItemTags.ROSE_GOLD_NUGGETS).add(ModItems.ROSE_GOLD_NUGGET.asItem());
		tag(ModItemTags.STERLING_SILVER_INGOTS).add(ModItems.STERLING_SILVER_INGOT.asItem());
		tag(ModItemTags.STERLING_SILVER_NUGGETS).add(ModItems.STERLING_SILVER_NUGGET.asItem());

		for (Ore ore : Ore.values()) copyCommonTag("ores/" + ore.oreId());
		copyTag(ModBlockTags.SILVER_BLOCKS.location());
		copyTag(ModBlockTags.RAW_SILVER_BLOCKS.location());
		copyTag(ModBlockTags.ELECTRUM_BLOCKS.location());
		copyTag(ModBlockTags.ROSE_GOLD_BLOCKS.location());
		copyTag(ModBlockTags.STERLING_SILVER_BLOCKS.location());

		var gems = tag(Tags.Items.GEMS);
		for (GemItems gem : GemItems.values()) {
			if (gem.gem.hasTag() && !gem.gem.isVanilla()) {
				tag(gem.gem.tag()).add(gem.asItem());
				gems.add(gem.asItem());
			}
		}

		tag(ModItemTags.BRIGHTSTONES).add(GemItems.BRIGHTSTONE.asItem(), GemItems.RED_BRIGHTSTONE.asItem(), GemItems.ICY_BRIGHTSTONE.asItem());
		copy(ModBlockTags.LAPIDES_MANALIS, ModItemTags.LAPIDES_MANALIS);
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
