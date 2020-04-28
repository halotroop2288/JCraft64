package me.hydos.J64;

import me.hydos.J64.minecraft.blocks.N64Renderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class ClientMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Main.n64BlockEntity, N64Renderer::new);
    }
}
