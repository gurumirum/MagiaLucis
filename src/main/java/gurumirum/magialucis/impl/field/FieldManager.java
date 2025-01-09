package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.MagiaLucisMod;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class FieldManager extends SavedData {
	private static final SavedData.Factory<FieldManager> FACTORY = new Factory<>(FieldManager::new,
			(tag, provider) -> new FieldManager());
	private static final String NAME = MagiaLucisMod.MODID + "_fields";

	public static @Nullable FieldManager tryGet(@Nullable Level level) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
	}

	public static @NotNull FieldManager get(@NotNull ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, NAME);
	}

	public static @Nullable FieldInstance tryGetField(@Nullable Level level, Field field) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel).getOrCreate(field) : null;
	}

	private final Map<Field, FieldInstance> fields = new Object2ObjectOpenHashMap<>();

	private FieldManager() {}

	public @Nullable FieldInstance get(Field field) {
		return this.fields.get(field);
	}

	public @NotNull FieldInstance getOrCreate(Field field) {
		return this.fields.computeIfAbsent(field, Field::createInstance);
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		return tag;
	}

	@Override
	public void save(@NotNull File file, HolderLookup.@NotNull Provider registries) {}

	void update() {
		this.fields.values().forEach(FieldInstance::update);
	}
}
