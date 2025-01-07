package gurumirum.gemthing.impl;

import gurumirum.gemthing.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record InWorldLinkInfo(@NotNull BlockPos origin, @NotNull Vec3 linkLocation) {
	public InWorldLinkInfo(CompoundTag tag) {
		this(NbtUtils.readBlockPos(tag, "origin").orElse(BlockPos.ZERO),
				TagUtils.readVec3(tag, "linkLocation"));
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		save(tag);
		return tag;
	}

	public void save(CompoundTag tag) {
		tag.put("origin", NbtUtils.writeBlockPos(this.origin));
		TagUtils.writeVec3(tag, "linkLocation", this.linkLocation);
	}
}
