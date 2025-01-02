package gurumirum.gemthing.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

import static gurumirum.gemthing.GemthingMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyMappings {
	private ModKeyMappings() {}

	public static final KeyMapping CHANGE_WAND = new KeyMapping("key." + MODID + ".change_wand",
			KeyConflictContext.IN_GAME,
			KeyModifier.ALT,
			InputConstants.Type.KEYSYM,
			InputConstants.KEY_C,
			"key.categories.gameplay");

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(ModKeyMappings.CHANGE_WAND);
	}
}
