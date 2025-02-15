package gurumirum.magialucis.contents.data;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public record SetRemover<T>(
		@NotNull @Unmodifiable Set<T> elements
) implements DataMapValueRemover<Item, @Unmodifiable Set<T>> {
	@Override
	public @NotNull Optional<@Unmodifiable Set<T>> remove(
			@NotNull Set<T> value, @NotNull Registry<Item> registry,
			@NotNull Either<TagKey<Item>, ResourceKey<Item>> source, @NotNull Item object) {
		Set<T> set2 = new ObjectOpenHashSet<>(value);
		set2.removeAll(this.elements);
		return set2.isEmpty() ? Optional.empty() : Optional.of(Set.copyOf(set2));
	}
}
