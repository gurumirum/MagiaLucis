package gurumirum.magialucis.api;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class MagiaLucisRegistries {
	private MagiaLucisRegistries() {}

	public static final ResourceKey<Registry<Augment>> AUGMENT = ResourceKey.createRegistryKey(id("augment"));
	public static final ResourceKey<Registry<LuxNodeType<?>>> LUX_NODE_TYPE = ResourceKey.createRegistryKey(id("lux_node_type"));

	private static @Nullable Registry<Augment> augmentRegistry;
	private static @Nullable Registry<LuxNodeType<?>> luxNodeTypeRegistry;

	public static @NotNull Registry<Augment> augmentRegistry() {
		return Objects.requireNonNull(augmentRegistry, "Registry not initialized");
	}

	public static @NotNull Registry<LuxNodeType<?>> luxNodeTypeRegistry() {
		return Objects.requireNonNull(luxNodeTypeRegistry, "Registry not initialized");
	}

	public static boolean initialized() {
		return augmentRegistry != null;
	}

	@ApiStatus.Internal
	public static void init(Registry<Augment> augmentRegistry, Registry<LuxNodeType<?>> luxNodeTypeRegistry) {
		MagiaLucisRegistries.augmentRegistry = augmentRegistry;
		MagiaLucisRegistries.luxNodeTypeRegistry = luxNodeTypeRegistry;
	}
}
