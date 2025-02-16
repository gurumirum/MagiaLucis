package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.field.FieldBuilder;
import gurumirum.magialucis.contents.block.AmberLanternBlock;

import static gurumirum.magialucis.api.MagiaLucisApi.id;
import static gurumirum.magialucis.api.field.FieldRegistry.register;

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

	public static final Field AMBER_LANTERN = register(new FieldBuilder()
			.forceRange(AmberLanternBlock.RANGE)
			.forceDiminishPower(0)
			.build(id("amber_lantern")));

	public static void init() {}
}
