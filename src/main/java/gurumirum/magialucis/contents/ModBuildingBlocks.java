package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.PillarOrnamentBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModBuildingBlocks implements ItemLike, BlockProvider {
	LAPIS_MANALIS(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).isValidSpawn(Blocks::never))),
	LAPIS_MANALIS_BRICKS(BlockProfile.block(lapisManalis())),

	LAPIS_MANALIS_PILLAR(BlockProfile.customBlock(RotatedPillarBlock::new, lapisManalis())),

	LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC(BlockProfile.customBlock(PillarOrnamentBlock::top, lapisManalis())),
	LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC(BlockProfile.customBlock(PillarOrnamentBlock::top, lapisManalis())),
	LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN(BlockProfile.customBlock(PillarOrnamentBlock::top, lapisManalis())),
	LAPIS_MANALIS_PILLAR_BASE_DORIC(BlockProfile.customBlock(PillarOrnamentBlock::bottom, lapisManalis())),
	LAPIS_MANALIS_PILLAR_BASE_IONIC(BlockProfile.customBlock(PillarOrnamentBlock::bottom, lapisManalis())),

	LAPIS_MANALIS_SLAB(BlockProfile.customBlock(SlabBlock::new, lapisManalis())),
	LAPIS_MANALIS_BRICKS_SLAB(BlockProfile.customBlock(SlabBlock::new, lapisManalis())),
	LAPIS_MANALIS_STAIRS(BlockProfile.customBlock(p -> new StairBlock(LAPIS_MANALIS.block().defaultBlockState(), p), lapisManalis())),
	LAPIS_MANALIS_BRICKS_STAIRS(BlockProfile.customBlock(p -> new StairBlock(LAPIS_MANALIS_BRICKS.block().defaultBlockState(), p), lapisManalis())),

	SILVER_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	RAW_SILVER_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)));

	private final DeferredBlock<? extends Block> block;
	@Nullable
	private final DeferredItem<? extends BlockItem> item;

	ModBuildingBlocks(@NotNull BlockProfile<Block, BlockItem> blockProfile) {
		String id = name().toLowerCase(Locale.ROOT);
		this.block = blockProfile.create(id);
		this.item = blockProfile.createItem(this.block);
	}

	public @NotNull ResourceLocation id() {
		return this.block.getId();
	}

	public @NotNull Block block() {
		return this.block.get();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item != null ? this.item.get() : net.minecraft.world.item.Items.AIR;
	}

	public @Nullable BlockItem blockItem() {
		return this.item != null ? this.item.get() : null;
	}

	public static void init() {}

	private static Supplier<BlockBehaviour.Properties> lapisManalis() {
		return () -> BlockBehaviour.Properties.ofFullCopy(ModBuildingBlocks.LAPIS_MANALIS.block());
	}
}
