package gurumirum.gemthing.contents;

import com.mojang.serialization.Codec;
import gurumirum.gemthing.capability.GemStat;
import gurumirum.gemthing.contents.block.RemoteChargerBlockEntity;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
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
	static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registries.PLACED_FEATURE, MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LUX_CHARGE = DATA_COMPONENTS.register("lux_charge",
			() -> DataComponentType.<Long>builder()
					.persistent(Codec.LONG)
					.networkSynchronized(ByteBufCodecs.VAR_LONG)
					.build());

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER = BLOCK_ENTITIES.register("remote_charger",
			() -> BlockEntityType.Builder.of(RemoteChargerBlockEntity::new, ModBlocks.REMOTE_CHARGER.block())
					.build(null));

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);
		BLOCK_ENTITIES.register(eventBus);
		PLACED_FEATURES.register(eventBus);

		eventBus.addListener((RegisterEvent event) -> {
			event.register(Registries.CREATIVE_MODE_TAB, h -> {
				h.register(id("main"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(ModItems.WAND))
						.displayItems((p, o) -> {
							for (var i : ModItems.values()) o.accept(i);
							for (var i : ModBlocks.values()) o.accept(i);
						})
						.build());

				h.register(id("gems"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(Gems.BRIGHTSTONE))
						.displayItems((p, o) -> {
							for (var ore : NormalOres.values()) {
								if (ore.hasOre()) o.accept(ore.oreItem());
								if (ore.hasDeepslateOre()) o.accept(ore.deepslateOreItem());
							}
							for (var g : GemStat.values()) o.accept(g.item());
						})
						.build());
			});
		});

		Gems.init();
		ModItems.init();
		ModBlocks.init();
		NormalOres.init();
	}

	static final BiFunction<Block, Item.Properties, BlockItem> defaultItemFactory = BlockItem::new;
}
