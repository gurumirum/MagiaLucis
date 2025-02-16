package gurumirum.magialucis.contents.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class InputPatternSpec<B extends FriendlyByteBuf, T> {
	private final int maxHeight;
	private final int maxWidth;
	private final T emptyInput;
	private final boolean optimize;

	private final MapCodec<InputPattern<T>> codec;
	private final StreamCodec<B, InputPattern<T>> streamCodec;

	public InputPatternSpec(int maxHeight, int maxWidth, @NotNull T emptyInput, boolean optimize,
	                        Supplier<Codec<T>> inputCodec, Supplier<StreamCodec<B, T>> inputStreamCodec) {
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.emptyInput = emptyInput;
		this.optimize = optimize;

		Codec<T> lazyInputCodec = Codec.lazyInitialized(inputCodec);
		Lazy<StreamCodec<B, T>> lazyInputStream = Lazy.of(inputStreamCodec);

		Codec<List<String>> patternCodec = Codec.STRING.listOf().comapFlatMap(list -> {
			if (list.size() > this.maxHeight) {
				return DataResult.error(() -> "Invalid pattern: too many rows, %s is maximum".formatted(maxHeight));
			} else if (list.isEmpty()) {
				return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
			} else {
				int i = list.getFirst().length();

				for (String s : list) {
					if (s.length() > maxWidth) {
						return DataResult.error(() -> "Invalid pattern: too many columns, %s is maximum".formatted(maxWidth));
					}

					if (i != s.length()) {
						return DataResult.error(() -> "Invalid pattern: each row must be the same width");
					}
				}

				return DataResult.success(list);
			}
		}, Function.identity());

		Codec<Character> symbolCodec = Codec.STRING.comapFlatMap(s -> {
			if (s.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '" + s + "' is an invalid symbol (must be 1 character only).");
			} else {
				return " ".equals(s) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(s.charAt(0));
			}
		}, String::valueOf);

		MapCodec<InputPattern.Data<T>> dataCodec = RecordCodecBuilder.mapCodec(b -> b.group(
				ExtraCodecs.strictUnboundedMap(symbolCodec, lazyInputCodec).fieldOf("key").forGetter(InputPattern.Data::key),
				patternCodec.fieldOf("pattern").forGetter(InputPattern.Data::pattern)
		).apply(b, InputPattern.Data::new));

		this.codec = dataCodec.flatXmap(
				this::unpack,
				p -> p.data() != null ? DataResult.success(p.data()) :
						DataResult.error(() -> "Cannot encode unpacked recipe"));

		this.streamCodec = StreamCodec.of((buffer, pattern) -> {
			buffer.writeVarInt(pattern.width());
			buffer.writeVarInt(pattern.height());
			for (T t : pattern.inputs()) lazyInputStream.get().encode(buffer, t);
		}, buffer -> {
			int width = buffer.readVarInt();
			int height = buffer.readVarInt();
			List<T> inputs = new ArrayList<>(width * height);
			for (int i = 0; i < width * height; i++) {
				inputs.add(lazyInputStream.get().decode(buffer));
			}
			return new InputPattern<>(width, height, List.copyOf(inputs), null);
		});
	}

	public int maxHeight() {
		return this.maxHeight;
	}
	public int maxWidth() {
		return this.maxWidth;
	}
	public T emptyInput() {
		return this.emptyInput;
	}
	public boolean optimize() {
		return this.optimize;
	}

	public MapCodec<InputPattern<T>> codec() {
		return this.codec;
	}
	public StreamCodec<B, InputPattern<T>> streamCodec() {
		return streamCodec;
	}

	public InputPattern<T> unpack(Map<Character, T> key, List<String> pattern) {
		return unpack(new InputPattern.Data<>(key, pattern)).getOrThrow();
	}

	public DataResult<InputPattern<T>> unpack(@NotNull InputPattern.Data<T> data) {
		String[] pattern = this.optimize ? shrink(data.pattern()) : data.pattern().toArray(new String[0]);
		int width = pattern[0].length();
		int height = pattern.length;
		List<T> inputs = new ArrayList<>(width * height);
		CharSet chars = new CharArraySet(data.key().keySet());

		for (int y = 0; y < pattern.length; y++) {
			String s = pattern[y];

			for (int x = 0; x < s.length(); x++) {
				char c = s.charAt(x);
				T t = c == ' ' ? this.emptyInput : data.key().get(c);
				if (t == null) {
					return DataResult.error(() -> "Pattern references symbol '" + c + "' but it's not defined in the key");
				}

				chars.remove(c);
				inputs.add(x + width * y, t);
			}
		}

		return !chars.isEmpty()
				? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + chars)
				: DataResult.success(new InputPattern<>(width, height, List.copyOf(inputs), data));
	}

	private static String[] shrink(List<String> pattern) {
		int xStart = Integer.MAX_VALUE;
		int xEnd = 0;
		int yStart = 0;
		int yEndOffset = 0;

		for (int i = 0; i < pattern.size(); i++) {
			String s = pattern.get(i);
			xStart = Math.min(xStart, firstNonSpace(s));
			int end = lastNonSpace(s);
			xEnd = Math.max(xEnd, end);
			if (end < 0) {
				if (yStart == i) yStart++;
				yEndOffset++;
			} else yEndOffset = 0;
		}

		if (pattern.size() == yEndOffset) return new String[0];

		String[] newPattern = new String[pattern.size() - yEndOffset - yStart];
		for (int i = 0; i < newPattern.length; i++) {
			newPattern[i] = pattern.get(i + yStart).substring(xStart, xEnd + 1);
		}
		return newPattern;
	}

	private static int firstNonSpace(String row) {
		int i = 0;
		while (i < row.length() && row.charAt(i) == ' ') i++;
		return i;
	}

	private static int lastNonSpace(String row) {
		int i = row.length() - 1;
		while (i >= 0 && row.charAt(i) == ' ') i--;
		return i;
	}
}
