package gurumirum.magialucis.contents.block.artisanrytable;

import com.mojang.datafixers.util.Pair;
import gurumirum.magialucis.capability.impl.FixedItemStackHandler;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.block.BlockEntityBase;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomBlockEntity;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class ArtisanryTableBlockEntity extends BlockEntityBase implements Ticker.Server, MenuProvider {
	public static final int SLOTS_CONTAINER = 0;
	public static final int SLOTS_CONTAINER_COUNT = 12;
	public static final int SLOTS_GRID = SLOTS_CONTAINER + SLOTS_CONTAINER_COUNT;
	public static final int SLOTS_GRID_COUNT = 9;
	public static final int SLOTS_OUTPUT = SLOTS_GRID + SLOTS_GRID_COUNT;
	public static final int SLOTS = SLOTS_OUTPUT + 1;

	private static final int NO_LUX_INPUT_TICKS_MAX = 30;

	private final ArtisanryTableInventory inventory = new ArtisanryTableInventory();

	private @Nullable ResourceLocation currentRecipeId;
	private @Nullable LuxRecipeEvaluation currentRecipe;
	private boolean recipeChecked;
	private double progress;
	private int noLuxInputTicks;

	private @Nullable LuxRecipeEvaluation recipePreview;
	private boolean recipePreviewChecked;

	private boolean gridContentsDirty;

	public ArtisanryTableBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.ARTISANRY_TABLE.get(), pos, blockState);
	}

	public @NotNull IItemHandlerModifiable inventory() {
		return this.inventory;
	}

	public boolean recipeInProgress() {
		return this.currentRecipe != null;
	}

	public @Nullable LuxRecipeEvaluation currentRecipe() {
		return this.currentRecipe;
	}

	public double progress() {
		return this.progress;
	}

	public int totalProgress() {
		return this.currentRecipe != null ? this.currentRecipe.processTicks() : 0;
	}

	public void beginRecipe() {
		if (this.level == null || this.level.isClientSide) return;
		if (this.recipeChecked) return;
		this.recipeChecked = true;

		var result = this.level.getRecipeManager()
				.getAllRecipesFor(ModRecipes.ARTISANRY_TYPE.get()).stream()
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

		this.recipePreview = null;
		this.recipePreviewChecked = false;
	}

	public @Nullable LuxRecipeEvaluation previewRecipe() {
		if (this.level == null || this.level.isClientSide) return null;
		if (this.currentRecipe != null) return this.currentRecipe;

		if (!this.recipePreviewChecked) {
			this.recipePreviewChecked = true;
			this.recipePreview = this.level.getRecipeManager()
					.getAllRecipesFor(ModRecipes.ARTISANRY_TYPE.get()).stream()
					.map(h -> h.value().evaluate(this.inventory))
					.filter(LuxRecipeEvaluation::isSuccess)
					.findFirst().orElse(null);
		}

		return this.recipePreview;
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (this.gridContentsDirty) {
			this.gridContentsDirty = false;
			this.recipeChecked = false;
			if (this.currentRecipe != null) {
				beginRecipe();
			} else {
				this.recipePreview = null;
				this.recipePreviewChecked = false;
			}
		}

		if (this.currentRecipe != null) {
			BlockPos lightLoomPos = getBlockPos().above().relative(getBlockState()
					.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
			LightLoomBlockEntity lightLoom = level.isLoaded(lightLoomPos) &&
					level.getBlockEntity(lightLoomPos) instanceof LightLoomBlockEntity ll ?
					ll : null;

			Vector3d luxInput = lightLoom != null ? lightLoom.luxInput(new Vector3d()) : new Vector3d();
			double v = this.currentRecipe.luxInputCondition().computeProgress(luxInput);
			if (v > 0) {
				this.noLuxInputTicks = 0;

				int processTicks = this.currentRecipe.processTicks();
				if (this.progress < processTicks) this.progress += v;
				if (this.progress >= processTicks) {
					if (this.inventory.insertItem(SLOTS_OUTPUT, this.currentRecipe.result(), true).isEmpty()) {
						if (this.currentRecipe.consumption().apply(
								new RangedWrapper(this.inventory, SLOTS_GRID, SLOTS_GRID + SLOTS_GRID_COUNT))) {
							this.inventory.insertItem(SLOTS_OUTPUT, this.currentRecipe.result(), false);
						}

						this.currentRecipeId = null;
						this.currentRecipe = null;
						this.progress = 0;
						this.noLuxInputTicks = 0;
						this.recipeChecked = false;
						this.recipePreview = null;
						this.recipePreviewChecked = false;
					}
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
	}

	@Override
	public @NotNull Component getDisplayName() {
		return Component.translatable("container.magialucis.artisanry_table");
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory,
	                                                  @NotNull Player player) {
		return new ArtisanryTableMenu(containerId, playerInventory, this);
	}

	@Override
	protected void save(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);

		if (context.isSaveLoad()) {
			tag.put("inventory", this.inventory.serializeNBT(lookupProvider));
			if (this.currentRecipeId != null) {
				tag.putString("currentRecipe", this.currentRecipeId.toString());
				tag.putDouble("progress", this.progress);
			}
		}
	}

	@Override
	protected void load(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);

		if (context.isSaveLoad()) {
			this.inventory.deserializeNBT(lookupProvider, tag.getCompound("inventory"));
			if (tag.contains("currentRecipe", Tag.TAG_STRING)) {
				this.currentRecipeId = ResourceLocation.tryParse(tag.getString("currentRecipe"));
				this.progress = tag.getDouble("progress");
			} else {
				this.currentRecipeId = null;
				this.progress = 0;
			}
			this.gridContentsDirty = true;
		}
	}

	private final class ArtisanryTableInventory extends FixedItemStackHandler implements ArtisanryRecipeInput {
		private final List<ItemStack> list = new AbstractList<>() {
			@Override public int size() {
				return ArtisanryTableInventory.this.size();
			}
			@Override public ItemStack get(int i) {
				return ArtisanryTableInventory.this.getItem(i);
			}
		};

		public ArtisanryTableInventory() {
			super(SLOTS);
		}

		@Override
		protected void onContentsChanged(int slot) {
			if (slot >= SLOTS_GRID && slot < SLOTS_GRID + SLOTS_GRID_COUNT) {
				ArtisanryTableBlockEntity.this.gridContentsDirty = true;
			}
			ArtisanryTableBlockEntity.this.setChanged();
		}

		@Override
		public @NotNull CraftingInput asCraftingInput() {
			return CraftingInput.of(3, 3, this.list);
		}

		@Override
		public @NotNull ItemStack getItem(int index) {
			if (index < 0 || index >= 9) {
				throw new IndexOutOfBoundsException(index);
			}
			return ArtisanryTableBlockEntity.this.inventory.getStackInSlot(SLOTS_GRID + index);
		}

		@Override
		public int size() {
			return 9;
		}
	}
}
