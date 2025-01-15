package gurumirum.magialucis.impl.field;

import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.impl.field.FieldRegistry.register;

public final class Fields {
	private Fields() {}

	public static final Field AMBER_CORE = register(new FieldBuilder()
			.interferenceThreshold(3)
			.build(id("amber_core")));

	public static final Field SUNLIGHT_CORE = register(new FieldBuilder()
			.interferenceThreshold(2)
			.build(id("sunlight_core")));

	public static final Field MOONLIGHT_CORE = register(new FieldBuilder()
			.interferenceThreshold(2)
			.build(id("moonlight_core")));

	public static void init() {}
}
