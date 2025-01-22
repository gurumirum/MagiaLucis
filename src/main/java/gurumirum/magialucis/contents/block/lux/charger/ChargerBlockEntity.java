package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class ChargerBlockEntity extends LuxNodeBlockEntity<ChargerBehavior> implements Ticker.Server {
	private final ChargerTier chargerTier;
	private final ChargerInventory inventory = new ChargerInventory();

	private @Nullable Vector3d lastCharge;

	public ChargerBlockEntity(ChargerTier chargerTier, BlockPos pos, BlockState blockState) {
		super(chargerTier.chargerBlockEntityType(), pos, blockState);
		this.chargerTier = chargerTier;
	}

	public IItemHandlerModifiable inventory() {
		return this.inventory;
	}

	public boolean dropItem() {
		Level level = this.level;
		if (level == null) return false;

		ItemStack stack = this.inventory.extractItem(0, 64, level.isClientSide);

		if (!stack.isEmpty()) {
			if (!level.isClientSide) {
				drop(level, getBlockPos(), stack);
			}
			return true;
		}

		return false;
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
		Vector3d charge = nodeBehavior().charge;

		ItemStack stack = this.inventory.getStackInSlot(0);
		if (!stack.isEmpty()) {
			if (ChargeLogic.chargeItem(charge, stack, this.chargerTier.stat())) {
				setChanged();
			}
		}

		if (this.lastCharge == null) {
			this.lastCharge = new Vector3d(charge);
		} else if (!this.lastCharge.equals(charge)) {
			this.lastCharge.set(charge);
			setChanged();
		}
	}

	@Override
	protected @NotNull ChargerBehavior createNodeBehavior() {
		return new ChargerBehavior(this.chargerTier, false);
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		tag.put("inventory", this.inventory.serializeNBT(lookupProvider));

		if (context.isSaveLoad()) {
			TagUtils.writeVector3d(tag, "charge", nodeBehavior().charge);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		for (int i = 0; i < this.inventory.getSlots(); i++) {
			this.inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
		this.inventory.deserializeNBT(lookupProvider, tag.getCompound("inventory"));

		if (context.isSaveLoad()) {
			TagUtils.readVector3d(tag, "charge", nodeBehavior().charge);
			if (this.lastCharge != null) this.lastCharge.set(nodeBehavior().charge);
		}
	}

	private static void drop(Level level, BlockPos pos, ItemStack stack) {
		ItemEntity itemEntity = new ItemEntity(level,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				stack);
		level.addFreshEntity(itemEntity);
	}

	private final class ChargerInventory extends ItemStackHandler {
		public ChargerInventory() {
			super(1);
		}

		@Override
		public void setSize(int size) {} // no-op

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.isEmpty() ||
					stack.is(Accessories.WAND_BELT.asItem()) ||
					stack.getCapability(ModCapabilities.LUX_ACCEPTOR) != null;
		}

		@Override
		protected int getStackLimit(int slot, @NotNull ItemStack stack) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
			ChargerBlockEntity.this.syncToClient();
		}
	}
}
