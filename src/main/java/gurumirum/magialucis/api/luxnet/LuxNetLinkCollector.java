package gurumirum.magialucis.api.luxnet;

import gurumirum.magialucis.api.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LuxNetLinkCollector {
	@NotNull ServerSideLinkContext context();

	void implicitLink(int nodeId, int linkWeight);

	boolean inWorldLink(int linkIndex, int nodeId, @NotNull BlockPos origin,
	                    @NotNull BlockPos linkPos, @NotNull Vec3 linkLocation,
	                    int linkWeight, boolean registerLinkFail, int failedLinkWeight);

	void inWorldLinkFail(int linkIndex, @NotNull BlockPos origin, @NotNull BlockPos linkPos,
	                     @NotNull Vec3 linkLocation, int linkWeight);

	void voidLink(int linkWeight);

	default boolean linkToInWorldNode(BlockEntity blockEntity,
	                                  Orientation orientation, Vec3 linkOrigin, double linkDistance,
	                                  int linkIndex, @Nullable LinkDestinationSelector selector,
	                                  int linkWeight, boolean registerLinkFail) {
		return linkToInWorldNode(blockEntity, orientation, linkOrigin, linkDistance, linkIndex, selector,
				linkWeight, registerLinkFail, linkWeight);
	}

	boolean linkToInWorldNode(BlockEntity blockEntity,
	                          Orientation orientation, Vec3 linkOrigin, double linkDistance,
	                          int linkIndex, @Nullable LinkDestinationSelector selector,
	                          int linkWeight, boolean registerLinkFail, int failedLinkWeight);


	default boolean directLinkToInWorldNode(BlockEntity blockEntity,
	                                        BlockPos linkDestPos, Direction side,
	                                        int linkIndex, @Nullable DirectLinkDestinationSelector selector,
	                                        int linkWeight, boolean registerLinkFail) {
		return directLinkToInWorldNode(blockEntity, linkDestPos, side, linkIndex, selector,
				linkWeight, registerLinkFail, linkWeight);
	}

	boolean directLinkToInWorldNode(BlockEntity blockEntity,
	                                BlockPos linkDestPos, Direction side,
	                                int linkIndex, @Nullable DirectLinkDestinationSelector selector,
	                                int linkWeight, boolean registerLinkFail, int failedLinkWeight);
}
