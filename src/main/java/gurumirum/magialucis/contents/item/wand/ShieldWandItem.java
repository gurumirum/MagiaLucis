package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
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
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
        if (lux < COST_PER_SHIELD) livingEntity.stopUsingItem();
        else stack.set(Contents.LUX_CHARGE, lux-COST_PER_SHIELD);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockEvent(LivingShieldBlockEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldWandItem && player.isUsingItem()) {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
                if (lux >= COST_PER_SHIELD) {
                    e.setBlocked(true);
                    stack.set(Contents.LUX_CHARGE, lux-COST_PER_BLOCKING);
                }
            }
        }

    }
}
