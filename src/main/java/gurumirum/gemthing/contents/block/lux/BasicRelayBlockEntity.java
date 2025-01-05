package gurumirum.gemthing.contents.block.lux;

import gurumirum.gemthing.capability.LinkSource;
import gurumirum.gemthing.capability.LuxNetComponent;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.impl.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BasicRelayBlockEntity extends LuxNodeBlockEntity implements LinkSource {
	public static final int DEFAULT_MAX_LINKS = 3;
	public static final double DEFAULT_LINK_DISTANCE = 15;

	private final List<Orientation> links = new ArrayList<>();
	private @Nullable List<Orientation> linksView;

	public BasicRelayBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public @NotNull @UnmodifiableView List<Orientation> getLinks() {
		return this.linksView == null ? this.linksView = Collections.unmodifiableList(this.links) : this.linksView;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		if (this.links.isEmpty()) return;

		Level level = this.level;
		if (level == null) return;

		BlockPos pos = getBlockPos();
		double linkDistance = linkDistance();

		for (Orientation o : this.links) {
			Vector3d vec = o.toVector(linkCollector.mutableVec3d);

			BlockHitResult hitResult = safeClip(level, new ClipContext(
					Vec3.atCenterOf(pos).add(vec.x,
							vec.y,
							vec.z),
					new Vec3(
							pos.getX() + .5f + vec.x * linkDistance,
							pos.getY() + .5f + vec.y * linkDistance,
							pos.getZ() + .5f + vec.z * linkDistance),
					ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
					CollisionContext.empty()));

			if (hitResult.getType() == HitResult.Type.BLOCK && !hitResult.getBlockPos().equals(pos)) {
				LuxNetComponent luxNetComponent = level.getCapability(ModCapabilities.LUX_NET_COMPONENT, hitResult.getBlockPos(), hitResult.getDirection());
				if (luxNetComponent != null) {
					linkCollector.link(luxNetComponent.luxNodeId());
				}
			}
		}
	}

	@Override
	public void link(@NotNull Vec3 target) {
		int nodeId = luxNodeId();
		if (nodeId == NO_ID) return;

		BlockPos pos = getBlockPos();
		var o = getOrientation(
				pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
				target.x, target.y, target.z);

		if (maxLinks() <= this.links.size()) {
			// change the last entry
			this.links.set(this.links.size() - 1, o);
		} else {
			this.links.add(o);
		}

		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.queueLinkUpdate(nodeId);

		setChanged();
		syncToClient();
	}

	@Override
	public void unlink() {
		int nodeId = luxNodeId();
		if (nodeId == NO_ID) return;
		if (this.links.isEmpty()) return;

		this.links.removeLast();

		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.queueLinkUpdate(nodeId);

		setChanged();
		syncToClient();
	}

	@Override
	public void unlinkAll() {
		int nodeId = luxNodeId();
		if (nodeId == NO_ID) return;
		if (this.links.isEmpty()) return;

		this.links.clear();

		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.queueLinkUpdate(nodeId);

		setChanged();
		syncToClient();
	}

	protected int maxLinks() {
		return DEFAULT_MAX_LINKS;
	}

	protected double linkDistance() {
		return DEFAULT_LINK_DISTANCE;
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		this.links.clear();
		if (tag.contains("orientations", CompoundTag.TAG_LONG_ARRAY)) {
			Arrays.stream(tag.getLongArray("orientations")).mapToObj(Orientation::new).forEach(this.links::add);
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		if (!this.links.isEmpty()) {
			tag.putLongArray("orientations", this.links.stream().mapToLong(Orientation::packageToLong).toArray());
		}
	}

	public static Orientation getOrientation(
			double fromX, double fromY, double fromZ,
			double toX, double toY, double toZ
	) {
		double x = toX - fromX;
		double y = toY - fromY;
		double z = toZ - fromZ;
		return new Orientation(
				(float)-Mth.atan2(y, Math.sqrt(x * x + z * z)),
				(float)(Mth.atan2(z, x) - Math.PI / 2));
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

	public record Orientation(float xRot, float yRot) {
		public Orientation(long packagedLong) {
			this(Float.intBitsToFloat((int)(packagedLong >> 32)), Float.intBitsToFloat((int)packagedLong));
		}

		public long packageToLong() {
			return ((long)Float.floatToRawIntBits(this.xRot) << 32) | Integer.toUnsignedLong(Float.floatToRawIntBits(this.yRot));
		}

		public Vector3d toVector(Vector3d vector) {
			float yCos = Mth.cos(-this.yRot);
			float ySin = Mth.sin(-this.yRot);
			float xCos = Mth.cos(this.xRot);
			float xSin = Mth.sin(this.xRot);
			return vector.set(ySin * xCos, -xSin, yCos * xCos);
		}

		public Vector3f toVector(Vector3f vector) {
			float yCos = Mth.cos(-this.yRot);
			float ySin = Mth.sin(-this.yRot);
			float xCos = Mth.cos(this.xRot);
			float xSin = Mth.sin(this.xRot);
			return vector.set(ySin * xCos, -xSin, yCos * xCos);
		}
	}
}
