package gurumirum.magialucis.contents.data;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.contents.Contents;
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

public final class ItemAugment {
	public static final Codec<ItemAugment> CODEC = RegistryFixedCodec.create(Contents.AUGMENT_REGISTRY_KEY).listOf()
			.xmap(ItemAugment::of, e -> List.copyOf(e.set()));

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemAugment> STREAM_CODEC =
			ByteBufCodecs.collection(i -> (Collection<Holder<Augment>>)(new ArrayList<Holder<Augment>>()),
							ByteBufCodecs.holderRegistry(Contents.AUGMENT_REGISTRY_KEY))
					.map(ItemAugment::of, ItemAugment::set);

	private static final ItemAugment empty = new ItemAugment();

	public static ItemAugment empty() {
		return empty;
	}

	public static ItemAugment of(@NotNull Collection<@NotNull Holder<Augment>> augments) {
		return new ItemAugment(augments);
	}

	private final Set<Holder<Augment>> set;

	private ItemAugment() {
		this.set = Set.of();
	}

	private ItemAugment(Collection<@NotNull Holder<Augment>> augments) {
		this.set = ObjectSets.unmodifiable(new ObjectLinkedOpenHashSet<>(augments));
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
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ItemAugment)obj;
		return Objects.equals(this.set, that.set);
	}

	@Override
	public int hashCode() {
		return this.set.hashCode();
	}

	@Override
	public String toString() {
		return "ItemAugment [" + this.set + "]";
	}
}
