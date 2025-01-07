package gurumirum.gemthing.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record InWorldLinkState(boolean linked, @NotNull InWorldLinkInfo info) {
	public InWorldLinkState(boolean linked, @NotNull BlockPos origin, @NotNull Vec3 linkLocation) {
		this(linked, new InWorldLinkInfo(origin, linkLocation));
	}

	public InWorldLinkState(CompoundTag tag) {
		this(tag.getBoolean("linked"), new InWorldLinkInfo(tag));
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("linked", this.linked);
		this.info.save(tag);
		return tag;
	}
}
