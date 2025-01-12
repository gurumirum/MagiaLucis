package gurumirum.magialucis.contents.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class ModBlockStateProps {
	private ModBlockStateProps() {}

	public static final BooleanProperty OVERSATURATED = BooleanProperty.create("oversaturated");
	public static final BooleanProperty SKYLIGHT_INTERFERENCE = BooleanProperty.create("skylight_interference");
	public static final IntegerProperty SKY_VISIBILITY = IntegerProperty.create("sky_visibility", 0, 15);
}
