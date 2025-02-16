package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.luxnet.DirectLinkDestinationSelector;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.api.luxnet.LuxNetCollisionContext;
import gurumirum.magialucis.contents.ModParticles;
import gurumirum.magialucis.utils.Orientation;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public final class LuxUtils {
	private LuxUtils() {}

	public static void transfer(Vector3d src, Vector3d dst, Vector3d dstMaxCharge) {
		transfer(src, dst, dstMaxCharge.x, dstMaxCharge.y, dstMaxCharge.z);
	}

	public static void transfer(Vector3d src, Vector3d dst,
	                            double dstMaxChargeR, double dstMaxChargeG, double dstMaxChargeB) {
		transfer(src, dst, ColorComponent.R, dstMaxChargeR, Double.MAX_VALUE);
		transfer(src, dst, ColorComponent.G, dstMaxChargeG, Double.MAX_VALUE);
		transfer(src, dst, ColorComponent.B, dstMaxChargeB, Double.MAX_VALUE);
	}

	public static void transfer(Vector3d src, Vector3d dst, ColorComponent component, double dstMaxCharge, double maxTransfer) {
		if (!(getComponent(dst, component) >= 0)) setComponent(dst, component, 0);

		double srcValue = getComponent(src, component);

		double transferLimit = Math.min(maxTransfer, srcValue);
		double receiveLimit = dstMaxCharge - getComponent(dst, component);
		double transferAmount = Math.min(receiveLimit, transferLimit);
		if (transferAmount <= 0) return;

		setComponent(src, component, srcValue - transferAmount);
		// to prevent floating point shenanigans
		setComponent(dst, component, receiveLimit <= transferLimit ?
				dstMaxCharge : getComponent(src, component) + transferAmount);
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

	public static @NotNull BlockHitResult traceConnection(Level level, Orientation orientation,
	                                                      BlockPos linkOriginPos, Vec3 linkOriginLocation,
	                                                      double linkDistance) {
		Vector3d vec = orientation.toVector(new Vector3d());

		return safeClip(level, new ClipContext(
						linkOriginLocation,
						new Vec3(
								linkOriginLocation.x + vec.x * linkDistance,
								linkOriginLocation.y + vec.y * linkDistance,
								linkOriginLocation.z + vec.z * linkDistance),
						ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
						LuxNetCollisionContext.EMPTY),
				linkOriginPos);
	}

	/**
	 * {@link net.minecraft.world.level.BlockGetter#clip(ClipContext)} that does not incur chunk loading
	 *
	 * @return Block hit result
	 */
	public static BlockHitResult safeClip(Level level, ClipContext context, @Nullable BlockPos origin) {
		return BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, (ctx, pos) -> {
			if (!level.isLoaded(pos)) {
				Vec3 center = Vec3.atCenterOf(pos);
				return BlockHitResult.miss(center, Direction.getNearest(center), pos);
			}
			if (pos.equals(origin)) {
				return null;
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

	public static void addSpreadingLightParticle(@NotNull Level level,
	                                             double x, double y, double z,
	                                             double offset, double speed) {
		addSpreadingLightParticle(level, x, y, z, offset, speed, false);
	}

	public static void addSpreadingLightParticle(@NotNull Level level,
	                                             double x, double y, double z,
	                                             double offset, double speed, boolean onlyUpwards) {
		Vector3d direction = Orientation.toVector(
				(float)(level.random.nextFloat() * (onlyUpwards ? Math.PI : Math.PI * 2) - Math.PI),
				(float)(level.random.nextFloat() * Math.PI * 2),
				new Vector3d());

		level.addParticle(ModParticles.LIGHT.get(),
				x + direction.x * offset,
				y + direction.y * offset,
				z + direction.z * offset,
				direction.x * speed,
				direction.y * speed,
				direction.z * speed);
	}

	public enum ColorComponent {
		R, G, B
	}
}
