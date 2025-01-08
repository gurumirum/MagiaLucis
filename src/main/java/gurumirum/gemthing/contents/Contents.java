package gurumirum.gemthing.contents;

import com.mojang.serialization.Codec;
import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.contents.block.lux.relay.RelayItemData;
import gurumirum.gemthing.contents.entity.GemGolemEntity;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltMenu;
import gurumirum.gemthing.contents.mobeffect.RecallFatigueMobEffect;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

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
	static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<GemGolemEntity>> GEM_GOLEM = ENTITY_TYPES.register("gem_golem",
			() -> EntityType.Builder.of(GemGolemEntity::new, MobCategory.MONSTER)
					.sized(1.4F, 2.7F)
					.clientTrackingRange(10)
					.build("gem_golem"));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<RelayItemData>> RELAY_ITEM = DATA_COMPONENTS.register("relay_item",
			() -> DataComponentType.<RelayItemData>builder()
					.persistent(ItemStack.CODEC.xmap(RelayItemData::new, RelayItemData::stack))
					.networkSynchronized(ItemStack.STREAM_CODEC.map(RelayItemData::new, RelayItemData::stack))
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> LINK_SOURCE = DATA_COMPONENTS.register("link_source",
			() -> DataComponentType.<GlobalPos>builder()
					.persistent(GlobalPos.CODEC)
					.networkSynchronized(GlobalPos.STREAM_CODEC)
					.build());

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

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);
		BLOCK_ENTITIES.register(eventBus);
		MENUS.register(eventBus);
		MOB_EFFECTS.register(eventBus);
		PLACED_FEATURES.register(eventBus);
		ENTITY_TYPES.register(eventBus);

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

								if (i == ModBlocks.RELAY) {
									for (GemStats g : GemStats.values()) {
										g.forEachItem(item -> {
											ItemStack stack = new ItemStack(ModBlocks.RELAY.asItem());
											stack.set(RELAY_ITEM, new RelayItemData(new ItemStack(item)));
											o.accept(stack);
										});
									}
								}
							}
						})
						.build());

				h.register(id("gems"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(GemItems.BRIGHTSTONE))
						.displayItems((p, o) -> {
							for (var ore : Ore.values()) ore.allOreItems().forEach(o::accept);
							for (var g : GemStats.values()) g.forEachItem(o::accept);
						})
						.build());
			});
		});

		eventBus.addListener((EntityAttributeCreationEvent event) -> {
			event.put(Contents.GEM_GOLEM.get(), GemGolemEntity.createAttributes().build());
		});

		GemItems.init();
		ModItems.init();
		ModBlocks.init();
		ModBlockEntities.init();
		Ore.init();
		Wands.init();
	}
}
