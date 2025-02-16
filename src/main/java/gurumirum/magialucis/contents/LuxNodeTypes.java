package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBehavior;
import gurumirum.magialucis.contents.block.lux.charger.ChargerTier;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBehavior;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.core.MoonlightCoreBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.core.SunlightCoreBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.focus.SunlightFocusBehavior;
import gurumirum.magialucis.api.luxnet.behavior.DefaultLuxNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class LuxNodeTypes {
	private LuxNodeTypes() {}

	public static final LuxNodeType<DefaultLuxNodeBehavior> DEFAULT = type("default",
			DefaultLuxNodeBehavior.class, LuxNodeBehavior::none);

	public static final LuxNodeType<DynamicLuxNodeBehavior> DYNAMIC = type("dynamic",
			DynamicLuxNodeBehavior.class, DynamicLuxNodeBehavior::save, DynamicLuxNodeBehavior::new);

	public static final LuxNodeType<AmberCoreBehavior> AMBER_CORE = type("amber_core",
			AmberCoreBehavior.class, AmberCoreBehavior::save, AmberCoreBehavior::new);

	public static final LuxNodeType<SunlightCoreBehavior> SUNLIGHT_CORE = type("sunlight_core",
			SunlightCoreBehavior.class, SunlightCoreBehavior::save, SunlightCoreBehavior::new);

	public static final LuxNodeType<MoonlightCoreBehavior> MOONLIGHT_CORE = type("moonlight_core",
			MoonlightCoreBehavior.class, MoonlightCoreBehavior::save, MoonlightCoreBehavior::new);

	public static final LuxNodeType<SunlightFocusBehavior> SUNLIGHT_FOCUS = type("sunlight_focus",
			SunlightFocusBehavior.class, SunlightFocusBehavior::save, SunlightFocusBehavior::new);

	public static final LuxNodeType<LightBasinBehavior> LIGHT_BASIN = type("light_basin",
			LightBasinBehavior.class, LightBasinBehavior::new);

	public static final LuxNodeType<LuxSourceBehavior> SOURCE = type("source",
			LuxSourceBehavior.class, LuxSourceBehavior::new);

	static {
		for (ChargerTier chargerTier : ChargerTier.values()) {
			Contents.LUX_NODE_TYPES.register(
					chargerTier.chargerBehaviorType(false).id().getPath(),
					() -> chargerTier.chargerBehaviorType(false));
			Contents.LUX_NODE_TYPES.register(
					chargerTier.chargerBehaviorType(true).id().getPath(),
					() -> chargerTier.chargerBehaviorType(true));
		}

		for (LightLoomType type : LightLoomType.values()) {
			Contents.LUX_NODE_TYPES.register(
					type.behaviorType().id().getPath(),
					type::behaviorType);
		}
	}

	public static void init() {}

	private static <B extends LuxNodeBehavior> LuxNodeType<B> type(
			@NotNull String id, @NotNull Class<B> type,
			@NotNull Supplier<B> provider) {
		LuxNodeType<B> luxNodeType = new LuxNodeType.Simple<>(id(id), type, provider);
		Contents.LUX_NODE_TYPES.register(id, () -> luxNodeType);
		return luxNodeType;
	}

	private static <B extends LuxNodeBehavior> LuxNodeType<B> type(
			@NotNull String id, @NotNull Class<B> type,
			@NotNull TriConsumer<B, CompoundTag, HolderLookup.Provider> writeFunc,
			@NotNull BiFunction<CompoundTag, HolderLookup.Provider, B> readFunc) {
		LuxNodeType<B> luxNodeType = new LuxNodeType.Serializable<>(id(id), type, writeFunc, readFunc);
		Contents.LUX_NODE_TYPES.register(id, () -> luxNodeType);
		return luxNodeType;
	}
}
