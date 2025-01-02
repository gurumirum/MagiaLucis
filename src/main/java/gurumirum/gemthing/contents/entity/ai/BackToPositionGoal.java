package gurumirum.gemthing.contents.entity.ai;

import gurumirum.gemthing.contents.entity.GemGolemEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BackToPositionGoal extends Goal {

    GemGolemEntity golem;

    private boolean forceTrigger;
    private final boolean checkNoActionTime;
    private int interval;

    public BackToPositionGoal(GemGolemEntity golem) {
        this.golem = golem;
        interval = 120;
        checkNoActionTime = true;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.golem.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.golem.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = golem.getTargetBlockPos();
            if (vec3 == null) {
                return false;
            } else {
                this.forceTrigger = false;
                return true;
            }
    }

    @Override
    public void start() {
        this.golem.getNavigation().moveTo(golem.getTargetBlockPos().x, golem.getTargetBlockPos().y, golem.getTargetBlockPos().z, 1);
    }

    @Override
    public void stop() {
        this.golem.getNavigation().stop();
        super.stop();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.golem.getNavigation().isDone() && !this.golem.hasControllingPassenger();
    }
}
