package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class ChargerBlockEntity extends LuxNodeBlockEntity<ChargerBehavior> implements Ticker.Server, DirectLinkDestination {
	private static final int SYNC_INTERVAL = 3;

	private final ChargerTier chargerTier;
	private final ChargerInventory inventory = new ChargerInventory();

	private boolean syncContents;

	public ChargerBlockEntity(ChargerTier chargerTier, BlockPos pos, BlockState blockState) {
		super(chargerTier.chargerBlockEntityType(), pos, blockState);
		this.chargerTier = chargerTier;
	}

	public IItemHandlerModifiable inventory() {
		return this.inventory;
	}

	public boolean dropItem(@Nullable Player player) {
		Level level = this.level;
		if (level == null) return false;

		ItemStack stack = this.inventory.extractItem(0, 64, level.isClientSide);

		if (!stack.isEmpty()) {
			if (!level.isClientSide) {
				ModUtils.giveOrDrop(player, level, stack, getBlockPos());
			}
			return true;
		}

		return false;
	}

	public boolean swapItem(@Nullable Player player, @NotNull ItemStack stack) {
		Level level = this.level;
		if (level == null) return false;

		if (!this.inventory.isItemValid(0, stack)) return false;

		if (!level.isClientSide) {
			ItemStack stack2 = this.inventory.getStackInSlot(0);
			this.inventory.setStackInSlot(0, stack.split(stack.getCount()));
			ModUtils.giveOrDrop(player, level, stack2, getBlockPos());
		}

		return true;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new BlockLightEffectProvider<>(this));
		}
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		Vector3d charge = nodeBehavior().charge;
		ItemStack stack = this.inventory.getStackInSlot(0);

		if (!stack.isEmpty()) {
			Vector3d luxInput = nodeBehavior().luxInput.min(new Vector3d());
			if (ChargeLogic.chargeItem(charge, stack, luxInput)) {
				this.syncContents = true;
				setChanged();
			}
		}

		charge.zero();

		if (this.syncContents && level.getGameTime() % SYNC_INTERVAL == 0) {
			this.syncContents = false;
			syncToClient();
		}
	}

	@Override
	protected @NotNull ChargerBehavior createNodeBehavior() {
		return new ChargerBehavior(this.chargerTier, false);
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {}

	@Override
	public @NotNull LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() == Direction.DOWN) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	public @NotNull LinkTestResult directLinkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() != Direction.DOWN) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		tag.put("inventory", this.inventory.serializeNBT(lookupProvider));
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		for (int i = 0; i < this.inventory.getSlots(); i++) {
			this.inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
		this.inventory.deserializeNBT(lookupProvider, tag.getCompound("inventory"));
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
			ChargerBlockEntity.this.syncContents = true;
		}
	}
}
