package gurumirum.magialucis.contents.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public record IngredientStack(Ingredient ingredient, int count) {
	public static final IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, 0);

	public static final Codec<IngredientStack> CODEC = Codec.<Ingredient, IngredientStack>either(
			Ingredient.CODEC_NONEMPTY,
			RecordCodecBuilder.create(b -> b.group(
					Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientStack::ingredient),
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(IngredientStack::count)
			).apply(b, IngredientStack::new))
	).xmap(e -> e.map(i -> new IngredientStack(i, 1), Function.identity()),
			is -> is.count == 1 ? Either.left(is.ingredient) : Either.right(is));

	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
		Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value.ingredient());
		buffer.writeVarInt(value.count());
	}, buffer -> new IngredientStack(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readVarInt()));

	public List<ItemStack> toItemList() {
		return switch (this.count) {
			case 0, 1 -> List.of(this.ingredient.getItems());
			default -> Arrays.stream(this.ingredient.getItems())
					.map(s -> s.copyWithCount(this.count)).toList();
		};
	}
}
