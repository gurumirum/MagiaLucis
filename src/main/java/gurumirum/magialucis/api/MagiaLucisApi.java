package gurumirum.magialucis.api;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class MagiaLucisApi {
	private MagiaLucisApi(){}

	public static final String MODID = "magialucis";

	@NotNull
	public static ResourceLocation id(@NotNull String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
}
