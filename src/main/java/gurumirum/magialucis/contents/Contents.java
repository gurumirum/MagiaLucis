package gurumirum.magialucis.contents;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.contents.block.lux.relay.RelayItemData;
import gurumirum.magialucis.contents.entity.GemGolemEntity;
import gurumirum.magialucis.contents.entity.LesserIceProjectile;
import gurumirum.magialucis.contents.entity.EnderChestPortal;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltMenu;
import gurumirum.magialucis.contents.mobeffect.DoubleMagicDamageMobEffect;
import gurumirum.magialucis.contents.mobeffect.RecallFatigueMobEffect;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

import static gurumirum.magialucis.MagiaLucisMod.MODID;

public final class Contents {
	private Contents() {}

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

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> FIELD_ID = DATA_COMPONENTS.register("field_id",
			() -> DataComponentType.<ResourceLocation>builder()
					.persistent(ResourceLocation.CODEC)
					.networkSynchronized(ResourceLocation.STREAM_CODEC)
					.build());

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

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PORTAL_UUID = DATA_COMPONENTS.register("portal_uuid",
			() -> DataComponentType.<UUID>builder()
					.persistent(UUIDUtil.CODEC)
					.networkSynchronized(UUIDUtil.STREAM_CODEC)
					.build());

	public static final DeferredHolder<MenuType<?>, MenuType<WandBeltMenu>> WANG_BELT_MENU = MENUS.register("wand_belt",
			() -> new MenuType<>(WandBeltMenu::new, FeatureFlagSet.of()));

	public static final DeferredHolder<MobEffect, MobEffect> RECALL_FATIGUE = MOB_EFFECTS.register("recall_fatigue",
			RecallFatigueMobEffect::new);

	public static final DeferredHolder<MobEffect, MobEffect> DOUBLE_MAGIC_DAMAGE = MOB_EFFECTS.register("double_magic_damage",
			DoubleMagicDamageMobEffect::new);

	public static final DeferredHolder<EntityType<?>, EntityType<GemGolemEntity>> GEM_GOLEM = ENTITY_TYPES.register("gem_golem",
			() -> EntityType.Builder.of(GemGolemEntity::new, MobCategory.MONSTER)
					.sized(1.4F, 2.7F)
					.clientTrackingRange(10)
					.build("gem_golem"));

	public static final DeferredHolder<EntityType<?>, EntityType<LesserIceProjectile>> LESSER_ICE_PROJECTILE = ENTITY_TYPES.register("lesser_ice_projectile",
			() -> EntityType.Builder.<LesserIceProjectile>of(LesserIceProjectile::new, MobCategory.MISC)
					.sized(0.25f, 0.25f)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build("lesser_ice_projectile"));

	public static final DeferredHolder<EntityType<?>, EntityType<EnderChestPortal>> ENDER_CHEST_PORTAL = ENTITY_TYPES.register("ender_chest_portal",
			() -> EntityType.Builder.<EnderChestPortal>of(EnderChestPortal::new, MobCategory.MISC)
					.sized(.75f, .75f)
					.clientTrackingRange(10)
					.updateInterval(10)
					.build("ender_chest_portal"));

	public static void init(IEventBus eventBus) {
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

		GemItems.init();
		ModItems.init();
		ModBlocks.init();
		ModBuildingBlocks.init();
		ModBlockEntities.init();
		Ore.init();
		Wands.init();
		CreativeTabType.init();
		ModRecipes.init();
	}
}
