package gurumirum.magialucis;

import com.mojang.logging.LogUtils;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.ModEntities;
import gurumirum.magialucis.contents.entity.GemGolemEntity;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(MagiaLucisMod.MODID)
public class MagiaLucisMod {
	public static final String MODID = "magialucis";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MagiaLucisMod(IEventBus modBus) {
		Contents.init(modBus);
		Fields.init();

		modBus.addListener((FMLCommonSetupEvent event) -> {
			event.enqueueWork(AncientLightCrafting::init);
		});

		modBus.addListener((EntityAttributeCreationEvent event) -> {
			event.put(ModEntities.GEM_GOLEM.get(), GemGolemEntity.createAttributes().build());
		});
	}

	@NotNull
	public static ResourceLocation id(@NotNull String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}
}
