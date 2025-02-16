package gurumirum.magialucis.contents.block.lux.splitter;

import gurumirum.magialucis.api.MagiaLucisApi;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Objects;

@EventBusSubscriber(modid = MagiaLucisApi.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SplitterBlockModels {
	private SplitterBlockModels() {}

	private static final Int2ObjectMap<ModelResourceLocation> sideModels = new Int2ObjectOpenHashMap<>();

	static {
		for (Direction side : Direction.values()) {
			for (byte apertureLevel = 0; apertureLevel < SplitterBlockEntity.APERTURE_LEVELS; apertureLevel++) {
				sideModels.put(pack(side, apertureLevel), new ModelResourceLocation(
						MagiaLucisApi.id("block/splitter/aperture_" + side + "_" + apertureLevel),
						ModelResourceLocation.STANDALONE_VARIANT));
			}
		}
	}

	public static ModelResourceLocation sideModel(Direction side, int apertureLevel) {
		return Objects.requireNonNull(sideModels.get(pack(side, apertureLevel)), "sideModel == null");
	}

	@SubscribeEvent
	public static void registerAdditionalModel(ModelEvent.RegisterAdditional event) {
		sideModels.values().forEach(event::register);
	}

	private static int pack(Direction side, int apertureLevel) {
		return side.get3DDataValue() * SplitterBlockEntity.APERTURE_LEVELS + apertureLevel;
	}
}
