package gurumirum.magialucis.jei;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import gurumirum.magialucis.contents.data.ItemAugment;
import gurumirum.magialucis.contents.recipe.artisanry.AugmentRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AugmentRecipeCategory extends BaseArtisanryRecipeCategory<AugmentRecipe> {
	public static final RecipeType<AugmentRecipe> RECIPE_TYPE = new RecipeType<>(
			MagiaLucisApi.id("artisanry_augment"), AugmentRecipe.class);

	private final IDrawable icon;

	public AugmentRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		this.icon = guiHelper.createDrawableItemLike(ModBlocks.CITRINE_LIGHTLOOM);
	}

	@Override
	public @NotNull RecipeType<AugmentRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.magialucis.artisanry_augment");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return this.icon;
	}

	@Override
	protected void computeRecipeSlots(
			@NotNull IRecipeLayoutBuilder builder, @NotNull AugmentRecipe recipe, @NotNull IFocusGroup focuses,
			Int2ObjectMap<List<ItemStack>> inputs, AtomicReference<List<ItemStack>> outputs) {
		List<ItemStack> inputItems = new ArrayList<>(), resultItems = new ArrayList<>();

		BuiltInRegistries.ITEM.getDataMap(ModDataMaps.AUGMENT_SPEC).forEach((key, spec) -> {
			for (AugmentRecipe.AugmentOp op : recipe.augments()) {
				if (!op.optional() && !spec.contains(op.augment())) return;
			}

			for (Holder<Augment> h : recipe.precursor()) {
				if (!spec.contains(h)) return;
			}

			Item item = BuiltInRegistries.ITEM.get(key);
			if (item == null || item == Items.AIR) return;

			AugmentStateMap inputAugments = new AugmentStateMap();
			AugmentStateMap resultAugments = new AugmentStateMap();

			for (Holder<Augment> h : recipe.precursor()) inputAugments.push(h, AugmentState.YES);
			for (Holder<Augment> h : recipe.incompatible()) inputAugments.push(h, AugmentState.NO);

			for (AugmentRecipe.AugmentOp op : recipe.augments()) {
				if (op.optional() && !spec.contains(op.augment())) continue;

				inputAugments.push(op.augment(), AugmentState.of(op.remove(), op.optional()));
				resultAugments.push(op.augment(), AugmentState.of(!op.remove(), op.optional()));
			}

			if (inputAugments.hasInvalidState() || resultAugments.hasInvalidState()) return;

			inputItems.add(inputAugments.toItemStack(item));
			resultItems.add(resultAugments.toItemStack(item));
		});

		inputs.put(4, inputItems);
		outputs.set(resultItems);
	}

	@Override
	protected void processRecipeSlots(
			@NotNull IRecipeLayoutBuilder builder, @NotNull AugmentRecipe recipe, @NotNull IFocusGroup focuses,
			List<IRecipeSlotBuilder> inputSlots, IRecipeSlotBuilder outputSlot) {
		super.processRecipeSlots(builder, recipe, focuses, inputSlots, outputSlot);
		builder.createFocusLink(inputSlots.get(4), outputSlot);
	}

	public static final class AugmentStateMap {
		private final Map<Holder<Augment>, AugmentState> states = new Object2ObjectLinkedOpenHashMap<>();

		public void push(Holder<Augment> augment, AugmentState newState) {
			this.states.compute(augment, (_h, prev) -> AugmentState.combine(prev, newState));
		}

		public boolean hasInvalidState() {
			for (AugmentState state : this.states.values()) {
				if (state == AugmentState.IMPOSSIBLE) return true;
			}
			return false;
		}

		public ItemStack toItemStack(Item item) {
			ItemStack input = new ItemStack(item);
			input.set(ModDataComponents.AUGMENTS, ItemAugment.of(new ObjectLinkedOpenHashSet<>(
					this.states.entrySet().stream()
							.filter(e2 -> e2.getValue().exists())
							.map(Map.Entry::getKey)
							.iterator())));
			return input;
		}
	}

	public enum AugmentState {
		YES,
		NO,
		YES_MAYBE,
		NO_MAYBE,
		IMPOSSIBLE;

		public boolean exists() {
			return this == YES || this == YES_MAYBE;
		}

		public boolean isOptional() {
			return this == YES_MAYBE || this == NO_MAYBE;
		}

		public static AugmentState of(boolean exists, boolean optional) {
			return exists ?
					optional ? YES_MAYBE : YES :
					optional ? NO_MAYBE : NO;
		}

		public static AugmentState combine(@Nullable AugmentState a, @Nullable AugmentState b) {
			if (a == null) return b;
			if (b == null) return a;
			if (a == IMPOSSIBLE || b == IMPOSSIBLE) return IMPOSSIBLE;
			if (a == b) return a;

			if (a.exists() != b.exists()) {
				if (a.isOptional()) {
					return b.isOptional() ? YES_MAYBE : b;
				} else {
					return b.isOptional() ? a : IMPOSSIBLE;
				}
			} else return a.isOptional() ? b : a;
		}
	}
}
