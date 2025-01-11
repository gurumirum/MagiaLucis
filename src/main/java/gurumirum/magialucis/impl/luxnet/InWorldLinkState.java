package gurumirum.magialucis.impl.luxnet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record InWorldLinkState(boolean linked, @NotNull InWorldLinkInfo info) {
	public InWorldLinkState(boolean linked, @NotNull BlockPos origin, @NotNull BlockPos linkPos, @NotNull Vec3 linkLocation) {
		this(linked, new InWorldLinkInfo(origin, linkPos, linkLocation));
	}

	public InWorldLinkState(CompoundTag tag) {
		this(tag.getBoolean("linked"), new InWorldLinkInfo(tag));
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
		this.info.save(tag);
		return tag;
	}
}
