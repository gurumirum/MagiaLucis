package gurumirum.gemthing;

import com.mojang.logging.LogUtils;
import gurumirum.gemthing.contents.Contents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(GemthingMod.MODID)
public class GemthingMod {
	public static final String MODID = "gemthing";
	public static final Logger LOGGER = LogUtils.getLogger();

	public GemthingMod(IEventBus modBus) {
		Contents.init(modBus);
	}

	@NotNull
	public static ResourceLocation id(@NotNull String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
}
