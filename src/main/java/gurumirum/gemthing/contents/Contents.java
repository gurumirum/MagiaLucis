package gurumirum.gemthing.contents;

import com.mojang.serialization.Codec;
import gurumirum.gemthing.capability.Gems;
import gurumirum.gemthing.contents.block.RemoteChargerBlockEntity;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltMenu;
import gurumirum.gemthing.contents.mobeffect.RecallFatigueMobEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiFunction;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

public final class Contents {
	private Contents() {}

	static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
	static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
	static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
	static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
	static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LUX_CHARGE = DATA_COMPONENTS.register("lux_charge",
			() -> DataComponentType.<Long>builder()
					.persistent(Codec.LONG)
					.networkSynchronized(ByteBufCodecs.VAR_LONG)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> WAND_BELT_SELECTED_INDEX = DATA_COMPONENTS.register("wand_belt_selected_index",
			() -> DataComponentType.<Byte>builder()
					.persistent(Codec.BYTE)
					.networkSynchronized(ByteBufCodecs.BYTE)
					.build());

	public static final DeferredHolder<MenuType<?>, MenuType<WandBeltMenu>> WANG_BELT_MENU = MENUS.register("wand_belt",
			() -> new MenuType<>(WandBeltMenu::new, FeatureFlagSet.of()));

	public static final DeferredHolder<MobEffect, MobEffect> RECALL_FATIGUE = MOB_EFFECTS.register("recall_fatigue",
			RecallFatigueMobEffect::new);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER = BLOCK_ENTITIES.register("remote_charger",
			() -> BlockEntityType.Builder.of(RemoteChargerBlockEntity::new, ModBlocks.REMOTE_CHARGER.block())
					.build(null));

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);
		BLOCK_ENTITIES.register(eventBus);
		MENUS.register(eventBus);
		MOB_EFFECTS.register(eventBus);
		PLACED_FEATURES.register(eventBus);

		eventBus.addListener((RegisterEvent event) -> {
			event.register(Registries.CREATIVE_MODE_TAB, h -> {
				h.register(id("main"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(Wands.ANCIENT_LIGHT))
						.displayItems((p, o) -> {
							for (var i : Wands.values()) {
								o.accept(i);
								if (i.luxContainerStat() != null) {
									ItemStack stack = new ItemStack(i);
									stack.set(LUX_CHARGE, i.luxContainerStat().maxCharge());
									o.accept(stack);
								}
							}
							for (var i : ModItems.values()) o.accept(i);
							for (var i : ModBlocks.values()) {
								if (i.blockItem() != null) o.accept(i);
							}
						})
						.build());

				h.register(id("gems"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(GemItems.BRIGHTSTONE))
						.displayItems((p, o) -> {
							for (var ore : Ore.values()) ore.allOreItems().forEach(o::accept);
							for (var g : Gems.values()) {
								o.accept(g.item());
								if (g == Gems.BRIGHTSTONE) o.accept(GemItems.RED_BRIGHTSTONE);
							}
						})
						.build());
			});
		});

		GemItems.init();
		ModItems.init();
		ModBlocks.init();
		Ore.init();
		Wands.init();
	}

	static final BiFunction<Block, Item.Properties, BlockItem> defaultItemFactory = BlockItem::new;
}
