package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.capability.DirectLinkDestination;
import gurumirum.magialucis.capability.LinkDestination;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class RemoteChargerBlockEntity extends LuxNodeBlockEntity<ChargerBehavior>
		implements Ticker.Server, DirectLinkDestination {
	private static final int CYCLE = 10;

	private final ChargerTier chargerTier;

	public RemoteChargerBlockEntity(ChargerTier chargerTier, BlockPos pos, BlockState blockState) {
		super(chargerTier.remoteChargerBlockEntityType(), pos, blockState);
		this.chargerTier = chargerTier;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			LightEffectRender.register(new BlockLightEffectProvider<>(this, 1));
		}
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE == 0) chargeTick(level, pos);
	}

	private void chargeTick(@NotNull Level level, @NotNull BlockPos pos) {
		Vector3d charge = nodeBehavior().charge;
		boolean hasLuxInput = charge.x > 0 || charge.y > 0 || charge.z > 0;

		updateProperty(BlockStateProperties.ENABLED, hasLuxInput);

		if (hasLuxInput) {
			for (Player p : level.getEntities(EntityTypeTest.forClass(Player.class),
					new AABB(pos).inflate(10),
					e -> true)) {
				Inventory inv = p.getInventory();
				for (int i = 0; i < 9; i++) {
					if (ChargeLogic.chargeItem(charge, inv.getItem(i), nodeBehavior().maxInput)) {
						if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) return;
					}
				}

				ICuriosItemHandler h = CuriosApi.getCuriosInventory(p).orElse(null);
				if (h == null) return;

				for (var e : h.getCurios().entrySet()) {
					IDynamicStackHandler stacks = e.getValue().getStacks();
					for (int i = 0; i < stacks.getSlots(); i++) {
						ItemStack stack = stacks.getStackInSlot(i);
						if (ChargeLogic.chargeItem(charge, stack, nodeBehavior().maxInput)) {
							if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) return;
						}
					}
				}
			}
		}
		nodeBehavior().reset();
	}

	@Override
	protected @NotNull ChargerBehavior createNodeBehavior() {
		return new ChargerBehavior(this.chargerTier, true);
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

	@Override
	public @NotNull LinkTestResult linkWithSource(@NotNull LinkContext context) {
		Direction dir = getBlockState().getValue(BlockStateProperties.FACING);
		if (context.side() != null && dir != Direction.DOWN && context.side() == dir.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult directLinkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() != getBlockState()
				.getValue(BlockStateProperties.FACING)
				.getOpposite()) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}
}
