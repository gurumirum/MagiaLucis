package gurumirum.magialucis.contents.recipe;

import net.minecraft.Util;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

/**
 * Generified version of {@link ShapedRecipePattern}
 *
 * @param <T> Input instance
 */
public final class InputPattern<T> {
	private final int width;
	private final int height;
	private final List<T> inputs;
	private final @Nullable Data<T> data;
	private final boolean symmetrical;

	public InputPattern(int width, int height, List<T> inputs, @Nullable Data<T> data) {
		this.width = width;
		this.height = height;
		this.inputs = inputs;
		this.data = data;
		this.symmetrical = Util.isSymmetrical(width, height, inputs);
	}

	public int width() {
		return this.width;
	}
	public int height() {
		return this.height;
	}
	public @NotNull @Unmodifiable List<T> inputs() {
		return this.inputs;
	}
	public @Nullable Data<T> data() {
		return this.data;
	}
	public boolean symmetrical() {
		return this.symmetrical;
	}

	public T get(int x, int y, boolean mirrored) {
		return this.inputs.get(getIndex(x, y, mirrored));
	}

	public int getIndex(int x, int y, boolean mirrored) {
		return (mirrored ? this.width - x - 1 : x) + y * this.width;
	}

	public record Data<T>(Map<Character, T> key, List<String> pattern) {
		public Data {
			key = Map.copyOf(key);
			pattern = List.copyOf(pattern);
		}
	}
}
