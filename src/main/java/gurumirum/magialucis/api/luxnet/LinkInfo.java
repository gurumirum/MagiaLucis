package gurumirum.magialucis.api.luxnet;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public record LinkInfo(int weight, @Nullable InWorldLinkInfo inWorld) {
	public LinkInfo(CompoundTag tag) {
		this(tag.contains("weight", CompoundTag.TAG_INT) ? Math.max(0, tag.getInt("weight")) : 1,
				tag.contains("info", CompoundTag.TAG_COMPOUND) ? new InWorldLinkInfo(tag.getCompound("info")) : null);
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		save(tag);
		return tag;
	}

	public void save(CompoundTag tag) {
		if (this.weight != 1) tag.putInt("weight", this.weight);
		if (this.inWorld != null) tag.put("info", this.inWorld.save());
	}
}
