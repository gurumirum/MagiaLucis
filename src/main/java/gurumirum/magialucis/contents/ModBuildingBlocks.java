package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.PillarOrnamentBlock;
import gurumirum.magialucis.contents.profile.BlockProfile;
import gurumirum.magialucis.utils.BlockProvider;
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
	LAPIS_MANALIS(BlockProfile.block(lapisManalis())),
	LAPIS_MANALIS_BRICKS(BlockProfile.block(lapisManalis())),

	LAPIS_MANALIS_PILLAR(BlockProfile.customBlock(RotatedPillarBlock::new, lapisManalis())),

	LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC(BlockProfile.customBlock(p -> new PillarOrnamentBlock(p, true, PillarOrnamentBlock.OrnamentType.DORIC), lapisManalis())),
	LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC(BlockProfile.customBlock(p -> new PillarOrnamentBlock(p, true, PillarOrnamentBlock.OrnamentType.IONIC), lapisManalis())),
	LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN(BlockProfile.customBlock(p -> new PillarOrnamentBlock(p, true, PillarOrnamentBlock.OrnamentType.CORINTHIAN), lapisManalis())),
	LAPIS_MANALIS_PILLAR_BASE_DORIC(BlockProfile.customBlock(p -> new PillarOrnamentBlock(p, false, PillarOrnamentBlock.OrnamentType.DORIC), lapisManalis())),
	LAPIS_MANALIS_PILLAR_BASE_IONIC(BlockProfile.customBlock(p -> new PillarOrnamentBlock(p, false, PillarOrnamentBlock.OrnamentType.IONIC_CORINTHIAN), lapisManalis())),

	LAPIS_MANALIS_SLAB(BlockProfile.customBlock(SlabBlock::new, lapisManalis())),
	LAPIS_MANALIS_BRICK_SLAB(BlockProfile.customBlock(SlabBlock::new, lapisManalis())),
	LAPIS_MANALIS_STAIRS(BlockProfile.customBlock(p -> new StairBlock(LAPIS_MANALIS.block().defaultBlockState(), p), lapisManalis())),
	LAPIS_MANALIS_BRICK_STAIRS(BlockProfile.customBlock(p -> new StairBlock(LAPIS_MANALIS_BRICKS.block().defaultBlockState(), p), lapisManalis())),

	SILVER_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	RAW_SILVER_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK))),

	ELECTRUM_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	ROSE_GOLD_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	STERLING_SILVER_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),

	LUMINOUS_ALLOY_BLOCK(BlockProfile.block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
			.lightLevel(state -> 10))),
	;

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

	static Supplier<BlockBehaviour.Properties> lapisManalis() {
		return () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).isValidSpawn(Blocks::never);
	}
}
