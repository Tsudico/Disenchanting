package io.github.tsudico.disenchanting.common.block;

import io.github.tsudico.disenchanting.Disenchanting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

import java.util.Random;


public class DisenchantTableBlockEntity extends BlockEntity implements Tickable {
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float pagesTurned;
    public float pagePosition;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float orientation;
    public float lastOrientation;
    public float playerDirection;
    private static final Random RANDOM = new Random();

    public DisenchantTableBlockEntity() {
        super(Disenchanting.DISENCHANT_TABLE_ENTITY);
    }

    public void tick() {
        pageTurningSpeed = nextPageTurningSpeed;
        lastOrientation = orientation;
        PlayerEntity closestPlayer = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3.0D, false);
        if (closestPlayer != null) {
            double playerOffsetX = closestPlayer.x - (pos.getX() + 0.5D);
            double playerOffsetZ = closestPlayer.z - (pos.getZ() + 0.5D);
            playerDirection = (float) MathHelper.atan2(playerOffsetZ, playerOffsetX);
            nextPageTurningSpeed += 0.1F;
            if (nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float pages = pagesTurned;

                do {
                    pagesTurned += (float) (RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (pages == pagesTurned);
            }
        } else {
            playerDirection += 0.02F;
            nextPageTurningSpeed -= 0.1F;
        }

        while (orientation >= 3.1415927F) {
            orientation -= 6.2831855F;
        }

        while (orientation < -3.1415927F) {
            orientation += 6.2831855F;
        }

        while (playerDirection >= 3.1415927F) {
            playerDirection -= 6.2831855F;
        }

        while (playerDirection < -3.1415927F) {
            playerDirection += 6.2831855F;
        }

        float angle;
        for (angle = playerDirection - orientation; angle >= 3.1415927F; angle -= 6.2831855F) {
        }

        while (angle < -3.1415927F) {
            angle += 6.2831855F;
        }

        orientation += angle * 0.4F;
        nextPageTurningSpeed = MathHelper.clamp(nextPageTurningSpeed, 0.0F, 1.0F);
        ++ticks;
        pageAngle = nextPageAngle;
        float pageRotation = (pagesTurned - nextPageAngle) * 0.4F;
        pageRotation = MathHelper.clamp(pageRotation, -0.2F, 0.2F);
        pagePosition += (pageRotation - pagePosition) * 0.9F;
        nextPageAngle += pagePosition;
    }
}
