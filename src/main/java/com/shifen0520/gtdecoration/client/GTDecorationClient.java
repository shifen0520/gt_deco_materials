package com.shifen0520.gtdecoration.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * 客户端初始化：把材料运行时染色挂到 mod 事件总线。
 * （连接纹理由 Athena 内置 {@code athena:ctm} 提供，本模组仅针对 GTM/GTO 机械外壳方块做覆盖。）
 */
public final class GTDecorationClient {

    private GTDecorationClient() {
    }

    public static void init(IEventBus modEventBus) {
        // 运行时染色：tintIndex 0 → 主色，1 → 副色（物品/方块通用）
        modEventBus.addListener(RegisterColorHandlersEvent.Item.class, ClientTintSetup::onItemColors);
        modEventBus.addListener(RegisterColorHandlersEvent.Block.class, ClientTintSetup::onBlockColors);
        // 玻璃方块走半透明渲染层（1.21.1 不能靠 RenderShape.TRANSLUCENT）
        modEventBus.addListener(FMLClientSetupEvent.class, GlassRenderTypes::register);
    }
}
