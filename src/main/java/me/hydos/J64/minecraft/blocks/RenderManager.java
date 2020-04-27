package me.hydos.J64.minecraft.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RenderManager {

//    public static RenderLayer getVertexDefaultLayer(Identifier texture) {
//        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, false)).transparency(RenderLayer.NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
//        return RenderLayer.of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
//    }

}
