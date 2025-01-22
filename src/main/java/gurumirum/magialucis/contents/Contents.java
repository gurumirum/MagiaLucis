package gurumirum.magialucis.contents;

import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

public final class Contents {
	private Contents() {}

	public static final ResourceKey<Registry<LuxNodeType<?>>> LUX_NODE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(id("lux_node_type"));

	static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
	static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
	static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
	static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
	static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
	static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
	static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

	static final DeferredRegister<LuxNodeType<?>> LUX_NODE_TYPES = DeferredRegister.create(LUX_NODE_TYPE_REGISTRY_KEY, MODID);

	private static @Nullable Registry<LuxNodeType<?>> luxNodeTypeRegistry;

	public static @NotNull Registry<LuxNodeType<?>> luxNodeTypeRegistry() {
		return Objects.requireNonNull(luxNodeTypeRegistry, "Registry not initialized");
	}

	public static void init(IEventBus eventBus) {
		eventBus.addListener((NewRegistryEvent event) -> {
			luxNodeTypeRegistry = event.create(new RegistryBuilder<>(Contents.LUX_NODE_TYPE_REGISTRY_KEY));
		});

		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);

		BLOCK_ENTITIES.register(eventBus);
		CREATIVE_MODE_TABS.register(eventBus);
		ENTITY_TYPES.register(eventBus);
		MENUS.register(eventBus);
		MOB_EFFECTS.register(eventBus);
		RECIPE_SERIALIZERS.register(eventBus);
		RECIPE_TYPES.register(eventBus);

		LUX_NODE_TYPES.register(eventBus);

		Accessories.init();
		CreativeTabType.init();
		GemItems.init();
		ModBlockEntities.init();
		ModBlocks.init();
		ModBuildingBlocks.init();
		ModDataComponents.init();
		ModEntities.init();
		ModItems.init();
		ModMenus.init();
		ModMobEffects.init();
		ModRecipes.init();
		Ore.init();
		Wands.init();

		LuxNodeTypes.init();
	}
}
