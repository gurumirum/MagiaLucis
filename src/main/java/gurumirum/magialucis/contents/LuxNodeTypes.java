package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.luxnet.behavior.DefaultLuxNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBehavior;
import gurumirum.magialucis.contents.block.lux.charger.ChargerTier;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlock;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.core.MoonlightCoreBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.core.SunlightCoreBehavior;
import gurumirum.magialucis.contents.block.lux.sunlight.focus.SunlightFocusBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.api.MagiaLucisApi.MODID;

public final class LuxNodeTypes {
	private LuxNodeTypes() {}

	public static void init() {
		register(DefaultLuxNodeBehavior.NODE_TYPE);

		register(AmberCoreBehavior.NODE_TYPE);
		register(DynamicLuxNodeBehavior.NODE_TYPE);
		register(LightBasinBlock.NODE_TYPE);
		register(SunlightCoreBehavior.NODE_TYPE);
		register(MoonlightCoreBehavior.NODE_TYPE);
		register(SunlightFocusBehavior.NODE_TYPE);
		register(LuxSourceBehavior.NODE_TYPE);

		for (ChargerTier chargerTier : ChargerTier.values()) {
			register(chargerTier.chargerBehaviorType(false));
			register(chargerTier.chargerBehaviorType(true));
		}

		for (LightLoomType type : LightLoomType.values()) {
			register(type.behaviorType());
		}
	}

	private static void register(@NotNull LuxNodeType<?> type) {
		if (!MODID.equals(type.id().getNamespace())) {
			throw new IllegalArgumentException("Trying to register lux node type " + type.id() + " to registry " + MODID);
		}
		Contents.LUX_NODE_TYPES.register(type.id().getPath(), () -> type);
	}
}
