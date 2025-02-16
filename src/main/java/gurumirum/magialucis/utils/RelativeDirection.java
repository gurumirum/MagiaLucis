package gurumirum.magialucis.utils;

import net.minecraft.core.Direction;

public enum RelativeDirection {
	FRONT,
	BACK,
	U,
	D,
	L,
	R;

	public static RelativeDirection getRelativeDirection(Direction base, Direction side) {
		Direction southSide = switch (base) {
			case DOWN -> side.getClockWise(Direction.Axis.X);
			case UP -> side.getCounterClockWise(Direction.Axis.X);
			case NORTH -> side.getAxis() == Direction.Axis.Y ? side : side.getOpposite();
			case SOUTH -> side;
			case WEST -> side.getCounterClockWise(Direction.Axis.Y);
			case EAST -> side.getClockWise(Direction.Axis.Y);
		};

		return switch (southSide) {
			case DOWN -> D;
			case UP -> U;
			case NORTH -> BACK;
			case SOUTH -> FRONT;
			case WEST -> R;
			case EAST -> L;
		};
	}

	public static Direction getSide(Direction base, RelativeDirection dir) {
		Direction southSide = switch (dir) {
			case FRONT -> Direction.SOUTH;
			case BACK -> Direction.NORTH;
			case U -> Direction.UP;
			case D -> Direction.DOWN;
			case L -> Direction.EAST;
			case R -> Direction.WEST;
		};

		return switch (base) {
			case DOWN -> southSide.getCounterClockWise(Direction.Axis.X);
			case UP -> southSide.getClockWise(Direction.Axis.X);
			case NORTH -> southSide.getAxis() == Direction.Axis.Y ? southSide : southSide.getOpposite();
			case SOUTH -> southSide;
			case WEST -> southSide.getClockWise(Direction.Axis.Y);
			case EAST -> southSide.getCounterClockWise(Direction.Axis.Y);
		};
	}
}