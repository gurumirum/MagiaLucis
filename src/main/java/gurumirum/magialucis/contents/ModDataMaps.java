package gurumirum.magialucis.contents;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.contents.data.GemStat;
import gurumirum.magialucis.contents.data.SetRemover;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.stream.Collectors;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataMaps {
	private ModDataMaps() {}

	public static final Codec<Holder<Augment>> AUGMENT_CODEC = RegistryFixedCodec.create(Contents.AUGMENT_REGISTRY_KEY);
	public static final Codec<HolderSet<Augment>> AUGMENT_SET_CODEC = HolderSetCodec
			.create(Contents.AUGMENT_REGISTRY_KEY, Codec.lazyInitialized(() -> Contents.augmentRegistry().holderByNameCodec()), false);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Augment>> AUGMENT_STREAM_CODEC = ByteBufCodecs.holderRegistry(Contents.AUGMENT_REGISTRY_KEY);
	public static final StreamCodec<RegistryFriendlyByteBuf, HolderSet<Augment>> AUGMENT_SET_STREAM_CODEC = ByteBufCodecs.holderSet(Contents.AUGMENT_REGISTRY_KEY);

	public static final AdvancedDataMapType<Item, @Unmodifiable Set<Holder<Augment>>, SetRemover<Holder<Augment>>> AUGMENT_SPEC;
	public static final DataMapType<Item, GemStat> GEM_STAT;

	static {
		@SuppressWarnings("unchecked")
		Codec<@Unmodifiable Set<Holder<Augment>>> augmentSetCodec = AUGMENT_SET_CODEC
				.xmap(holders -> holders.stream().collect(Collectors.toUnmodifiableSet()),
						set -> HolderSet.direct(set.toArray(Holder[]::new)));

		AUGMENT_SPEC = AdvancedDataMapType.builder(id("augment_spec"), Registries.ITEM, augmentSetCodec)
				.synced(augmentSetCodec, true)
				.merger(DataMapValueMerger.setMerger())
				.remover(augmentSetCodec.xmap(SetRemover::new, SetRemover::elements))
				.build();

		GEM_STAT = DataMapType.builder(
						id("gem_stat"), Registries.ITEM, GemStat.CODEC)
				.synced(GemStat.CODEC, true)
				.build();
	}

	@SubscribeEvent
	public static void register(RegisterDataMapTypesEvent event) {
		event.register(AUGMENT_SPEC);
		event.register(GEM_STAT);
	}
}
