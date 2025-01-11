package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record InWorldLinkInfo(@NotNull BlockPos origin, @NotNull BlockPos linkPos, @NotNull Vec3 linkLocation) {
	public InWorldLinkInfo(CompoundTag tag) {
		this(NbtUtils.readBlockPos(tag, "origin").orElse(BlockPos.ZERO),
				NbtUtils.readBlockPos(tag, "linkPos").orElse(BlockPos.ZERO),
				TagUtils.readVec3(tag, "linkLocation"));
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		save(tag);
		return tag;
	}

	public void save(CompoundTag tag) {
		tag.put("origin", NbtUtils.writeBlockPos(this.origin));
		tag.put("linkPos", NbtUtils.writeBlockPos(this.linkPos));
		TagUtils.writeVec3(tag, "linkLocation", this.linkLocation);
	}
}
