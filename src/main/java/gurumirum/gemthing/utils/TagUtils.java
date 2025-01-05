package gurumirum.gemthing.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.joml.Vector3d;

public final class TagUtils {
	private TagUtils() {}

	public static void writeVector3d(CompoundTag tag, String fieldName, Vector3d vector3d) {
		if (vector3d.x != 0 || vector3d.y != 0 || vector3d.z != 0) {
			CompoundTag tag2 = new CompoundTag();
			tag2.putDouble("x", vector3d.x);
			tag2.putDouble("y", vector3d.y);
			tag2.putDouble("z", vector3d.z);
			tag.put(fieldName, tag2);
		}
	}

	public static void readVector3d(CompoundTag tag, String fieldName, Vector3d vector3d) {
		if (tag.contains(fieldName, Tag.TAG_COMPOUND)) {
			CompoundTag tag2 = tag.getCompound(fieldName);
			vector3d.set(tag2.getDouble("x"),
					tag2.getDouble("y"),
					tag2.getDouble("z"));
		} else vector3d.zero();
	}
}
