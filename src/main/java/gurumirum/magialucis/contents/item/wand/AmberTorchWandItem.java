package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmberTorchWandItem extends LuxContainerItem {
	public static final int COST_PER_LIGHT_SOURCE = 5;

	private static final int HIT_DISTANCE = 15;
	private static final int PARTICLE_COUNT = 8;

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
			Vec3 playerPos = player.getEyePosition().lerp(player.getPosition(0), 0.5);
			int count = (int)playerPos.distanceTo(hitResult.getLocation());

			for (int i = 0; i < count; i++) {
				Vec3 particlePos = playerPos.lerp(hitResult.getLocation(), i / (float)count)
						.offsetRandom(player.getRandom(), 0.5f);
				level.addParticle(ParticleTypes.FLAME,
						particlePos.x, particlePos.y, particlePos.z,
						0.0, 0.0, 0.0);
			}

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

		if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
		if (!state.canBeReplaced()) {
			pos = pos.relative(blockHitResult.getDirection());
			state = level.getBlockState(pos);

			if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
			if (!state.canBeReplaced()) return InteractionResult.FAIL;
		}

		BlockState placeState = ModBlocks.AMBER_LIGHT.block()
				.getStateForPlacement(new BlockPlaceContext(player, hand, stack, blockHitResult));
		if (placeState == null) return InteractionResult.FAIL;

		magicalFlameCircle(level, pos);
		level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5F, 1.0F);
		level.setBlockAndUpdate(pos, placeState);
		return InteractionResult.SUCCESS;
	}

	private void applyCooldown(@Nullable Player player) {
		if (player == null) return;
		player.getCooldowns().addCooldown(this, 10);
	}

	private static void magicalFlameCircle(Level level, BlockPos pos) {
		double x = (double)pos.getX() + 0.5;
		double y = (double)pos.getY() + 0.5;
		double z = (double)pos.getZ() + 0.5;
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			level.addParticle(ParticleTypes.FLAME, x, y, z, 0.1 * (level.getRandom().nextDouble() + 0.5) * Math.sin(2 * Math.PI / PARTICLE_COUNT * i),
					level.getRandom().nextDouble() * 0.05,
					0.1 * (level.getRandom().nextDouble() + 0.5) * Math.cos(2 * Math.PI / PARTICLE_COUNT * i));
		}
	}
}
