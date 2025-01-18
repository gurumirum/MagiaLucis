package gurumirum.magialucis.contents.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public record IngredientStack(Ingredient ingredient, int count) {
	public static final Codec<IngredientStack> CODEC = RecordCodecBuilder.create(b -> b.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientStack::ingredient),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(IngredientStack::count)
	).apply(b, IngredientStack::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
		Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value.ingredient());
		buffer.writeVarInt(value.count());
	}, buffer -> new IngredientStack(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readVarInt()));

	public List<@Nullable ItemStack> toItemList() {
		return switch (this.count) {
			case 0 -> List.of();
			case 1 -> List.of(this.ingredient.getItems());
			default -> Arrays.stream(this.ingredient.getItems())
					.map(s -> s.copyWithCount(this.count)).toList();
		};
	}
}
