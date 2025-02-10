package gurumirum.magialucis.impl.luxnet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record InWorldLinkState(boolean linked, int weight, @NotNull InWorldLinkInfo info) {
	public InWorldLinkState(boolean linked, int weight, @NotNull BlockPos origin, @NotNull BlockPos linkPos, @NotNull Vec3 linkLocation) {
		this(linked, weight, new InWorldLinkInfo(origin, linkPos, linkLocation));
	}

	public InWorldLinkState(CompoundTag tag) {
		this(tag.getBoolean("linked"),
				tag.contains("weight", CompoundTag.TAG_INT) ? Math.max(0, tag.getInt("weight")) : 1,
				new InWorldLinkInfo(tag));
	}

	public @NotNull BlockPos origin() {
		return this.info.origin();
	}

	public @NotNull BlockPos linkPos() {
		return this.info.linkPos();
	}

	public @NotNull Vec3 linkLocation() {
		return this.info.linkLocation();
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("linked", this.linked);
		if (this.weight != 1) tag.putInt("weight", this.weight);
		this.info.save(tag);
		return tag;
	}
}
