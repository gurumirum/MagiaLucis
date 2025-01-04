package gurumirum.gemthing.contents;

import gurumirum.gemthing.contents.block.lux.relay.RelayBlock;
import gurumirum.gemthing.contents.block.AmberLightBlock;
import gurumirum.gemthing.contents.block.lux.remotecharger.RemoteChargerBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ModBlocks implements ItemLike {
	SILVER(BlockProfile.block(Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	RAW_SILVER_BLOCK(BlockProfile.block(Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK))),

	AMBER_LIGHT(BlockProfile.customBlockWithoutItem(AmberLightBlock::new, Properties.of().lightLevel(s -> 15)
			.replaceable()
			.noLootTable()
			.noCollission()
			.noOcclusion())),

	REMOTE_CHARGER(BlockProfile.customBlock(RemoteChargerBlock::new, Properties.of())),
	RELAY(BlockProfile.customBlock(RelayBlock::new, Properties.of()));

	private final DeferredBlock<? extends Block> block;
	@Nullable
	private final DeferredItem<? extends BlockItem> item;

	ModBlocks(@NotNull BlockProfile<Block, BlockItem> blockProfile) {
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
}
