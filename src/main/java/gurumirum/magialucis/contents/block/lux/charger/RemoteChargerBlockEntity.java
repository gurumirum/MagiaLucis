package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LuxAcceptor;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public abstract class RemoteChargerBlockEntity extends LuxNodeBlockEntity<RemoteChargerBehavior> implements Ticker.Server {
	private static final int CYCLE = 10;

	public static RemoteChargerBlockEntity basic(BlockPos pos, BlockState blockState) {
		return new RemoteChargerBlockEntity(ModBlockEntities.REMOTE_CHARGER.get(), pos, blockState) {
			@Override
			protected @NotNull RemoteChargerBehavior createNodeBehavior() {
				return new RemoteChargerBehavior.Basic();
			}
		};
	}

	public static RemoteChargerBlockEntity advanced(BlockPos pos, BlockState blockState) {
		return new RemoteChargerBlockEntity(ModBlockEntities.REMOTE_CHARGER_2.get(), pos, blockState) {
			@Override
			protected @NotNull RemoteChargerBehavior createNodeBehavior() {
				return new RemoteChargerBehavior.Advanced();
			}
		};
	}

	private @Nullable Vector3d lastCharge;
	private @Nullable Vector3d acceptedCache;
	private @Nullable Vector3d chargeLeft;

	public RemoteChargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		Vector3d charge = nodeBehavior().charge;

		if (this.lastCharge == null) {
			this.lastCharge = new Vector3d(charge);
		} else if (!this.lastCharge.equals(charge)) {
			this.lastCharge.set(charge);
			setChanged();
		}

		if (level.getGameTime() % CYCLE != 0) return;
		if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) return;

		Vector3d chargeLeft = this.chargeLeft != null ? this.chargeLeft : (this.chargeLeft = new Vector3d());
		nodeBehavior().stat().maxTransfer(chargeLeft).min(charge);
		charge.sub(this.chargeLeft);

		setChanged();

		for (Player p : level.getEntities(EntityTypeTest.forClass(Player.class), new AABB(pos).inflate(10), e -> true)) {
			Inventory inv = p.getInventory();
			for (int i = 0; i < 9; i++) {
				if (chargeItem(chargeLeft, inv.getItem(i))) {
					if (chargeLeft.x <= 0 && chargeLeft.y <= 0 && chargeLeft.z <= 0) return;
				}
			}

			ICuriosItemHandler h = CuriosApi.getCuriosInventory(p).orElse(null);
			if (h == null) return;

			for (var e : h.getCurios().entrySet()) {
				IDynamicStackHandler stacks = e.getValue().getStacks();
				for (int i = 0; i < stacks.getSlots(); i++) {
					ItemStack stack = stacks.getStackInSlot(i);
					if (chargeItem(chargeLeft, stack)) {
						if (chargeLeft.x <= 0 && chargeLeft.y <= 0 && chargeLeft.z <= 0) return;
					}
				}
			}
		}
	}

	private boolean chargeItem(Vector3d charge, ItemStack stack) {
		boolean success = false;

		if (stack.is(Accessories.WAND_BELT.asItem())) {
			if (stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof IItemHandlerModifiable itemHandler) {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					ItemStack s = itemHandler.getStackInSlot(i);
					if (chargeItem(charge, s)) {
						itemHandler.setStackInSlot(i, s);
						success = true;
						if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) break;
					}
				}
			}

			return success;
		}

		LuxAcceptor luxAcceptor = stack.getCapability(ModCapabilities.LUX_ACCEPTOR);
		Vector3d accepted = this.acceptedCache != null ? this.acceptedCache : (this.acceptedCache = new Vector3d());

		if (luxAcceptor != null) {
			luxAcceptor.accept(charge.x, charge.y, charge.z, false, accepted.zero());
			if (LuxUtils.isValid(accepted)) {
				if (!(accepted.x <= 0) || !(accepted.y <= 0) || !(accepted.z <= 0)) {
					LuxUtils.snapComponents(accepted, 0);
					charge.sub(accepted);
					success = true;
				}
			} else {
				MagiaLucisMod.LOGGER.warn("Lux acceptor capability of item stack {} returned an invalid result!", stack);
			}
		}
		return success;
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
			this.lastCharge.set(nodeBehavior().charge);
		}
	}
}
