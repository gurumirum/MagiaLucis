package gurumirum.magialucis;

import com.mojang.logging.LogUtils;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.impl.InWorldBeamCraftingManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(MagiaLucisMod.MODID)
public class MagiaLucisMod {
	public static final String MODID = "magialucis";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MagiaLucisMod(IEventBus modBus) {
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
