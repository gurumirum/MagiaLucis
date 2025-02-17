package gurumirum.magialucis.contents.data;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.api.MagiaLucisRegistries;
import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.utils.AugmentProvider;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Unmodifiable
public final class ItemAugment implements Collection<Holder<Augment>> {
	public static final Codec<ItemAugment> CODEC = RegistryFixedCodec.create(MagiaLucisRegistries.AUGMENT).listOf()
			.xmap(ItemAugment::of, e -> List.copyOf(e.set()));

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemAugment> STREAM_CODEC =
			ByteBufCodecs.collection(i -> (Collection<Holder<Augment>>)(new ArrayList<Holder<Augment>>()),
							ByteBufCodecs.holderRegistry(MagiaLucisRegistries.AUGMENT))
					.map(ItemAugment::of, ItemAugment::list);

	private static final ItemAugment empty = new ItemAugment();

	public static ItemAugment empty() {
		return empty;
	}

	public static ItemAugment of(@NotNull Collection<@NotNull Holder<Augment>> augments) {
		return augments.isEmpty() ? empty() : new ItemAugment(augments);
	}

	private final List<Holder<Augment>> list;
	private final Set<Holder<Augment>> set;

	private ItemAugment() {
		this.list = List.of();
		this.set = Set.of();
	}

	private ItemAugment(Collection<@NotNull Holder<Augment>> augments) {
		this.set = ObjectSets.unmodifiable(new ObjectLinkedOpenHashSet<>(augments));
		this.list = List.copyOf(this.set);
	}

	public @NotNull @Unmodifiable List<Holder<Augment>> list() {
		return this.list;
	}

	public @NotNull @Unmodifiable Set<Holder<Augment>> set() {
		return this.set;
	}

	public boolean has(@NotNull AugmentProvider augment) {
		return has(augment.augment());
	}

	public boolean has(@NotNull Holder<Augment> augment) {
		return this.set.contains(augment);
	}

	public ItemAugment with(Consumer<Set<Holder<Augment>>> change) {
		@NotNull ObjectLinkedOpenHashSet<Holder<Augment>> set = new ObjectLinkedOpenHashSet<>(this.set);
		change.accept(set);
		return new ItemAugment(set);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	/**
	 * @deprecated Use {@link #has(Holder)} / {@link #has(AugmentProvider)} for type safety
	 */
	@Override
	@Deprecated
	public boolean contains(Object o) {
		return this.set.contains(o);
	}

	@Override
	public @NotNull Iterator<Holder<Augment>> iterator() {
		return this.list.iterator();
	}

	@Override
	public @NotNull Object @NotNull [] toArray() {
		return this.list.toArray();
	}
	@Override
	public <T> @NotNull T @NotNull [] toArray(@NotNull T @NotNull [] ts) {
		return this.list.toArray(ts);
	}

	@Override
	public boolean add(Holder<Augment> augmentHolder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> collection) {
		return this.set.containsAll(collection);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends Holder<Augment>> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ItemAugment)obj;
		return Objects.equals(this.list, that.list);
	}

	@Override
	public int hashCode() {
		return this.list.hashCode();
	}

	@Override
	public String toString() {
		return "ItemAugment [" + this.list.stream()
				.map(Holder::getRegisteredName)
				.collect(Collectors.joining(", ")) + "]";
	}
}
