package gurumirum.gemthing;

import com.mojang.logging.LogUtils;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.InWorldBeamCraftingManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(GemthingMod.MODID)
public class GemthingMod {
	public static final String MODID = "gemthing";
	public static final Logger LOGGER = LogUtils.getLogger();

	public GemthingMod(IEventBus modBus) {
		Contents.init(modBus);

		modBus.addListener((FMLCommonSetupEvent event) -> {
			event.enqueueWork(InWorldBeamCraftingManager::init);
		});
	}

	@NotNull
	public static ResourceLocation id(@NotNull String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
}
