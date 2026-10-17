package com.shifen0520.gtdecoration.client;

import com.shifen0520.gtdecoration.Casings;
import com.shifen0520.gtdecoration.GTDecoration;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * 玻璃方块的渲染层注册（仅客户端）。
 *
 * <p>1.21.1 的 {@code RenderShape} 不再支持 TRANSLUCENT，透明必须通过
 * {@link ItemBlockRenderTypes#setRenderLayer} 显式指定渲染层。
 * 在 {@link FMLClientSetupEvent} 里对 {@link Casings#glassNames()} 列出的方块
 * 注册 {@link RenderType#TRANSLUCENT}，玻璃就能透视后方方块。
 */
public final class GlassRenderTypes {

    private GlassRenderTypes() {
    }

    public static void register(FMLClientSetupEvent event) {
        int count = 0;
        for (String name : Casings.glassNames()) {
            Block block = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, name));
            if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
                continue;
            }
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.TRANSLUCENT);
            count++;
        }
        GTDecoration.LOGGER.info("Registered translucent render layer for {} glass blocks.", count);
    }
}