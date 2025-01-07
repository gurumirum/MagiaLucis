package gurumirum.gemthing.contents.block.lux.relay;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.capability.LuxStat;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModBlockEntities;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNetEvent;
import gurumirum.gemthing.impl.LuxNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RelayBlockEntity extends BasicRelayBlockEntity {
	private boolean hasOutboundConnection;

	private ItemStack stack = ItemStack.EMPTY;

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.RELAY.get(), pos, blockState);
	}

	public ItemStack stack() {
		return this.stack;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
		LuxNet luxNet = getLuxNet();
		if (luxNet != null) luxNet.queuePropertyUpdate(luxNodeId());
		setChanged();
		syncToClient();
	}

	@Override
	public void updateProperties(@NotNull LuxNet luxNet, @NotNull LuxNode node) {
		LuxStat gemStat = this.stack.getCapability(ModCapabilities.GEM_STAT);
		node.setStats(gemStat != null ? gemStat : LuxStat.NULL);
		setHasOutboundConnection(!node.outboundNodes().isEmpty());
	}

	@Override
	public void connectionUpdated(LuxNetEvent.ConnectionUpdated connectionUpdated) {
		if (luxNodeId() == connectionUpdated.sourceNode()) {
			setHasOutboundConnection(connectionUpdated.destinationNode() != NO_ID);
		}
	}

	public boolean hasOutboundConnection() {
		return this.hasOutboundConnection;
	}

	private void setHasOutboundConnection(boolean value) {
		if (this.hasOutboundConnection == value) return;
		this.hasOutboundConnection = value;
		syncToClient();
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.save(lookupProvider));
		}

		if (context.isSync()) {
			tag.putBoolean("hasOutboundConnection", this.hasOutboundConnection);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		if (tag.contains("item")) {
			this.stack = ItemStack.parse(lookupProvider, Objects.requireNonNull(tag.get("item"))).orElseGet(() -> {
				GemthingMod.LOGGER.error("Cannot parse item of a relay");
				return ItemStack.EMPTY;
			});
		} else {
			this.stack = ItemStack.EMPTY;
		}

		if (context.isSync()) {
			this.hasOutboundConnection = tag.getBoolean("hasOutboundConnection");
		}
	}

	@Override
	protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		this.stack = componentInput.getOrDefault(Contents.ITEM_STACK.get(), ItemStack.EMPTY);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		components.set(Contents.ITEM_STACK.get(), this.stack);
	}
}
