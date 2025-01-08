package gurumirum.gemthing.contents.block.lux.relay;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.capability.LuxStat;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModBlockEntities;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.gemthing.impl.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RelayBlockEntity extends BasicRelayBlockEntity {
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
		if (luxNet != null) luxNet.queueStatUpdate(luxNodeId());
		setChanged();
		syncToClient();
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(@NotNull LuxNet luxNet) {
		return this.stack.getCapability(ModCapabilities.GEM_STAT);
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.save(lookupProvider));
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
	}

	@Override
	protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		RelayItemData relayItemData = componentInput.get(Contents.RELAY_ITEM.get());
		this.stack = relayItemData != null ? relayItemData.stack().copy() : ItemStack.EMPTY;
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (!this.stack.isEmpty()) components.set(Contents.RELAY_ITEM.get(), new RelayItemData(this.stack.copy()));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item");
	}
}
