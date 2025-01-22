package gurumirum.magialucis.contents.block.sunlight.focus;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.contents.block.sunlight.SunlightLogic;
import gurumirum.magialucis.impl.RGB332;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class SunlightFocusBehavior implements LuxGeneratorNodeBehavior {
	public static final LuxStat STAT = LuxStat.simple(RGB332.WHITE,
			0,
			// use max throughput of foci for the stat
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY);

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
		return STAT;
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
	public void generateLux(Level level, LuxNet luxNet, LuxNode node, Vector3d generatedLux) {
		if (node.isLoaded()) {
			SunlightLogic.getColor(level, pos(),
					SunlightLogic.DEFAULT_BASE_INTENSITY * (skyVisibility() / 16.0),
					generatedLux);
		}
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
