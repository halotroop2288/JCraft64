package me.hydos.J64.minecraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render", at=@At("TAIL"))
    public void e(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci){
        VertexConsumerProvider provider = this.bufferBuilders.getEntityVertexConsumers();
        provider.getBuffer(RenderLayer.getEntitySolid(TextureManager.MISSING_IDENTIFIER));

//        consumer
//                .vertex(1f,1f,1f)
//                .vertex(1f,1f,1f)
//                .vertex(1f,1f,1f)
//                .color(1f,0.5f,0.5f, 1f)
//                .texture(0,0)
//                .overlay(1)
//                .normal(1,1,1)
//                .light(1)
//                .next();
    }

}
