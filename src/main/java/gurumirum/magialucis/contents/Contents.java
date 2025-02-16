package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gurumirum.magialucis.api.MagiaLucisApi.MODID;
import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class Contents {
	private Contents() {}

	public static final ResourceKey<Registry<Augment>> AUGMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(id("augment"));
	public static final ResourceKey<Registry<LuxNodeType<?>>> LUX_NODE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(id("lux_node_type"));

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	public static final DeferredRegister<Augment> AUGMENTS = DeferredRegister.create(AUGMENT_REGISTRY_KEY, MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
	public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, MODID);
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

	public static final DeferredRegister<LuxNodeType<?>> LUX_NODE_TYPES = DeferredRegister.create(LUX_NODE_TYPE_REGISTRY_KEY, MODID);

	private static @Nullable Registry<Augment> augmentRegistry;
	private static @Nullable Registry<LuxNodeType<?>> luxNodeTypeRegistry;

	public static @NotNull Registry<Augment> augmentRegistry() {
		return Objects.requireNonNull(augmentRegistry, "Registry not initialized");
	}

	public static @NotNull Registry<LuxNodeType<?>> luxNodeTypeRegistry() {
		return Objects.requireNonNull(luxNodeTypeRegistry, "Registry not initialized");
	}

	public static void init(IEventBus eventBus) {
		eventBus.addListener((NewRegistryEvent event) -> {
			augmentRegistry = event.create(new RegistryBuilder<>(Contents.AUGMENT_REGISTRY_KEY).sync(true));
			luxNodeTypeRegistry = event.create(new RegistryBuilder<>(Contents.LUX_NODE_TYPE_REGISTRY_KEY));
		});

		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);

		AUGMENTS.register(eventBus);
		BLOCK_ENTITIES.register(eventBus);
		CREATIVE_MODE_TABS.register(eventBus);
		ENTITY_TYPES.register(eventBus);
		MENUS.register(eventBus);
		MOB_EFFECTS.register(eventBus);
		PARTICLES.register(eventBus);
		RECIPE_SERIALIZERS.register(eventBus);
		RECIPE_TYPES.register(eventBus);
		STRUCTURE_PIECE_TYPES.register(eventBus);
		STRUCTURE_TYPES.register(eventBus);

		LUX_NODE_TYPES.register(eventBus);

		Accessories.init();
		Augments.init();
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
		ModParticles.init();
		ModRecipes.init();
		ModStructures.init();
		Ore.init();
		Wands.init();

		LuxNodeTypes.init();
	}
}
