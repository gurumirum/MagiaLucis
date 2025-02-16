package gurumirum.magialucis.contents.recipe.artisanry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.contents.data.ItemAugment;
import gurumirum.magialucis.contents.recipe.*;
import gurumirum.magialucis.contents.recipe.crafting.CraftingLogic;
import gurumirum.magialucis.utils.ModUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AugmentRecipe implements ArtisanryRecipe {
	private final InputPattern<IngredientStack> pattern;
	private final List<AugmentOp> augments;
	private final HolderSet<Augment> precursor;
	private final HolderSet<Augment> incompatible;
	private final int processTicks;
	private final LuxInputCondition luxInputCondition;

	public AugmentRecipe(InputPattern<IngredientStack> pattern, List<AugmentOp> augments,
	                     HolderSet<Augment> precursor, HolderSet<Augment> incompatible,
	                     int processTicks, LuxInputCondition luxInputCondition) {
		this.pattern = pattern;
		this.augments = augments;
		this.precursor = precursor;
		this.incompatible = incompatible;
		this.processTicks = processTicks;
		this.luxInputCondition = luxInputCondition;
	}

	public InputPattern<IngredientStack> pattern() {
		return this.pattern;
	}
	public List<AugmentOp> augments() {
		return this.augments;
	}
	public HolderSet<Augment> precursor() {
		return this.precursor;
	}
	public HolderSet<Augment> incompatible() {
		return this.incompatible;
	}
	public int processTicks() {
		return this.processTicks;
	}
	public LuxInputCondition luxInputCondition() {
		return this.luxInputCondition;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input) {
		ItemStack stack = input.getCenterStack();
		if (stack.isEmpty()) return LuxRecipeEvaluation.fail();

		Set<Holder<Augment>> spec = AugmentLogic.getSpec(stack);
		for (AugmentOp op : this.augments) {
			if (!op.optional && !spec.contains(op.augment)) return LuxRecipeEvaluation.fail();
		}

		ItemAugment itemAugment = AugmentLogic.getAugments(stack);
		for (AugmentOp op : this.augments) {
			if (op.optional) continue;
			if (op.remove != itemAugment.has(op.augment)) return LuxRecipeEvaluation.fail();
		}

		for (Holder<Augment> h : this.precursor) {
			if (!itemAugment.has(h)) return LuxRecipeEvaluation.fail();
		}

		for (Holder<Augment> h : this.incompatible) {
			if (itemAugment.has(h)) return LuxRecipeEvaluation.fail();
		}

		ConsumptionRecord consumptions = CraftingLogic.test(this.pattern, input.asAugmentInput());
		if (consumptions == null) return LuxRecipeEvaluation.fail();

		return new LuxRecipeEvaluation(
				() -> {
					ItemStack copy = stack.copy();
					copy.set(ModDataComponents.AUGMENTS, itemAugment.with(set -> {
						for (AugmentOp op : this.augments) {
							if (op.remove) {
								set.remove(op.augment);
							} else {
								// non-optional augments have already been checked
								if (op.optional) {
									if (!spec.contains(op.augment)) continue;
								}
								set.add(op.augment);
							}
						}
					}));
					return copy;
				},
				this.processTicks,
				consumptions,
				this.luxInputCondition
		);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.AUGMENT_SERIALIZER.get();
	}

	public static @Nullable Supplier<String> validateAugmentList(List<AugmentOp> list) {
		if (list.isEmpty()) return () -> "Augment list of recipe cannot be empty";
		Set<Holder<Augment>> set = new ObjectOpenHashSet<>();
		for (AugmentOp op : list) {
			if (!set.add(op.augment)) {
				return () -> "Duplicated addition or removal against of augment " + op.augment;
			}
		}
		return null;
	}

	public record AugmentOp(
			@NotNull Holder<Augment> augment,
			boolean remove,
			boolean optional
	) {
		public static final Codec<AugmentOp> CODEC = Codec.<AugmentOp, AugmentOp>either(
				ModDataMaps.AUGMENT_CODEC.xmap(
						h -> new AugmentOp(h, false, false),
						c -> c.augment).validate(c -> {
					if (c.remove || c.optional) {
						return DataResult.error(() -> "Uh you're not supposed to see this");
					}
					return DataResult.success(c);
				}),
				RecordCodecBuilder.create(b -> b.group(
						ModDataMaps.AUGMENT_CODEC.fieldOf("augment").forGetter(AugmentOp::augment),
						Codec.BOOL.optionalFieldOf("remove", false).forGetter(AugmentOp::remove),
						Codec.BOOL.optionalFieldOf("optional", false).forGetter(AugmentOp::optional)
				).apply(b, AugmentOp::new))
		).xmap(
				Either::unwrap,
				op -> {
					if (op.remove || op.optional) return Either.right(op);
					else return Either.left(op);
				}
		);

		public static final StreamCodec<RegistryFriendlyByteBuf, AugmentOp> STREAM_CODEC = StreamCodec.of(
				(buffer, c) -> {
					ModDataMaps.AUGMENT_STREAM_CODEC.encode(buffer, c.augment);
					buffer.writeBoolean(c.remove);
					buffer.writeBoolean(c.optional);
				}, buffer -> {
					return new AugmentOp(
							ModDataMaps.AUGMENT_STREAM_CODEC.decode(buffer),
							buffer.readBoolean(),
							buffer.readBoolean());
				});
	}

	public static final class Serializer implements RecipeSerializer<AugmentRecipe> {
		private static final MapCodec<AugmentRecipe> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
				ArtisanryRecipe.GRID_UNOPTIMIZED_SPEC.codec().fieldOf("pattern").forGetter(r -> r.pattern),
				AugmentOp.CODEC.listOf().validate(list -> {
					Supplier<String> errorMessage = validateAugmentList(list);
					return errorMessage != null ? DataResult.error(errorMessage) : DataResult.success(list);
				}).fieldOf("augments").forGetter(r -> r.augments),
				ModDataMaps.AUGMENT_SET_CODEC.optionalFieldOf("precursor", HolderSet.empty()).forGetter(r -> r.precursor),
				ModDataMaps.AUGMENT_SET_CODEC.optionalFieldOf("incompatible", HolderSet.empty()).forGetter(r -> r.incompatible),
				Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("processTicks", 0).forGetter(r -> r.processTicks),
				LuxInputCondition.CODEC.codec().optionalFieldOf("luxInput", LuxInputCondition.none()).forGetter(r -> r.luxInputCondition)
		).apply(b, AugmentRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, AugmentRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ArtisanryRecipe.GRID_UNOPTIMIZED_SPEC.streamCodec().encode(buffer, recipe.pattern);
					ModUtils.writeCollection(buffer, recipe.augments, AugmentOp.STREAM_CODEC);
					ModDataMaps.AUGMENT_SET_STREAM_CODEC.encode(buffer, recipe.precursor);
					ModDataMaps.AUGMENT_SET_STREAM_CODEC.encode(buffer, recipe.incompatible);
					buffer.writeVarInt(recipe.processTicks);
					LuxInputCondition.STREAM_CODEC.encode(buffer, recipe.luxInputCondition);
				}, buffer -> {
					InputPattern<IngredientStack> pattern = ArtisanryRecipe.GRID_UNOPTIMIZED_SPEC.streamCodec().decode(buffer);
					List<AugmentOp> augments = ModUtils.readList(buffer, AugmentOp.STREAM_CODEC);
					HolderSet<Augment> precursor = ModDataMaps.AUGMENT_SET_STREAM_CODEC.decode(buffer);
					HolderSet<Augment> incompatible = ModDataMaps.AUGMENT_SET_STREAM_CODEC.decode(buffer);
					int processTicks = buffer.readVarInt();
					LuxInputCondition luxInputCondition = LuxInputCondition.STREAM_CODEC.decode(buffer);
					return new AugmentRecipe(pattern, augments, precursor, incompatible, processTicks, luxInputCondition);
				}
		);

		@Override
		public @NotNull MapCodec<AugmentRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, AugmentRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
