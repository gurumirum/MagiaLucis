package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.item.LuxBatteryItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;

public class PearlWandItem extends LuxBatteryItem {
    private static final int HEAL_AMOUNT = 10;
    public static final int COST_PER_CAST = 20;

    public PearlWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
        if (charge < COST_PER_CAST) return InteractionResultHolder.fail(stack);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
            if (charge < COST_PER_CAST) return stack;

            level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
                            player.getBoundingBox().inflate(5f), e -> !(e instanceof Enemy))
                    .forEach(e -> e.heal(HEAL_AMOUNT));
            player.heal(HEAL_AMOUNT);
            return super.finishUsingItem(stack, level, livingEntity);
        }
        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 80;
    }
}
