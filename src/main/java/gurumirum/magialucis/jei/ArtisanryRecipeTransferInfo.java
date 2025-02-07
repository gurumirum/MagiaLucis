package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.ModMenus;
import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableMenu;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipe;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableBlockEntity.*;

public class ArtisanryRecipeTransferInfo<R extends ArtisanryRecipe> implements IRecipeTransferInfo<ArtisanryTableMenu, R> {
	private final RecipeType<R> type;

	public ArtisanryRecipeTransferInfo(RecipeType<R> type) {
		this.type = type;
	}

	@Override
	public @NotNull Class<? extends ArtisanryTableMenu> getContainerClass() {
		return ArtisanryTableMenu.class;
	}

	@Override
	public @NotNull Optional<MenuType<ArtisanryTableMenu>> getMenuType() {
		return Optional.of(ModMenus.ARTISANRY_TABLE.get());
	}

	@Override
	public @NotNull RecipeType<R> getRecipeType() {
		return this.type;
	}

	@Override
	public boolean canHandle(@NotNull ArtisanryTableMenu menu, @NotNull R recipe) {
		return true;
	}

	@Override
	public @NotNull List<Slot> getRecipeSlots(@NotNull ArtisanryTableMenu menu, @NotNull R recipe) {
		return menu.slots.subList(SLOTS_GRID, SLOTS_GRID + SLOTS_GRID_COUNT);
	}

	@Override
	public @NotNull List<Slot> getInventorySlots(@NotNull ArtisanryTableMenu menu, @NotNull R recipe) {
		List<Slot> list = new ArrayList<>();
		list.addAll(menu.slots.subList(SLOTS_CONTAINER, SLOTS_CONTAINER + SLOTS_CONTAINER_COUNT));
		list.addAll(menu.slots.subList(SLOTS, SLOTS + 36));
		return list;
	}
}
