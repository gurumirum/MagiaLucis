package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.block.AmberLanternBlock;
import gurumirum.magialucis.contents.block.AmberLightBlock;
import gurumirum.magialucis.contents.block.BaseLanternBlock;
import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableBlock;
import gurumirum.magialucis.contents.block.fieldmonitor.FieldMonitorBlock;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlock;
import gurumirum.magialucis.contents.block.lux.charger.ChargerBlock;
import gurumirum.magialucis.contents.block.lux.charger.ChargerTier;
import gurumirum.magialucis.contents.block.lux.charger.RemoteChargerBlock;
import gurumirum.magialucis.contents.block.lux.connector.ConnectorBlock;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlock;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomBaseBlock;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomBlock;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.data.GemItemData;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlock;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlock;
import gurumirum.magialucis.contents.block.lux.splitter.SplitterBlock;
import gurumirum.magialucis.contents.block.lux.sunlight.core.MoonlightCoreBlock;
import gurumirum.magialucis.contents.block.lux.sunlight.core.SunlightCoreBlock;
import gurumirum.magialucis.contents.block.lux.sunlight.focus.SunlightFocusBlock;
import gurumirum.magialucis.contents.profile.BlockProfile;
import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.field.FieldRegistry;
import gurumirum.magialucis.utils.BlockProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ModBlocks implements ItemLike, BlockProvider {
	AMBER_LIGHT(BlockProfile.customBlockWithoutItem(AmberLightBlock::new, Properties.of()
			.lightLevel(s -> 15)
			.sound(SoundType.WOOL)
			.replaceable()
			.noLootTable()
			.noCollission()
			.noOcclusion()), null),

	RELAY(BlockProfile.customBlock(RelayBlock::new, Properties.of()
			.strength(1)
			.sound(SoundType.GLASS)), CreativeTabType.TRANSMITTERS),

	SPLITTER(BlockProfile.customBlock(SplitterBlock::new, Properties.of()
			.noOcclusion()
			.strength(2.5f)
			.sound(SoundType.WOOD)), CreativeTabType.TRANSMITTERS),

	CONNECTOR(BlockProfile.customBlock(ConnectorBlock::new, Properties.of()
			.noOcclusion()
			.strength(2.5f)
			.sound(SoundType.WOOD)), CreativeTabType.TRANSMITTERS),

	AMBER_CORE(BlockProfile.customBlock(AmberCoreBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD)
			.lightLevel(AmberCoreBlock::getLightValue))),

	AMBER_CHARGER(BlockProfile.customBlock(p -> new ChargerBlock(p, ChargerTier.PRIMITIVE), Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD))),

	AMBER_LANTERN(BlockProfile.customBlock(AmberLanternBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD)
			.lightLevel(state -> 15))),

	SUNLIGHT_CORE(BlockProfile.customBlock(SunlightCoreBlock::new, Properties.of()
			.noOcclusion()
			.strength(2.5f)
			.sound(SoundType.GLASS))),
	MOONLIGHT_CORE(BlockProfile.customBlock(MoonlightCoreBlock::new, Properties.of()
			.noOcclusion()
			.strength(2.5f)
			.sound(SoundType.GLASS))),

	SUNLIGHT_FOCUS(BlockProfile.customBlock(SunlightFocusBlock::new, Properties.of()
			.requiresCorrectToolForDrops()
			.strength(3.5f)
			.sound(SoundType.STONE))),

	ARTISANRY_TABLE(BlockProfile.customBlock(ArtisanryTableBlock::new, Properties.of()
			.strength(2.5f)
			.sound(SoundType.WOOD))),

	LIGHTLOOM_BASE(BlockProfile.customBlock(LightLoomBaseBlock::new, lightloom())),
	CITRINE_LIGHTLOOM(BlockProfile.customBlock(p -> new LightLoomBlock(p, LightLoomType.CITRINE), lightloom())),
	IOLITE_LIGHTLOOM(BlockProfile.customBlock(p -> new LightLoomBlock(p, LightLoomType.IOLITE), lightloom())),

	LIGHT_BASIN(BlockProfile.customBlock(LightBasinBlock::new, ModBuildingBlocks.lapisManalis())),

	LUMINOUS_CHARGER(BlockProfile.customBlock(p -> new ChargerBlock(p, ChargerTier.LUMINOUS), Properties.of()
			.requiresCorrectToolForDrops()
			.strength(2.5f))),

	LUMINOUS_LANTERN_BASE(BlockProfile.customBlock(BaseLanternBlock.Stateless::new, Properties.of()
			.requiresCorrectToolForDrops()
			.strength(2.5f))),

	LUMINOUS_RESONANCE_LANTERN(BlockProfile.customBlock(p -> new RemoteChargerBlock(p, ChargerTier.LUMINOUS), Properties.of()
			.requiresCorrectToolForDrops()
			.strength(2.5f)
			.lightLevel(state -> state.getValue(BlockStateProperties.ENABLED) ? 15 : 0))),

	LUSTROUS_RESONANCE_LANTERN(BlockProfile.customBlock(p -> new RemoteChargerBlock(p, ChargerTier.LUSTROUS), Properties.of()
			.requiresCorrectToolForDrops()
			.strength(2.5f)
			.lightLevel(state -> state.getValue(BlockStateProperties.ENABLED) ? 15 : 0))),

	LUX_SOURCE(BlockProfile.customBlock(p -> new LuxSourceBlock(p,
			Gem.BRIGHTSTONE, 10), Properties.of().strength(2.5f))),
	LUX_SOURCE_2(BlockProfile.customBlock(p -> new LuxSourceBlock(p,
			LuxStat.simple(0, 100000, 100000, 100000), 100000), Properties.of().strength(2.5f))),

	FIELD_MONITOR(BlockProfile.customBlock(FieldMonitorBlock::new, Properties.of().strength(1)));

	private final DeferredBlock<? extends Block> block;
	private final @Nullable DeferredItem<? extends BlockItem> item;
	private final @Nullable CreativeTabType tab;

	ModBlocks(@NotNull BlockProfile<Block, BlockItem> blockProfile) {
		this(blockProfile, CreativeTabType.MECHANISMS);
	}

	ModBlocks(@NotNull BlockProfile<Block, BlockItem> blockProfile, @Nullable CreativeTabType tab) {
		String id = name().toLowerCase(Locale.ROOT);
		this.block = blockProfile.create(id);
		this.item = blockProfile.createItem(this.block);
		this.tab = tab;
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

	public @Nullable CreativeTabType getCreativeTab() {
		return this.tab;
	}

	public void addItem(CreativeModeTab.Output o) {
		switch (this) {
			case RELAY, SPLITTER, CONNECTOR -> {
				o.accept(this);
				for (Gem g : Gem.values()) {
					g.forEachItem(item -> {
						ItemStack stack = new ItemStack(this);
						stack.set(ModDataComponents.GEM_ITEM, new GemItemData(new ItemStack(item)));
						o.accept(stack);
					});
				}
			}
			case FIELD_MONITOR -> {
				for (Field f : FieldRegistry.fields().values()) {
					ItemStack stack = new ItemStack(this);
					stack.set(ModDataComponents.FIELD_ID, f.id());
					o.accept(stack);
				}
			}
			case AMBER_LIGHT -> {}
			default -> o.accept(this);
		}
	}

	public static void init() {}

	private static Properties lightloom() {
		return Properties.of()
				.requiresCorrectToolForDrops()
				.strength(2.5f)
				.sound(SoundType.METAL);
	}
}
