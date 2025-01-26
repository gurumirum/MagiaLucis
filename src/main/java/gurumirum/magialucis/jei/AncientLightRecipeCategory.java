package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.recipe.ancientlight.AncientLightRecipe;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AncientLightRecipeCategory implements IRecipeCategory<AncientLightRecipe> {
	public static final RecipeType<AncientLightRecipe> RECIPE_TYPE = new RecipeType<>(
			MagiaLucisMod.id("ancient_light"), AncientLightRecipe.class);

	private static final Hash.Strategy<ItemStack> ITEM_STACK_STRATEGY = new Hash.Strategy<>() {
		@Override public int hashCode(ItemStack stack) {
			return stack == null ? 0 : ItemStack.hashItemAndComponents(stack);
		}

		@Override public boolean equals(ItemStack a, ItemStack b) {
			return a == null ? b == null : b != null && ItemStack.isSameItemSameComponents(a, b);
		}
	};

	private final IDrawable icon;

	public AncientLightRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(Wands.ANCIENT_LIGHT);
	}

	@Override
	public @NotNull RecipeType<AncientLightRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.magialucis.ancient_light");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, AncientLightRecipe recipe, @NotNull IFocusGroup focuses) {
		ObjectLinkedOpenCustomHashSet<ItemStack> inputs = new ObjectLinkedOpenCustomHashSet<>(ITEM_STACK_STRATEGY);

		for (Holder<Block> block : recipe.blocks()) {
			Item item = block.value().asItem();
			if (item == Items.AIR) continue;
			inputs.add(new ItemStack(item));
		}

		if (!inputs.isEmpty()) {
			builder.addInputSlot(1, 9)
					.setStandardSlotBackground()
					.addItemStacks(inputs.stream().toList());
		}

		int i = 0;

		// TODO need better UI for multiple outputs
		for (ItemStack stack : recipe.getResultItemView()) {
			builder.addOutputSlot(61 + 18 * i, 9)
					.setOutputSlotBackground()
					.addItemStack(stack);
			i++;
		}
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, AncientLightRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addRecipeArrow().setPosition(26, 9);

		double cookTimeSeconds = Math.floor((recipe.getProcessTicksView() / 20.0) * 100) / 100;

		if (Double.isFinite(cookTimeSeconds) && cookTimeSeconds > 0) {
			Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
			builder.addText(timeString, getWidth() - 20, 10)
					.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
					.setTextAlignment(HorizontalAlignment.RIGHT)
					.setTextAlignment(VerticalAlignment.BOTTOM)
					.setColor(0xFF808080);
		}
	}

	@Override
	public int getWidth() {
		return 82;
	}

	@Override
	public int getHeight() {
		return 45;
	}
}
