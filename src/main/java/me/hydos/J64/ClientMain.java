package me.hydos.J64;

import me.hydos.J64.minecraft.N64Renderer;
import me.hydos.J64.minecraft.registry.BlockRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class ClientMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(BlockRegistry.N64_BE_TYPE, N64Renderer::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), BlockRegistry.n64_blocks);
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.TV, RenderLayer.getCutoutMipped());
    }
}
