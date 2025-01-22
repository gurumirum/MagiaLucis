package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class RemoteChargerBlockEntity extends LuxNodeBlockEntity<ChargerBehavior> implements Ticker.Server {
	private static final int CYCLE = 10;

	private final ChargerTier chargerTier;

	private @Nullable Vector3d lastCharge;
	private @Nullable Vector3d chargeLeft;

	public RemoteChargerBlockEntity(ChargerTier chargerTier, BlockPos pos, BlockState blockState) {
		super(chargerTier.remoteChargerBlockEntityType(), pos, blockState);
		this.chargerTier = chargerTier;
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE == 0) chargeTick(level, pos);

		Vector3d charge = nodeBehavior().charge;

		if (this.lastCharge == null) {
			this.lastCharge = new Vector3d(charge);
		} else if (!this.lastCharge.equals(charge)) {
			this.lastCharge.set(charge);
			setChanged();
		}
	}

	private void chargeTick(@NotNull Level level, @NotNull BlockPos pos) {
		Vector3d charge = nodeBehavior().charge;
		if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) return;

		Vector3d chargeLeft = this.chargeLeft != null ? this.chargeLeft : (this.chargeLeft = new Vector3d());
		nodeBehavior().stat().maxTransfer(chargeLeft).min(charge);
		charge.sub(this.chargeLeft);

		for (Player p : level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos).inflate(10), e -> true)) {
			Inventory inv = p.getInventory();
			for (int i = 0; i < 9; i++) {
				if (ChargeLogic.chargeItem(chargeLeft, inv.getItem(i), this.chargerTier.stat())) {
					if (chargeLeft.x <= 0 && chargeLeft.y <= 0 && chargeLeft.z <= 0) return;
				}
			}

			ICuriosItemHandler h = CuriosApi.getCuriosInventory(p).orElse(null);
			if (h == null) return;

			for (var e : h.getCurios().entrySet()) {
				IDynamicStackHandler stacks = e.getValue().getStacks();
				for (int i = 0; i < stacks.getSlots(); i++) {
					ItemStack stack = stacks.getStackInSlot(i);
					if (ChargeLogic.chargeItem(chargeLeft, stack, this.chargerTier.stat())) {
						if (chargeLeft.x <= 0 && chargeLeft.y <= 0 && chargeLeft.z <= 0) return;
					}
				}
			}
		}
	}

	@Override
	protected @NotNull ChargerBehavior createNodeBehavior() {
		return new ChargerBehavior(this.chargerTier, true);
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (context.isSaveLoad()) {
			TagUtils.writeVector3d(tag, "charge", nodeBehavior().charge);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		if (context.isSaveLoad()) {
			TagUtils.readVector3d(tag, "charge", nodeBehavior().charge);
			if (this.lastCharge != null) this.lastCharge.set(nodeBehavior().charge);
		}
	}
}
