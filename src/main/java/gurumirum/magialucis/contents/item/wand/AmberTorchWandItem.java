package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.ModBlockStates;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AmberTorchWandItem extends LuxContainerItem {
	public static final int COST_PER_LIGHT_SOURCE = 5;

	private static final int HIT_DISTANCE = 15;
	private static final int PARTICLE_COUNT = 8;
	private static final int PARTICLE_COUNT_WATERLOGGED = 8;

	public AmberTorchWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < COST_PER_LIGHT_SOURCE) return InteractionResultHolder.fail(player.getItemInHand(hand));

		HitResult res = player.pick(HIT_DISTANCE, 0, false);
		if (!(res instanceof BlockHitResult hitResult) || hitResult.getType() != HitResult.Type.BLOCK)
			return InteractionResultHolder.fail(stack);

		InteractionResult result = placeLight(level, player, hand, stack, hitResult);
		if (result.indicateItemUse()) {
			particleLine(level, player, hitResult.getLocation());
			stack.set(ModDataComponents.LUX_CHARGE, charge - COST_PER_LIGHT_SOURCE);
			applyCooldown(player);
		}

		return new InteractionResultHolder<>(result, stack);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < COST_PER_LIGHT_SOURCE) return InteractionResult.FAIL;

		InteractionResult result = placeLight(context.getLevel(), context.getPlayer(), context.getHand(),
				context.getItemInHand(), context.getHitResult());
		if (result.indicateItemUse()) {
			stack.set(ModDataComponents.LUX_CHARGE, charge - COST_PER_LIGHT_SOURCE);
			applyCooldown(context.getPlayer());
		}

		return result;
	}

	private static InteractionResult placeLight(Level level, Player player, InteractionHand hand, ItemStack stack,
	                                            BlockHitResult blockHitResult) {
		if (blockHitResult == null || blockHitResult.getType() != HitResult.Type.BLOCK) return InteractionResult.FAIL;

		BlockPos pos = blockHitResult.getBlockPos();
		BlockState state = level.getBlockState(pos);

		if (state.is(ModBlocks.AMBER_LIGHT.block()) && !state.getValue(ModBlockStates.LANTERN)) {
			return InteractionResult.FAIL;
		}
		if (!state.canBeReplaced()) {
			pos = pos.relative(blockHitResult.getDirection());
			state = level.getBlockState(pos);

			if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
			if (!state.canBeReplaced()) return InteractionResult.FAIL;
		}

		BlockState placeState = ModBlocks.AMBER_LIGHT.block()
				.getStateForPlacement(new BlockPlaceContext(player, hand, stack, blockHitResult));
		if (placeState == null) return InteractionResult.FAIL;

		particleCircle(level, pos, placeState.getValue(BlockStateProperties.WATERLOGGED));
		level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5F, 1.0F);
		level.setBlockAndUpdate(pos, placeState);
		return InteractionResult.SUCCESS;
	}

	private void applyCooldown(@Nullable Player player) {
		if (player == null) return;
		player.getCooldowns().addCooldown(this, 10);
	}

	private static void particleCircle(Level level, BlockPos pos, boolean waterlogged) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;

		int count = waterlogged ? PARTICLE_COUNT_WATERLOGGED : PARTICLE_COUNT;
		double hSpd = waterlogged ? 0.5 : 0.1;
		double vSpd = waterlogged ? 0.5 : 0.05;

		for (int i = 0; i < count; i++) {
			level.addParticle(waterlogged ? ParticleTypes.BUBBLE : ParticleTypes.FLAME,
					x, y, z,
					hSpd * (level.getRandom().nextDouble() + 0.5) * Math.sin(2 * Math.PI / count * i),
					vSpd * level.getRandom().nextDouble(),
					hSpd * (level.getRandom().nextDouble() + 0.5) * Math.cos(2 * Math.PI / count * i));
		}
	}

	private static void particleLine(Level level, Player player, Vec3 hitLocation) {
		Vector3f pos = player.position().toVector3f();
		pos.y += player.getBbHeight() / 2;
		Vector3f end = hitLocation.toVector3f();

		int count = (int)pos.distance(end);
		if (count <= 0) return;

		// prevent position clipping through block and end up producing wrong type of particles at the end
		Vector3f incr = end.sub(pos).mul(0.98f).div(count);
		MutableBlockPos mpos = new MutableBlockPos();

		for (int i = 0; i < count; i++) {
			pos.add(incr);
			mpos.set(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));

			FluidState fluidState = level.getFluidState(mpos);
			boolean waterlogged = fluidState.is(Tags.Fluids.WATER);
			RandomSource random = level.getRandom();

			for (int j = 0; j < (waterlogged ? 2 : 1); j++) {
				float xo = (random.nextFloat() - .5f) * .5f;
				float yo = (random.nextFloat() - .5f) * .5f;
				float zo = (random.nextFloat() - .5f) * .5f;

				level.addParticle(waterlogged ? ParticleTypes.BUBBLE : ParticleTypes.FLAME,
						pos.x + xo, pos.y + yo, pos.z + zo,
						0.0, waterlogged ? (j + 1) * 0.001 : 0.0, 0.0);
			}
		}
	}
}
