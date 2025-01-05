package gurumirum.gemthing.contents.block.lux;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public abstract class LuxNodeBlock extends Block implements EntityBlock {
	public LuxNodeBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		LuxNet luxNet = LuxNet.tryGet(level);
		if (luxNet == null || !(level.getBlockEntity(pos) instanceof LuxNodeBlockEntity relay))
			return InteractionResult.SUCCESS;

		LuxNode node = luxNet.get(relay.luxNodeId());
		if (node != null) {
			GemthingMod.LOGGER.info("Node #{}, iface: {}", node.id, node.iface());
			if (!node.inboundNodes().isEmpty())
				GemthingMod.LOGGER.info("Inbound connection with: {}", node.inboundNodes().intStream()
						.mapToObj(Integer::toString)
						.collect(Collectors.joining(", ")));
			else GemthingMod.LOGGER.info("No inbound connections");
			if (!node.outboundNodes().isEmpty())
				GemthingMod.LOGGER.info("Outbound connection with: {}", node.outboundNodes().intStream()
						.mapToObj(Integer::toString)
						.collect(Collectors.joining(", ")));
			else GemthingMod.LOGGER.info("No outbound connections");
		} else {
			GemthingMod.LOGGER.info("No node");
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
		LuxNet luxNet = LuxNet.tryGet(level);
		if (luxNet != null && level.getBlockEntity(pos) instanceof LuxNodeBlockEntity blockEntity) {
			luxNet.unregister(blockEntity.luxNodeId(), true);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
