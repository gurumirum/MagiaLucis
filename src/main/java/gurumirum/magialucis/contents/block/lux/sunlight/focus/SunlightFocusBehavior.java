package gurumirum.magialucis.contents.block.lux.sunlight.focus;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.contents.block.lux.sunlight.SunlightLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class SunlightFocusBehavior implements LuxGeneratorNodeBehavior {
	private final BlockPos pos;

	private int skyVisibility;

	public SunlightFocusBehavior(@NotNull BlockPos pos, int skyVisibility) {
		this.pos = pos;
		this.skyVisibility = skyVisibility;
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.SUNLIGHT_FOCUS;
	}

	@Override
	public @NotNull LuxStat stat() {
		return SunlightFocusBlock.STAT;
	}

	public BlockPos pos() {
		return pos;
	}

	public int skyVisibility() {
		return skyVisibility;
	}

	public void setSkyVisibility(int skyVisibility) {
		this.skyVisibility = skyVisibility;
	}

	@Override
	public void generateLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d generatedLux) {
		SunlightLogic.getColor(level, pos(),
				SunlightLogic.DEFAULT_BASE_INTENSITY * (skyVisibility() / 16.0),
				generatedLux);
	}

	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		tag.put("pos", NbtUtils.writeBlockPos(this.pos));
		tag.putByte("skyVisibility", (byte)this.skyVisibility);
	}

	public SunlightFocusBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this.pos = NbtUtils.readBlockPos(tag, "pos").orElse(BlockPos.ZERO);
		this.skyVisibility = Mth.clamp(tag.getByte("skyVisibility"), 0, 15);
	}
}
