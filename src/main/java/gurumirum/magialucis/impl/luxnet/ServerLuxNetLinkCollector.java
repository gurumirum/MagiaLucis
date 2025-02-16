package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.luxnet.*;
import gurumirum.magialucis.api.Orientation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ServerLuxNetLinkCollector implements LuxNetLinkCollector, ServerSideLinkContext {
	private final ServerLuxNet luxNet;
	private @Nullable ServerLuxNode node;

	public final List<Link> links = new ArrayList<>();
	public final Int2ObjectMap<InWorldLinkState> linkIndexToState = new Int2ObjectOpenHashMap<>();
	public final Set<ServerLuxNode> nodeCache = new ObjectOpenHashSet<>();

	public int voidLinkWeight;

	public ServerLuxNetLinkCollector(ServerLuxNet luxNet) {
		this.luxNet = luxNet;
	}

	public void init(@NotNull ServerLuxNode node) {
		this.node = node;
	}

	@Override
	public @NotNull ServerSideLinkContext context() {
		return this;
	}

	@Override
	public @NotNull ServerLuxNet luxNet() {
		return luxNet;
	}

	@Override
	public @NotNull ServerLuxNode luxNode() {
		if (this.node == null) throw new IllegalStateException();
		return this.node;
	}

	@Override
	public void implicitLink(int nodeId, int linkWeight) {
		if (this.node == null) throw new IllegalStateException();
		if (linkWeight < 0) throw new IllegalArgumentException("linkWeight < 0");

		// disallow null source and self connection
		boolean connected = nodeId != LuxNet.NO_ID && this.node.id() != nodeId && luxNet.get(nodeId) != null;

		if (connected) {
			this.links.add(new Link(nodeId, true, -1, linkWeight, null));
		}
	}

	@Override
	public boolean inWorldLink(int linkIndex, int nodeId, @NotNull BlockPos origin,
	                           @NotNull BlockPos linkPos, @NotNull Vec3 linkLocation,
	                           int linkWeight, boolean registerLinkFail, int failedLinkWeight) {
		if (this.node == null) throw new IllegalStateException();
		if (linkIndex < 0) throw new IllegalArgumentException("linkIndex < 0");
		if (linkWeight < 0) throw new IllegalArgumentException("linkWeight < 0");
		if (failedLinkWeight < 0) throw new IllegalArgumentException("failedLinkWeight < 0");

		// disallow null source and self connection
		boolean connected = nodeId != LuxNet.NO_ID && this.node.id() != nodeId && luxNet.get(nodeId) != null;

		if (connected || registerLinkFail) {
			this.links.add(new Link(nodeId, connected, linkIndex, connected ? linkWeight : failedLinkWeight,
					new InWorldLinkInfo(origin.immutable(), linkPos.immutable(), Objects.requireNonNull(linkLocation))));
		}

		return connected;
	}

	@Override
	public void inWorldLinkFail(int linkIndex, @NotNull BlockPos origin, @NotNull BlockPos linkPos,
	                            @NotNull Vec3 linkLocation, int linkWeight) {
		if (this.node == null) throw new IllegalStateException();
		if (linkIndex < 0) throw new IllegalArgumentException("linkIndex < 0");
		if (linkWeight < 0) throw new IllegalArgumentException("linkWeight < 0");

		this.links.add(new Link(-1, false, linkIndex, linkWeight,
				new InWorldLinkInfo(origin.immutable(), linkPos.immutable(), Objects.requireNonNull(linkLocation))));
	}

	@Override
	public void voidLink(int linkWeight) {
		if (this.node == null) throw new IllegalStateException();
		if (linkWeight < 0) throw new IllegalArgumentException("linkWeight < 0");
		this.voidLinkWeight += linkWeight;
	}

	@Override
	public boolean linkToInWorldNode(BlockEntity blockEntity,
	                                 Orientation orientation, Vec3 linkOrigin, double linkDistance,
	                                 int linkIndex, @Nullable LinkDestinationSelector selector,
	                                 int linkWeight, boolean registerLinkFail, int failedLinkWeight) {
		Level level = blockEntity.getLevel();
		if (level == null) return false;

		BlockPos pos = blockEntity.getBlockPos();
		BlockHitResult hitResult = LuxUtils.traceConnection(level, orientation,
				pos, linkOrigin, linkDistance);

		if (hitResult.getType() == HitResult.Type.BLOCK && !hitResult.getBlockPos().equals(pos)) {
			if (selector == null) selector = LinkDestinationSelector.DEFAULT;
			LinkDestination dest = selector.chooseLinkDestination(level, this, hitResult);
			if (dest != null) {
				LinkContext context = new LinkContext(level, luxNet(), luxNode(), hitResult);
				return inWorldLink(linkIndex,
						dest.linkWithSource(context).nodeId(),
						pos, hitResult.getBlockPos(), hitResult.getLocation(),
						linkWeight, registerLinkFail, failedLinkWeight);
			}
		}
		if (registerLinkFail) {
			inWorldLinkFail(linkIndex, pos, hitResult.getBlockPos(), hitResult.getLocation(), failedLinkWeight);
		}
		return false;
	}

	@Override
	public boolean directLinkToInWorldNode(BlockEntity blockEntity,
	                                       BlockPos linkDestPos, Direction side,
	                                       int linkIndex, @Nullable DirectLinkDestinationSelector selector,
	                                       int linkWeight, boolean registerLinkFail, int failedLinkWeight) {
		Level level = blockEntity.getLevel();
		if (level == null) return false;

		BlockPos pos = blockEntity.getBlockPos();

		if (level.isLoaded(linkDestPos)) {
			if (selector == null) selector = DirectLinkDestinationSelector.DEFAULT;
			DirectLinkDestination dest = selector.chooseDirectLinkDestination(level, this,
					linkDestPos, side);

			if (dest != null) {
				LinkContext context = new LinkContext(level, luxNet(), luxNode(),
						side, null);
				return inWorldLink(linkIndex,
						dest.directLinkWithSource(context).nodeId(),
						pos, linkDestPos.immutable(), Vec3.atLowerCornerOf(side.getNormal())
								.add(1, 1, 1)
								.scale(0.5)
								.add(Vec3.atLowerCornerOf(linkDestPos)),
						linkWeight, registerLinkFail, failedLinkWeight);
			}
		}
		if (registerLinkFail) {
			inWorldLinkFail(linkIndex, pos, linkDestPos.immutable(),
					Vec3.atLowerCornerOf(side.getNormal())
							.add(1, 1, 1)
							.scale(0.5)
							.add(Vec3.atLowerCornerOf(linkDestPos)),
					failedLinkWeight);
		}
		return false;
	}

	public void reset() {
		this.links.clear();
		this.linkIndexToState.clear();
		this.node = null;
		this.voidLinkWeight = 0;
	}

	public record Link(
			int destId,
			boolean linked,
			int linkIndex,
			int weight,
			@Nullable InWorldLinkInfo info
	) {}
}
