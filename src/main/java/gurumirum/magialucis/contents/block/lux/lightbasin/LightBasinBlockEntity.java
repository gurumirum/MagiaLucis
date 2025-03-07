package gurumirum.magialucis.contents.block.lux.lightbasin;

import com.mojang.datafixers.util.Pair;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.capability.FixedItemStackHandler;
import gurumirum.magialucis.client.render.RenderEffects;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.block.ModBlockStates;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import gurumirum.magialucis.contents.recipe.transfusion.TransfusionRecipeInput;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.impl.luxnet.behavior.SimpleConsumerBehavior;
import gurumirum.magialucis.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Objects;

public class LightBasinBlockEntity extends LuxNodeBlockEntity<SimpleConsumerBehavior> implements Ticker.Both {
	private static final int SYNC_INTERVAL = 3;
	private static final int NO_LUX_INPUT_TICKS_MAX = 30;

	private final LightBasinInventory inventory = new LightBasinInventory();

	private boolean contentsDirty;
	private boolean syncContents;

	private @Nullable ResourceLocation currentRecipeId;
	private @Nullable LuxRecipeEvaluation currentRecipe;
	private double progress;
	private int noLuxInputTicks;

	public LightBasinBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.LIGHT_BASIN.get(), pos, blockState);
	}

	public @NotNull IItemHandlerModifiable inventory() {
		return this.inventory;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && this.level.isClientSide) {
			RenderEffects.light.register(new LightBasinBlockLightEffectProvider(this));
		}
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {}

	@Override
	protected @NotNull SimpleConsumerBehavior createNodeBehavior() {
		return LightBasinBlock.NODE_TYPE.instantiate();
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		return context.side() != null && context.side() != Direction.UP ?
				LinkTestResult.reject() : super.linkWithSource(context);
	}

	@Override
	public void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % 2 == 0 && getBlockState().getValue(ModBlockStates.WORKING)) {
			LuxUtils.addSpreadingLightParticle(level, pos.getX() + 0.5,
					pos.getY() + 1.25,
					pos.getZ() + 0.5,
					0.2f,
					0.025f);
		}
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (this.contentsDirty) {
			this.contentsDirty = false;
			this.syncContents = true;

			var result = level.getRecipeManager()
					.getAllRecipesFor(ModRecipes.LIGHT_BASIN_TYPE.get()).stream()
					.map(h -> Pair.of(h, h.value().evaluate(this.inventory)))
					.filter(p -> p.getSecond().isSuccess())
					.findFirst().orElse(null);

			if (result == null) {
				this.currentRecipeId = null;
				this.currentRecipe = null;
			} else {
				ResourceLocation newRecipeId = result.getFirst().id();
				if (!Objects.equals(newRecipeId, this.currentRecipeId)) {
					this.progress = 0;
					this.currentRecipeId = newRecipeId;
				}
				this.currentRecipe = result.getSecond();
			}
		}

		boolean working = false;

		if (this.currentRecipe != null) {
			Vector3d luxInput = nodeBehavior().min();
			double progress = this.currentRecipe.luxInputCondition().computeProgress(luxInput);
			if (progress > 0) {
				working = true;

				this.progress += progress;
				this.noLuxInputTicks = 0;

				if (this.progress >= this.currentRecipe.processTicks()) {
					if (this.currentRecipe.consumption().apply(this.inventory)) {
						ModUtils.drop(level, this.currentRecipe.result(),
								pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
								0, 0.15, 0);
					}

					this.currentRecipeId = null;
					this.currentRecipe = null;
					this.progress = 0;
					this.noLuxInputTicks = 0;
				}
			} else {
				if (this.noLuxInputTicks >= NO_LUX_INPUT_TICKS_MAX) {
					if (this.progress > 0) this.progress--;
				} else {
					this.noLuxInputTicks++;
				}
			}
			setChanged();
		} else {
			this.progress = 0;
		}

		updateProperty(ModBlockStates.WORKING, working);

		if (this.syncContents && level.getGameTime() % SYNC_INTERVAL == 0) {
			this.syncContents = false;
			syncToClient();
		}
	}

	public boolean dropLastContent(@Nullable Player player) {
		Level level = this.level;
		if (level == null) return false;

		BlockPos pos = getBlockPos();

		for (int i = this.inventory.getSlots() - 1; i >= 0; i--) {
			ItemStack stack = this.inventory.extractItem(i, 64, level.isClientSide);
			if (stack.isEmpty()) continue;

			if (!level.isClientSide) {
				ModUtils.giveOrDrop(player, level, stack,
						pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
						0, 0, 0);
			}

			return true;
		}

		return false;
	}

	public void dropAllContents(@Nullable Player player) {
		Level level = this.level;
		if (level == null) return;

		BlockPos pos = getBlockPos();

		for (int i = 0; i < this.inventory.getSlots(); i++) {
			if (!level.isClientSide) {
				ModUtils.giveOrDrop(player, level, this.inventory.getStackInSlot(i),
						pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
						0, 0, 0);
				this.inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		tag.put("inventory", this.inventory.serializeNBT(lookupProvider));

		if (context.isSaveLoad()) {
			if (this.currentRecipeId != null) {
				tag.putString("currentRecipe", this.currentRecipeId.toString());
				tag.putDouble("progress", this.progress);
			}
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
			if (tag.contains("currentRecipe", Tag.TAG_STRING)) {
				this.currentRecipeId = ResourceLocation.tryParse(tag.getString("currentRecipe"));
				this.progress = tag.getDouble("progress");
			} else {
				this.currentRecipeId = null;
				this.progress = 0;
			}
		}

		this.contentsDirty = true;
	}

	private final class LightBasinInventory extends FixedItemStackHandler implements TransfusionRecipeInput {
		public LightBasinInventory() {
			super(4);
		}

		@Override
		protected void onContentsChanged(int slot) {
			LightBasinBlockEntity.this.contentsDirty = true;
			LightBasinBlockEntity.this.setChanged();
		}

		@Override
		public @NotNull ItemStack getItem(int index) {
			return getStackInSlot(index);
		}

		@Override
		public int size() {
			return getSlots();
		}
	}
}
