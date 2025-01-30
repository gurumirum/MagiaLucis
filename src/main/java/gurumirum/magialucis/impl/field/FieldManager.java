package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.MagiaLucisMod;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FieldManager extends SavedData {
	private static final SavedData.Factory<FieldManager> FACTORY = new Factory<>(FieldManager::new, FieldManager::new);
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
		ListTag list = new ListTag();
		for (var e : this.fields.entrySet()) {
			if (e.getValue().isEmpty()) continue;
			CompoundTag tag2 = new CompoundTag();
			e.getValue().save(tag2);
			tag2.putString("id", e.getKey().id.toString());
			list.add(tag2);
		}
		tag.put("fields", list);
		return tag;
	}

	private FieldManager(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
		ListTag list = tag.getList("fields", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag2 = list.getCompound(i);

			String idString = tag2.getString("id");
			ResourceLocation id = ResourceLocation.tryParse(idString);

			if (id == null) {
				MagiaLucisMod.LOGGER.info("Cannot parse field with invalid id '{}'", idString);
				continue;
			}

			Field field = FieldRegistry.fields().get(id);
			if (field == null) {
				MagiaLucisMod.LOGGER.info("Cannot find field with id '{}'", id);
				continue;
			}

			this.fields.put(field, field.createInstance(tag2));
		}
	}

	void update() {
		this.fields.values().forEach(FieldInstance::update);
	}
}
