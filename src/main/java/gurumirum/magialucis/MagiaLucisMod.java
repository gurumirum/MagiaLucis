package gurumirum.magialucis;

import com.mojang.logging.LogUtils;
import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.impl.field.Fields;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MagiaLucisApi.MODID)
public class MagiaLucisMod {
	public static final Logger LOGGER = LogUtils.getLogger();

	public MagiaLucisMod(IEventBus modBus) {
		Contents.init(modBus);
		Fields.init();
	}
}
