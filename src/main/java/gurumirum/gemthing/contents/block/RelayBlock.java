package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class RelayBlock extends Block implements EntityBlock {
	public RelayBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RelayBlockEntity(pos, state);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		LuxNet luxNet = LuxNet.tryGet(level);
		if (luxNet == null || !(level.getBlockEntity(pos) instanceof RelayBlockEntity relay))
			return InteractionResult.SUCCESS;

		LuxNode node = luxNet.get(relay.luxNodeId());
		if (node != null) {
			if (!node.adjacentInboundNodeIds().isEmpty())
				GemthingMod.LOGGER.info("Inbound connection with: {}", node.adjacentInboundNodeIds().intStream()
						.mapToObj(Integer::toString)
						.collect(Collectors.joining(", ")));
			else GemthingMod.LOGGER.info("No inbound connections");
			if (node.outboundNode() != LuxNet.NO_ID)
				GemthingMod.LOGGER.info("Outbound connection with {}", node.outboundNode());
			else GemthingMod.LOGGER.info("No outbound connection");
		} else {
			GemthingMod.LOGGER.info("No node");
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
		LuxNet luxNet = LuxNet.tryGet(level);
		if (luxNet != null && level.getBlockEntity(pos) instanceof RelayBlockEntity blockEntity) {
			luxNet.unregister(blockEntity.luxNodeId(), true);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
