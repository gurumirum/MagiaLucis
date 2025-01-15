package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShieldWandItem extends LuxBatteryItem {
    public static final int COST_PER_SHIELD = 1;
    public static final int COST_PER_BLOCKING = 5;

    public ShieldWandItem(Properties properties) {super(properties);}

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.getOrDefault(Contents.LUX_CHARGE, 0L) < COST_PER_SHIELD) return InteractionResultHolder.fail(stack);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) return;
        long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
        if (lux < COST_PER_SHIELD) livingEntity.stopUsingItem();
        else stack.set(Contents.LUX_CHARGE, lux-COST_PER_SHIELD);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }
}
