package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.InputPattern;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BaseArtisanryRecipeCategory<R extends ArtisanryRecipe> implements IRecipeCategory<R> {
	private final ICraftingGridHelper craftingGridHelper;

	public BaseArtisanryRecipeCategory(IGuiHelper guiHelper) {
		this.craftingGridHelper = guiHelper.createCraftingGridHelper();
	}

	@Override
	public int getWidth() {
		return 116;
	}

	@Override
	public int getHeight() {
		return 54;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, @NotNull IFocusGroup focuses) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) throw new NullPointerException("level must not be null.");

		Int2ObjectMap<List<ItemStack>> inputs = new Int2ObjectOpenHashMap<>();
		AtomicReference<List<ItemStack>> outputs = new AtomicReference<>();

		computeRecipeSlots(builder, recipe, focuses, inputs, outputs);

		InputPattern<IngredientStack> pattern = recipe.pattern();
		List<IRecipeSlotBuilder> inputSlots = this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK,
				IntStream.range(0, pattern.inputs().size())
						.mapToObj(i -> Objects.requireNonNullElseGet(inputs.get(i),
								() -> pattern.inputs().get(i).toItemList()))
						.toList(),
				pattern.width(), pattern.height());

		IRecipeSlotBuilder outputSlot = this.craftingGridHelper.createAndSetOutputs(builder, Objects.requireNonNullElseGet(
				outputs.get(), () -> List.of(recipe.getResultItem(level.registryAccess()))));

		processRecipeSlots(builder, recipe, focuses, inputSlots, outputSlot);
	}

	protected void computeRecipeSlots(
			@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, @NotNull IFocusGroup focuses,
			Int2ObjectMap<List<ItemStack>> inputs, AtomicReference<List<ItemStack>> outputs) {}

	protected void processRecipeSlots(
			@NotNull IRecipeLayoutBuilder builder, @NotNull R recipe, @NotNull IFocusGroup focuses,
			List<IRecipeSlotBuilder> inputSlots, IRecipeSlotBuilder outputSlot) {
		InputPattern<IngredientStack> pattern = recipe.pattern();
		for (int i = 0; i < pattern.inputs().size(); i++) {
			IngredientStack is = pattern.inputs().get(i);

			if (is.count() == 0 && !is.ingredient().isEmpty()) {
				int index = getCraftingIndex(i, pattern.width(), pattern.height());

				inputSlots.get(index).setOverlay(new IDrawable() {
					@Override public int getWidth() {
						return 16;
					}
					@Override public int getHeight() {
						return 16;
					}
					@Override public void draw(@NotNull GuiGraphics guiGraphics, int xOffset, int yOffset) {
						Minecraft mc = Minecraft.getInstance();
						String s = ChatFormatting.YELLOW + "∞";
						guiGraphics.drawString(mc.font, s, xOffset + 19 - 2 - mc.font.width(s), yOffset + 6 + 3, 16777215, true);
					}
				}, 0, 0).addRichTooltipCallback((recipeSlotView, tooltip) -> {
					tooltip.add(Component.literal("NC"));
				});
			}
		}

	}

	@Override
	public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull R recipe, @NotNull IFocusGroup focuses) {
		if (recipe.processTicks() > 0) {
			builder.addAnimatedRecipeArrow(recipe.processTicks())
					.setPosition(61, 19);
			JeiLogic.processTimeWidget(this, builder, recipe.processTicks());
		} else {
			builder.addRecipeArrow()
					.setPosition(61, 19);
		}

		LuxInputCondition luxInputCondition = recipe.luxInputCondition();
		boolean usesLux = !luxInputCondition.equals(LuxInputCondition.none());
		if (usesLux) {
			builder.addWidget(new LuxInputWidget(luxInputCondition, 64, 2));
		}
	}

	// actual jei code lol
	private static int getCraftingIndex(int i, int width, int height) {
		int index;
		if (width == 1) {
			if (height == 3) {
				index = (i * 3) + 1;
			} else if (height == 2) {
				index = (i * 3) + 1;
			} else {
				index = 4;
			}
		} else if (height == 1) {
			index = i + 3;
		} else if (width == 2) {
			index = i;
			if (i > 1) {
				index++;
				if (i > 3) {
					index++;
				}
			}
		} else if (height == 2) {
			index = i + 3;
		} else {
			index = i;
		}
		return index;
	}
}
