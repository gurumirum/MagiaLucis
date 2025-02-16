package gurumirum.magialucis.contents.block.lux.connector;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.GemContainerBlock;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.data.GemItemData;
import gurumirum.magialucis.contents.data.GemStatLogic;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConnectorBlockEntity extends LuxNodeBlockEntity<DynamicLuxNodeBehavior>
		implements GemContainerBlock.GemContainer {
	private ItemStack stack = ItemStack.EMPTY;

	public ConnectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.CONNECTOR.get(), pos, blockState);
	}

	@Override
	public @NotNull ItemStack stack() {
		return this.stack;
	}

	@Override
	public void setStack(@NotNull ItemStack stack) {
		this.stack = stack;
		if (luxNodeId() != LuxNet.NO_ID) {
			nodeBehavior().setStats(GemStatLogic.getOrDefault(stack));
		}
		setChanged();
		syncToClient();
	}

	@Override public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new BlockLightEffectProvider<>(this).raySize(0));
			RenderEffects.prism.register(new ConnectorBlockPrismEffect(this));
		}
	}

	@Override
	protected @NotNull DynamicLuxNodeBehavior createNodeBehavior() {
		return new DynamicLuxNodeBehavior(GemStatLogic.get(this.stack));
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {
		Level level = getLevel();
		if (level == null) return;

		Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
		BlockPos facingPos = getBlockPos().relative(facing);

		BlockState state = level.getBlockState(facingPos);
		boolean extendedReach = state.isCollisionShapeFullBlock(level, facingPos);

		if (!linkCollector.directLinkToInWorldNode(this,
				facingPos, facing.getOpposite(), 0, null,
				1, !extendedReach)) {
			if (extendedReach) {
				linkCollector.directLinkToInWorldNode(this,
						getBlockPos().relative(facing, 2), facing.getOpposite(), 0, null,
						1, true);
			}
		}
	}

	@Override
	public @NotNull LinkTestResult linkWithSource(@NotNull LinkContext context) {
		if (context.side() != null && context.side() == getBlockState().getValue(BlockStateProperties.FACING)) {
			return LinkTestResult.reject();
		}
		return LinkTestResult.linkable(luxNodeId());
	}

	@Override
	protected void save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (!this.stack.isEmpty()) {
			tag.put("item", this.stack.save(lookupProvider));
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider, SaveLoadContext context) {
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
		GemItemData gemItemData = componentInput.get(ModDataComponents.GEM_ITEM.get());
		this.stack = gemItemData != null ? gemItemData.stack().copy() : ItemStack.EMPTY;
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (!this.stack.isEmpty())
			components.set(ModDataComponents.GEM_ITEM.get(), new GemItemData(this.stack.copy()));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item");
	}
}
