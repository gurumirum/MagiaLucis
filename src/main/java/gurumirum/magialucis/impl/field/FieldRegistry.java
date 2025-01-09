package gurumirum.magialucis.impl.field;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public final class FieldRegistry {
	private static final Map<ResourceLocation, Field> fields = new Object2ObjectOpenHashMap<>();
	private static final Map<ResourceLocation, Field> fieldsView = Collections.unmodifiableMap(fields);

	public static Map<ResourceLocation, Field> fields() {
		return fieldsView;
	}

	public static @NotNull Field register(@NotNull Field field) {
		if (fields.putIfAbsent(field.id, field) != null) {
			throw new IllegalStateException("Duplicated registration of field " + field.id);
		}
		return field;
	}
}
