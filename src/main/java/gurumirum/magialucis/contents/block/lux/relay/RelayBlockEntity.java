package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.impl.luxnet.LuxNet;
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
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			LightEffectRender.register(new BlockLightEffectProvider<>(this));
		}
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
				MagiaLucisMod.LOGGER.error("Cannot parse item of a relay");
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
