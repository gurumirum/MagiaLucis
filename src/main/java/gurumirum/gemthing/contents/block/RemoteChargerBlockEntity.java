package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.capability.Capabilities;
import gurumirum.gemthing.capability.LuxAcceptor;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class RemoteChargerBlockEntity extends BlockEntity implements Ticker {
	private static final int CYCLE = 5;

	public RemoteChargerBlockEntity(BlockPos pos, BlockState blockState) {
		super(Contents.REMOTE_CHARGER.get(), pos, blockState);
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;

		for (Player p : level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos).inflate(5), e -> true)) {
			ItemStack stack = p.getMainHandItem();
			LuxAcceptor luxAcceptor = stack.getCapability(Capabilities.LUX_ACCEPTOR);
			if (luxAcceptor == null) continue;
			luxAcceptor.accept(10, RGB332.WHITE, false);
		}
	}
}
