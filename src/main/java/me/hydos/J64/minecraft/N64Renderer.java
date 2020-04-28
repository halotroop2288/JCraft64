package me.hydos.J64.minecraft;

import me.hydos.J64.minecraft.block.entity.N64BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class N64Renderer extends BlockEntityRenderer<N64BlockEntity> {

    public N64Renderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(N64BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), 0,0,0,1,1,1,1,1,1,1);
    }
}
