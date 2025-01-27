package gurumirum.magialucis.client.render;

import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class RenderEffectManager<T extends RenderEffect> implements Iterable<T> {
	private final List<T> effects = new ArrayList<>();
	private final List<T> effectsView = Collections.unmodifiableList(this.effects);

	public @NotNull @Unmodifiable List<T> effects() {
		return this.effectsView;
	}

	public void register(@NotNull T effect) {
		this.effects.add(Objects.requireNonNull(effect));
	}

	void clear() {
		this.effects.clear();
	}

	void onLevelUnload(LevelAccessor level) {
		for (T effect : this.effects) {
			effect.onLevelUnload(level);
		}
	}

	@Override public @NotNull Iterator<T> iterator() {
		return new Iter();
	}

	private final class Iter implements Iterator<T> {
		private int nextIndex = 0;
		private @Nullable T nextElement;

		@Override public boolean hasNext() {
			if (this.nextElement == null) resolveNext();
			return this.nextElement != null;
		}

		private void resolveNext() {
			List<T> effects = RenderEffectManager.this.effects;
			while (this.nextElement == null && this.nextIndex < effects.size()) {
				this.nextElement = effects.get(this.nextIndex);

				if (!this.nextElement.isEffectValid()) {
					this.nextElement = null;
					effects.remove(this.nextIndex);
					continue;
				}

				this.nextIndex++;
				return;
			}
		}

		@Override public T next() {
			if (this.nextElement == null) resolveNext();

			if (this.nextElement == null) throw new NoSuchElementException();
			T ret = this.nextElement;
			this.nextElement = null;
			return ret;
		}

		@Override public void remove() {
			RenderEffectManager.this.effects.remove(--this.nextIndex);
		}
	}
}
