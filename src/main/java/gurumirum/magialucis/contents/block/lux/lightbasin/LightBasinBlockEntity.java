package gurumirum.magialucis.contents.block.lux.lightbasin;

import com.mojang.datafixers.util.Pair;
import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.contents.recipe.TransfusionRecipeEvaluation;
import gurumirum.magialucis.contents.recipe.TransfusionRecipeInput;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Objects;

public class LightBasinBlockEntity extends LuxNodeBlockEntity<LightBasinBehavior> implements Ticker.Server {
	public static final LuxStat STAT = GemStats.BRIGHTSTONE;

	private static final int SYNC_INTERVAL = 3;
	private static final int NO_LUX_INPUT_TICKS_MAX = 30;

	private final LightBasinInventory inventory = new LightBasinInventory();
	private final Vector3d luxInput = new Vector3d();

	private boolean contentsDirty;
	private boolean syncContents;

	private @Nullable ResourceLocation currentRecipeId;
	private @Nullable TransfusionRecipeEvaluation currentRecipe;
	private int progress;
	private int noLuxInputTicks;

	public LightBasinBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.LIGHT_BASIN.get(), pos, blockState);
	}

	public @NotNull IItemHandlerModifiable inventory() {
		return this.inventory;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {}

	@Override
	protected @NotNull LightBasinBehavior createNodeBehavior() {
		return new LightBasinBehavior();
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		return context.side() != null && context.side() != Direction.UP ?
				LinkTestResult.reject() : super.linkWithSource(context);
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

		if (this.currentRecipe != null) {
			if (this.currentRecipe.testLuxInput(this.luxInput)) {
				this.progress++;
				this.noLuxInputTicks = 0;

				if (this.progress >= this.currentRecipe.processTicks()) {
					if (consume(this.currentRecipe.consumption())) {
						drop(level, pos, this.currentRecipe.result());
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
		} else {
			this.progress = 0;
		}

		if (this.syncContents && level.getGameTime() % SYNC_INTERVAL == 0) {
			this.syncContents = false;
			syncToClient();
		}
	}

	private boolean consume(Int2IntMap consumption) {
		for (var e : consumption.int2IntEntrySet()) {
			int index = e.getIntKey();
			if (index < 0 || index >= this.inventory.getSlots()) return false;

			int count = e.getIntValue();
			if (this.inventory.getStackInSlot(index).getCount() < count) return false;
		}

		for (var e : consumption.int2IntEntrySet()) {
			this.inventory.extractItem(e.getIntKey(), e.getIntValue(), false);
		}

		return true;
	}

	public boolean dropLastContent() {
		Level level = this.level;
		if (level == null) return false;

		BlockPos pos = getBlockPos();

		for (int i = this.inventory.getSlots() - 1; i >= 0; i--) {
			ItemStack stack = this.inventory.extractItem(i, 64, level.isClientSide);
			if (stack.isEmpty()) continue;

			if (!level.isClientSide) {
				drop(level, pos, stack);
			}

			return true;
		}

		return false;
	}

	public void dropAllContents() {
		Level level = this.level;
		if (level == null) return;

		BlockPos pos = getBlockPos();

		for (int i = 0; i < this.inventory.getSlots(); i++) {
			if (!level.isClientSide) {
				drop(level, pos, this.inventory.getStackInSlot(i));
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
				tag.putInt("progress", this.progress);
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
				this.progress = tag.getInt("progress");
			} else {
				this.currentRecipeId = null;
				this.progress = 0;
			}
		}

		this.contentsDirty = true;
	}

	private static void drop(Level level, BlockPos pos, ItemStack stack) {
		ItemEntity itemEntity = new ItemEntity(level,
				pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
				stack);
		level.addFreshEntity(itemEntity);
	}

	private final class LightBasinInventory extends ItemStackHandler implements TransfusionRecipeInput {
		public LightBasinInventory() {
			super(4);
		}

		@Override
		public void setSize(int size) {} // no-op

		@Override
		protected void onContentsChanged(int slot) {
			LightBasinBlockEntity.this.contentsDirty = true;
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
