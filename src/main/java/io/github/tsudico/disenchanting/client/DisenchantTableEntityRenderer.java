package io.github.tsudico.disenchanting.client;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.tsudico.disenchanting.common.block.DisenchantTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DisenchantTableEntityRenderer extends BlockEntityRenderer<DisenchantTableBlockEntity> {
    private static final Identifier BOOK_TEX = new Identifier("textures/entity/enchanting_table_book.png");
    private final BookModel book = new BookModel();

    public void render(DisenchantTableBlockEntity disenchantTableBlockEntity, double x, double y, double z, float delta, int int_1) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
        float deltaTicks = (float)disenchantTableBlockEntity.ticks + delta;
        GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(deltaTicks * 0.1F) * 0.01F, 0.0F);

        float orientation;
        for(orientation = disenchantTableBlockEntity.orientation - disenchantTableBlockEntity.lastOrientation; orientation >= 3.1415927F; orientation -= 6.2831855F) {
        }

        while(orientation < -3.1415927F) {
            orientation += 6.2831855F;
        }

        float deltaOrientation = disenchantTableBlockEntity.lastOrientation + orientation * delta;
        GlStateManager.rotatef(-deltaOrientation * 57.295776F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
        bindTexture(BOOK_TEX);
        float leftPage = MathHelper.lerp(delta, disenchantTableBlockEntity.pageAngle, disenchantTableBlockEntity.nextPageAngle) + 0.25F;
        float rightPage = MathHelper.lerp(delta, disenchantTableBlockEntity.pageAngle, disenchantTableBlockEntity.nextPageAngle) + 0.75F;
        leftPage = (leftPage - (float)MathHelper.fastFloor((double)leftPage)) * 1.6F - 0.3F;
        rightPage = (rightPage - (float)MathHelper.fastFloor((double)rightPage)) * 1.6F - 0.3F;
        if (leftPage < 0.0F) {
            leftPage = 0.0F;
        }

        if (rightPage < 0.0F) {
            rightPage = 0.0F;
        }

        if (leftPage > 1.0F) {
            leftPage = 1.0F;
        }

        if (rightPage > 1.0F) {
            rightPage = 1.0F;
        }

        float midPage = MathHelper.lerp(delta, disenchantTableBlockEntity.pageTurningSpeed, disenchantTableBlockEntity.nextPageTurningSpeed);
        GlStateManager.enableCull();
        book.render(deltaTicks, leftPage, rightPage, midPage, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
