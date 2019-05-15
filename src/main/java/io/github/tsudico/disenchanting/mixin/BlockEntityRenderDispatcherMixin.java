package io.github.tsudico.disenchanting.mixin;

import com.google.common.collect.Maps;
import io.github.tsudico.disenchanting.client.DisenchantTableEntityRenderer;
import io.github.tsudico.disenchanting.common.block.DisenchantTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Shadow
    private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.newHashMap();

    @Inject(at = @At("RETURN"), method = "<init>")
    private void addRenderer(CallbackInfo info) {
        DisenchantTableEntityRenderer renderer = new DisenchantTableEntityRenderer();
        this.renderers.put(DisenchantTableBlockEntity.class, renderer);

        renderer.setRenderManager( (BlockEntityRenderDispatcher)(Object)this );
    }
}