package gurumirum.magialucis.contents;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.AmberLightBlock;
import gurumirum.magialucis.contents.block.fieldmonitor.FieldMonitorBlock;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlock;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlock;
import gurumirum.magialucis.contents.block.lux.relay.RelayItemData;
import gurumirum.magialucis.contents.block.lux.remotecharger.RemoteChargerBlock;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlock;
import gurumirum.magialucis.contents.block.sunlight.core.MoonlightCoreBlock;
import gurumirum.magialucis.contents.block.sunlight.core.SunlightCoreBlock;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusBlock;
import gurumirum.magialucis.impl.RGB332;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.FieldRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ModBlocks implements ItemLike, BlockProvider {
	AMBER_LIGHT(BlockProfile.customBlockWithoutItem(AmberLightBlock::new, Properties.of().lightLevel(s -> 15)
			.sound(SoundType.WOOL)
			.replaceable()
			.noLootTable()
			.noCollission()
			.noOcclusion())),

	REMOTE_CHARGER(BlockProfile.customBlock(RemoteChargerBlock.Basic::new, Properties.of().strength(2.5f))),
	REMOTE_CHARGER_2(BlockProfile.customBlock(RemoteChargerBlock.Advanced::new, Properties.of().strength(2.5f))),
	RELAY(BlockProfile.customBlock(RelayBlock::new, Properties.of()
			.strength(1)
			.sound(SoundType.GLASS))),
	AMBER_CORE(BlockProfile.customBlock(AmberCoreBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD)
			.lightLevel(AmberCoreBlock::getLightValue))),
	SUNLIGHT_CORE(BlockProfile.customBlock(SunlightCoreBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.GLASS))),
	MOONLIGHT_CORE(BlockProfile.customBlock(MoonlightCoreBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.GLASS))),
	SUNLIGHT_FOCUS(BlockProfile.customBlock(SunlightFocusBlock::new, Properties.of()
			.requiresCorrectToolForDrops()
			.strength(3.5f)
			.sound(SoundType.STONE))),

	LUX_SOURCE(BlockProfile.customBlock(p -> new LuxSourceBlock(p,
			GemStats.BRIGHTSTONE, 10), Properties.of().strength(2.5f))),
	LUX_SOURCE_2(BlockProfile.customBlock(p -> new LuxSourceBlock(p,
			LuxStat.simple(RGB332.WHITE, 0, 100000), 100000), Properties.of().strength(2.5f))),

	FIELD_MONITOR(BlockProfile.customBlock(FieldMonitorBlock::new, Properties.of().strength(1)));

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

	public void addItem(CreativeModeTab.Output o) {
		switch (this) {
			case RELAY -> {
				o.accept(this);
				for (GemStats g : GemStats.values()) {
					g.forEachItem(item -> {
						ItemStack stack = new ItemStack(this);
						stack.set(Contents.RELAY_ITEM, new RelayItemData(new ItemStack(item)));
						o.accept(stack);
					});
				}
			}
			case FIELD_MONITOR -> {
				for (Field f : FieldRegistry.fields().values()) {
					ItemStack stack = new ItemStack(this);
					stack.set(Contents.FIELD_ID, f.id);
					o.accept(stack);
				}
			}
			case AMBER_LIGHT -> {}
			default -> o.accept(this);
		}
	}

	public static void init() {}
}
