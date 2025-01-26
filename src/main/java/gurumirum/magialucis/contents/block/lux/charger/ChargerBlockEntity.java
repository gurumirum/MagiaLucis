package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.capability.DirectLinkDestination;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
				giveOrDrop(player, level, getBlockPos(), stack);
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
			giveOrDrop(player, level, getBlockPos(), stack2);
		}

		return true;
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
		ItemStack stack = this.inventory.getStackInSlot(0);
		if (!stack.isEmpty()) {
			if (ChargeLogic.chargeItem(nodeBehavior().charge, stack, nodeBehavior().maxInput)) {
				this.syncContents = true;
				setChanged();
			}
		}

		nodeBehavior().reset();

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
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

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

	private static void giveOrDrop(@Nullable Player player, Level level, BlockPos pos, ItemStack stack) {
		if (player != null) {
			if (player.addItem(stack)) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
						((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1) * 2);
				return;
			}
		}

		ItemEntity itemEntity = new ItemEntity(level,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				stack);
		itemEntity.setDeltaMovement(Vec3.ZERO);
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
			ChargerBlockEntity.this.syncContents = true;
		}
	}
}
