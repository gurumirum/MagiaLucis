package gurumirum.magialucis.client;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.resources.ResourceLocation;

public final class Textures {
	private Textures() {}

	public static final ResourceLocation CITRINE_MATRIX = MagiaLucisApi.id("block/matrix/citrine");
	public static final ResourceLocation IOLITE_MATRIX = MagiaLucisApi.id("block/matrix/iolite");

	public static final ResourceLocation AUGMENT_ATLAS = MagiaLucisApi.id("textures/atlas/augments.png");
	public static final ResourceLocation AUGMENT_ATLAS_INFO = MagiaLucisApi.id("augments");

	public static final ResourceLocation AUGMENT_COLLECTOR = MagiaLucisApi.id("collector");
	public static final ResourceLocation AUGMENT_CONCEAL = MagiaLucisApi.id("conceal");
	public static final ResourceLocation AUGMENT_DEBUG_VIEW = MagiaLucisApi.id("debug_view");
}
