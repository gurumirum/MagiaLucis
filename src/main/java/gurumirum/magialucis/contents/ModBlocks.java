package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.AmberLightBlock;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlock;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlock;
import gurumirum.magialucis.contents.block.lux.remotecharger.RemoteChargerBlock;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlock;
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

public enum ModBlocks implements ItemLike, BlockProvider {
	SILVER(BlockProfile.block(Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL))),
	RAW_SILVER_BLOCK(BlockProfile.block(Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK))),

	AMBER_LIGHT(BlockProfile.customBlockWithoutItem(AmberLightBlock::new, Properties.of().lightLevel(s -> 15)
			.replaceable()
			.noLootTable()
			.noCollission()
			.noOcclusion())),

	REMOTE_CHARGER(BlockProfile.customBlock(RemoteChargerBlock.Basic::new, Properties.of())),
	REMOTE_CHARGER_2(BlockProfile.customBlock(RemoteChargerBlock.Advanced::new, Properties.of())),
	RELAY(BlockProfile.customBlock(RelayBlock::new, Properties.of())),
	AMBER_CORE(BlockProfile.customBlock(AmberCoreBlock::new, Properties.of().lightLevel(state -> 9))),
	LUX_SOURCE(BlockProfile.customBlock(LuxSourceBlock::new, Properties.of()));

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
