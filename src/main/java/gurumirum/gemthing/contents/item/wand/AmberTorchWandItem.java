package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.item.LuxBatteryItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmberTorchWandItem extends LuxBatteryItem {
	public static final int COST_PER_LIGHT_SOURCE = 2;

	private static final int HIT_DISTANCE = 10;
	private static final int PARTICLE_COUNT = 8;

	public AmberTorchWandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if(charge < COST_PER_LIGHT_SOURCE) return InteractionResultHolder.success(player.getItemInHand(hand));
		HitResult res = player.pick(HIT_DISTANCE, 0, false);
		if(res instanceof  BlockHitResult hitResult) {
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);

			if(state.is(ModBlocks.AMBER_LIGHT.block()) || state.is(Blocks.AIR)) return InteractionResultHolder.fail(stack);
			if (!state.canBeReplaced()) {
				pos = pos.relative(hitResult.getDirection());
				state = level.getBlockState(pos);

				if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResultHolder.fail(stack);
				if (!state.canBeReplaced()) return InteractionResultHolder.pass(stack);
			}

			BlockState placeState = ModBlocks.AMBER_LIGHT.block().getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
			if (placeState == null)	return InteractionResultHolder.fail(stack);

			Vec3 playerPos = player.getEyePosition().lerp(player.getPosition(0), 0.5);
			int count = (int) playerPos.distanceTo(res.getLocation());
			for (int i=0; i<count; i++) {
				Vec3 particlePos = playerPos.lerp(res.getLocation(), i/(float)count).offsetRandom(player.getRandom(), 0.5f);
				level.addParticle(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0);
			}
			magicalFlameCircle(level, pos);

			level.setBlockAndUpdate(pos, placeState);

			stack.set(Contents.LUX_CHARGE, charge - COST_PER_LIGHT_SOURCE);
			applyCooldown(player);

			return InteractionResultHolder.success(stack);
		}

		return InteractionResultHolder.fail(stack);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (charge < COST_PER_LIGHT_SOURCE) return InteractionResult.FAIL;

		BlockPos pos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(pos);

		if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
		if (!state.canBeReplaced()) {
			pos = pos.relative(context.getClickedFace());
			state = context.getLevel().getBlockState(pos);

			if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
			if (!state.canBeReplaced()) return InteractionResult.PASS;
		}
		BlockState placeState = ModBlocks.AMBER_LIGHT.block().getStateForPlacement(new BlockPlaceContext(context));
		if (placeState == null) return InteractionResult.FAIL;

		magicalFlameCircle(context.getLevel(), pos);
		context.getLevel().setBlockAndUpdate(pos, placeState);

		stack.set(Contents.LUX_CHARGE, charge - COST_PER_LIGHT_SOURCE);
		applyCooldown(context.getPlayer());

		return InteractionResult.SUCCESS;
	}

	private void applyCooldown(@Nullable Player player) {
		if (player == null) return;
		player.getCooldowns().addCooldown(this, 10);
	}

	private void magicalFlameCircle(Level level, BlockPos pos) {
		double x = (double)pos.getX() + 0.5;
		double y = (double)pos.getY() + 0.5;
		double z = (double)pos.getZ() + 0.5;
		for (int i=0; i<PARTICLE_COUNT; i++) {
			level.addParticle(ParticleTypes.FLAME, x, y, z, 0.1 * (level.getRandom().nextDouble() + 0.5) * Math.sin(2 * Math.PI / PARTICLE_COUNT * i),
					level.getRandom().nextDouble() * 0.05,
					0.1 * (level.getRandom().nextDouble() + 0.5) * Math.cos(2 * Math.PI / PARTICLE_COUNT * i));
		}
	}

}
