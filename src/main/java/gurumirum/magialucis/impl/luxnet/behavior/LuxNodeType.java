package gurumirum.magialucis.impl.luxnet.behavior;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public sealed abstract class LuxNodeType<B extends LuxNodeBehavior> {
	private final ResourceLocation id;
	private final Class<B> type;

	public LuxNodeType(@NotNull ResourceLocation id, @NotNull Class<B> type) {
		this.id = Objects.requireNonNull(id);
		this.type = Objects.requireNonNull(type);
	}

	public @NotNull ResourceLocation id() {
		return this.id;
	}

	public @NotNull Class<B> type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public @Nullable CompoundTag writeCast(@NotNull LuxNodeBehavior behavior, @NotNull HolderLookup.Provider lookupProvider) {
		try {
			return write((B)behavior, lookupProvider);
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException("Lux node behavior of class " + behavior.getClass() +
					" cannot be saved by lux node type " + this.id);
		}
	}

	public abstract @Nullable CompoundTag write(@NotNull B behavior, @NotNull HolderLookup.Provider lookupProvider);

	public abstract @NotNull B read(@Nullable CompoundTag tag, @NotNull HolderLookup.Provider lookupProvider);

	@Override
	public boolean equals(Object o) {
		return o instanceof LuxNodeType<?> other && Objects.equals(this.id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}

	@Override
	public String toString() {
		return "LuxNodeType [" + id + ']';
	}

	public static final class Serializable<B extends LuxNodeBehavior> extends LuxNodeType<B> {
		private final TriConsumer<B, CompoundTag, HolderLookup.Provider> writeFunc;
		private final BiFunction<CompoundTag, HolderLookup.Provider, B> readFunc;

		public Serializable(@NotNull ResourceLocation id, @NotNull Class<B> type,
		                    @NotNull TriConsumer<B, CompoundTag, HolderLookup.Provider> writeFunc,
		                    @NotNull BiFunction<CompoundTag, HolderLookup.Provider, B> readFunc) {
			super(id, type);
			this.writeFunc = Objects.requireNonNull(writeFunc);
			this.readFunc = Objects.requireNonNull(readFunc);
		}

		@Override
		public @NotNull CompoundTag write(@NotNull B behavior, HolderLookup.@NotNull Provider lookupProvider) {
			CompoundTag tag = new CompoundTag();
			this.writeFunc.accept(behavior, tag, lookupProvider);
			return tag;
		}

		@Override
		public @NotNull B read(@Nullable CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
			if (tag == null) tag = new CompoundTag();
			return this.readFunc.apply(tag, lookupProvider);
		}
	}

	public static final class Simple<B extends LuxNodeBehavior> extends LuxNodeType<B> {
		private final Supplier<B> factory;

		public Simple(@NotNull ResourceLocation id, @NotNull Class<B> type, @NotNull Supplier<B> factory) {
			super(id, type);
			this.factory = factory;
		}

		@Override
		public @Nullable CompoundTag write(@NotNull B behavior, HolderLookup.@NotNull Provider lookupProvider) {
			return null;
		}

		@Override
		public @NotNull B read(@Nullable CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
			return this.factory.get();
		}
	}
}
