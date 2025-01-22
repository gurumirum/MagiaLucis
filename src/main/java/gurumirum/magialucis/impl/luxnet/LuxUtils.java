package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.capability.LuxNetLinkDestination;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public final class LuxUtils {
	private LuxUtils() {}

	public static void transfer(Vector3d src, Vector3d dst, Vector3d dstMaxCharge) {
		transfer(src, dst, dstMaxCharge.x, dstMaxCharge.y, dstMaxCharge.z);
	}

	public static void transfer(Vector3d src, Vector3d dst, double dstMaxChargeR, double dstMaxChargeG, double dstMaxChargeB) {
		dst.x = transfer(src, dst.x, ColorComponent.R, dstMaxChargeR);
		dst.y = transfer(src, dst.y, ColorComponent.G, dstMaxChargeG);
		dst.z = transfer(src, dst.z, ColorComponent.B, dstMaxChargeB);
	}

	public static double transfer(Vector3d src, double dst, ColorComponent component, double dstMaxCharge) {
		return transfer(src, dst, component, dstMaxCharge, Double.MAX_VALUE);
	}

	public static double transfer(Vector3d src, double dst, ColorComponent component, double dstMaxCharge, double maxTransfer) {
		double srcValue = getComponent(src, component);

		double transferLimit = Math.min(maxTransfer, srcValue);
		double receiveLimit = dstMaxCharge - dst;
		double transferAmount = Math.min(receiveLimit, transferLimit);
		if (transferAmount == 0) return dst;

		setComponent(src, component, srcValue - transferAmount);
		// to prevent floating point shenanigans
		return receiveLimit <= transferLimit ? dstMaxCharge : dst + transferAmount;
	}

	public static boolean isValid(Vector3d vec) {
		return !Double.isNaN(vec.x) && !Double.isNaN(vec.y) && !Double.isNaN(vec.z);
	}

	public static void snapComponents(Vector3d vec, double min) {
		if (!(vec.x >= min)) vec.x = 0;
		if (!(vec.y >= min)) vec.y = 0;
		if (!(vec.z >= min)) vec.z = 0;
	}

	private static double getComponent(Vector3d color, ColorComponent component) {
		return switch (component) {
			case R -> color.x;
			case G -> color.y;
			case B -> color.z;
		};
	}

	private static void setComponent(Vector3d color, ColorComponent component, double value) {
		switch (component) {
			case R -> color.x = value;
			case G -> color.y = value;
			case B -> color.z = value;
		}
	}

	public static boolean linkToInWorldNode(BlockEntity blockEntity, LuxNet.LinkCollector linkCollector,
	                                        float xRot, float yRot, double linkDistance,
	                                        int linkIndex, @Nullable LinkDestinationSelector selector) {
		Level level = blockEntity.getLevel();
		if (level == null) return false;

		Vector3d vec = LinkSource.Orientation.toVector(xRot, yRot, linkCollector.mutableVec3d);

		BlockPos pos = blockEntity.getBlockPos();
		BlockHitResult hitResult = safeClip(level, new ClipContext(
				Vec3.atCenterOf(pos).add(vec.x, vec.y, vec.z),
				new Vec3(
						pos.getX() + .5f + vec.x * linkDistance,
						pos.getY() + .5f + vec.y * linkDistance,
						pos.getZ() + .5f + vec.z * linkDistance),
				ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
				LuxNetCollisionContext.EMPTY));

		if (hitResult.getType() == HitResult.Type.BLOCK && !hitResult.getBlockPos().equals(pos)) {
			if (selector == null) selector = LinkDestinationSelector.DEFAULT;
			LuxNetLinkDestination dest = selector.chooseLinkDestination(level, linkCollector, hitResult);
			if (dest != null) {
				return linkCollector.inWorldLink(linkIndex,
						dest.linkWithSource(new LinkContext(level, linkCollector.luxNet(), linkCollector.luxNode(), hitResult)).nodeId(),
						pos, hitResult.getBlockPos(), hitResult.getLocation());
			}
		}
		linkCollector.inWorldLinkFail(linkIndex, pos, hitResult.getBlockPos(), hitResult.getLocation());
		return false;
	}

	/**
	 * {@link net.minecraft.world.level.BlockGetter#clip(ClipContext)} that does not incur chunk loading
	 *
	 * @return Block hit result
	 */
	public static BlockHitResult safeClip(Level level, ClipContext context) {
		return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, (ctx, pos) -> {
			if (!level.isLoaded(pos)) {
				Vec3 center = Vec3.atCenterOf(pos);
				return BlockHitResult.miss(center, Direction.getNearest(center), pos);
			}

			BlockState state = level.getBlockState(pos);
			FluidState fluidState = level.getFluidState(pos);
			BlockHitResult blockHit = level.clipWithInteractionOverride(ctx.getFrom(), ctx.getTo(), pos, ctx.getBlockShape(state, level, pos), state);
			BlockHitResult fluidHit = ctx.getFluidShape(fluidState, level, pos).clip(ctx.getFrom(), ctx.getTo(), pos);
			double blockHitDist = blockHit == null ? Double.MAX_VALUE : ctx.getFrom().distanceToSqr(blockHit.getLocation());
			double fluidHitDist = fluidHit == null ? Double.MAX_VALUE : ctx.getFrom().distanceToSqr(fluidHit.getLocation());
			return blockHitDist <= fluidHitDist ? blockHit : fluidHit;
		}, ctx -> {
			Vec3 dist = ctx.getFrom().subtract(ctx.getTo());
			return BlockHitResult.miss(ctx.getTo(), Direction.getNearest(dist.x, dist.y, dist.z), BlockPos.containing(ctx.getTo()));
		});
	}

	public static double sum(Vector3d vec) {
		return Math.max(0, vec.x) + Math.max(0, vec.y) + Math.max(0, vec.z);
	}

	public enum ColorComponent {
		R, G, B
	}
}
