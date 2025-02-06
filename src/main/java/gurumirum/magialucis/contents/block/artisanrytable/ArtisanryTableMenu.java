package gurumirum.magialucis.contents.block.artisanrytable;

import gurumirum.magialucis.contents.ModMenus;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import gurumirum.magialucis.utils.ModUtils;
import gurumirum.magialucis.utils.QuickMoveLogic;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static gurumirum.magialucis.client.SharedGUI.*;
import static gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableBlockEntity.*;

public class ArtisanryTableMenu extends AbstractContainerMenu {
	public static final int ARTISANRY_TABLE_WIDTH = 176;
	public static final int ARTISANRY_TABLE_HEIGHT = 88;

	public static final int WIDTH = Math.max(PLAYER_INV_WIDTH, ARTISANRY_TABLE_WIDTH);
	public static final int HEIGHT = ARTISANRY_TABLE_HEIGHT + PLAYER_INV_LABEL_HEIGHT + PLAYER_INV_HEIGHT;

	public static final int PLAYER_INV_LABEL_Y = ARTISANRY_TABLE_HEIGHT;
	public static final int PLAYER_INV_Y = PLAYER_INV_LABEL_Y + PLAYER_INV_LABEL_HEIGHT;

	public static final int BUTTON_BEGIN_RECIPE = 1;

	private static final int CONTAINER_DATA_COUNT = 4;

	private static final QuickMoveLogic quickMoveLogic = QuickMoveLogic.builder(SLOTS)
			.insertion(stack -> true, SLOTS_CONTAINER, SLOTS_CONTAINER_COUNT, false, false)
			.build();

	private final @Nullable ArtisanryTableBlockEntity artisanryTable;
	private final ContainerData containerData;

	public ArtisanryTableMenu(int containerId, Inventory playerInv) {
		super(ModMenus.ARTISANRY_TABLE.get(), containerId);
		this.artisanryTable = null;
		this.containerData = new SimpleContainerData(CONTAINER_DATA_COUNT);
		init(playerInv, new ItemStackHandler(SLOTS));
	}

	public ArtisanryTableMenu(int containerId, Inventory playerInv, @NotNull ArtisanryTableBlockEntity artisanryTable) {
		super(ModMenus.ARTISANRY_TABLE.get(), containerId);
		this.artisanryTable = artisanryTable;
		this.containerData = new ArtisanryTableContainerData(artisanryTable);
		init(playerInv, artisanryTable.inventory());
	}

	private void init(Inventory playerInv, IItemHandlerModifiable artisanryTable) {
		for (int i = 0; i < SLOTS_CONTAINER_COUNT; i++) {
			addSlot(new SlotItemHandler(artisanryTable, SLOTS_CONTAINER + i,
					13 + i % 3 * 18, 9 + i / 3 * 18));
		}

		for (int i = 0; i < SLOTS_GRID_COUNT; i++) {
			addSlot(new SlotItemHandler(artisanryTable, SLOTS_GRID + i,
					79 + i % 3 * 18, 9 + i / 3 * 18));
		}

		addSlot(new SlotItemHandler(artisanryTable, SLOTS_OUTPUT, 145, 41) {
			@Override
			public @NotNull ItemStack getItem() {
				ItemStack stack = super.getItem();
				if (!stack.isEmpty()) return stack;
				ArtisanryTableBlockEntity artisanryTable = ArtisanryTableMenu.this.artisanryTable;
				if (artisanryTable != null) {

					LuxRecipeEvaluation preview = artisanryTable.recipeInProgress() ?
							artisanryTable.currentRecipe() : artisanryTable.previewRecipe();
					if (preview != null) return preview.result();
				}
				return ItemStack.EMPTY;
			}

			@Override
			public boolean mayPlace(@NotNull ItemStack stack) {
				return false;
			}

			@Override public boolean mayPickup(@NotNull Player player) {
				return ArtisanryTableMenu.this.artisanryTable != null ? super.mayPickup(player) :
						ArtisanryTableMenu.this.hasOutputItem();
			}

			@Override public boolean isFake() {
				return ArtisanryTableMenu.this.artisanryTable == null && !ArtisanryTableMenu.this.hasOutputItem();
			}
		});

		ModUtils.addInventorySlots(playerInv, 0, PLAYER_INV_Y, this::addSlot);

		addDataSlots(Objects.requireNonNull(this.containerData));
	}

	public int progress() {
		return this.containerData.get(0);
	}

	public int totalProgress() {
		return this.containerData.get(1);
	}

	public boolean recipeInProgress() {
		return totalProgress() >= 0;
	}

	public boolean hasOutputItem() {
		return this.containerData.get(2) != 0;
	}

	public boolean canBeginRecipe() {
		return this.containerData.get(3) != 0;
	}

	@Override
	public boolean clickMenuButton(@NotNull Player player, int id) {
		if (id == BUTTON_BEGIN_RECIPE) {
			if (this.artisanryTable != null) {
				this.artisanryTable.beginRecipe();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return this.artisanryTable == null || Container.stillValidBlockEntity(this.artisanryTable, player);
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
		return quickMoveLogic.perform(this, player, index);
	}

	private record ArtisanryTableContainerData(
			@NotNull ArtisanryTableBlockEntity artisanryTable
	) implements ContainerData {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> this.artisanryTable.progress();
				case 1 -> this.artisanryTable.recipeInProgress() ? this.artisanryTable.totalProgress() : -1;
				case 2 -> this.artisanryTable.inventory().getStackInSlot(SLOTS_OUTPUT).isEmpty() ? 0 : 1;
				case 3 -> {
					if (this.artisanryTable.recipeInProgress()) yield 0;
					LuxRecipeEvaluation preview = this.artisanryTable.previewRecipe();
					yield preview != null && preview.isSuccess() ? 1 : 0;
				}
				default -> throw new IllegalStateException("Unexpected value: " + index);
			};
		}

		@Override
		public void set(int index, int value) {}

		@Override
		public int getCount() {
			return CONTAINER_DATA_COUNT;
		}
	}
}
