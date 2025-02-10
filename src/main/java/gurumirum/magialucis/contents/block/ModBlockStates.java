package gurumirum.magialucis.contents.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class ModBlockStates {
	private ModBlockStates() {}

	public static final BooleanProperty LANTERN = BooleanProperty.create("lantern");
	public static final BooleanProperty OVERSATURATED = BooleanProperty.create("oversaturated");
	public static final BooleanProperty SKYLIGHT_INTERFERENCE = BooleanProperty.create("skylight_interference");
	public static final BooleanProperty WORKING = BooleanProperty.create("working");
	public static final BooleanProperty LEFT = BooleanProperty.create("left");
	public static final BooleanProperty LIGHTLOOM = BooleanProperty.create("lightloom");

	public static final IntegerProperty SKY_VISIBILITY = IntegerProperty.create("sky_visibility", 0, 15);
}
