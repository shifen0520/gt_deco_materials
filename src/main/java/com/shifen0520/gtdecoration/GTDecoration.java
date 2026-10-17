package com.shifen0520.gtdecoration;

import com.gto.registrylib.RegistryCore;
import com.shifen0520.gtdecoration.client.GTDecorationClient;
import com.shifen0520.gtdecoration.material.MaterialRegistry;
import com.shifen0520.gtdecoration.material.Materials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GT装饰与材料 —— 从 SHJTCore 独立出来的材料注册系统。
 *
 * <p>只注册材料（锭/粉/板/杆/齿轮/流体/矿石/宝石/电路…），不注册任何机器。
 * 贴图联动 Athena 4.0.6 作为连接纹理，运行时按材料颜色动态生成并注册。
 */
@Mod(GTDecoration.MODID)
public class GTDecoration {

    public static final String MODID = "gt_deco_materials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final RegistryCore REGISTRY = RegistryCore.create(MODID);

    public GTDecoration(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // 1) 创造模式标签页（materials / fluids / blocks）
        BuiltinSHJTCreativeTabs.init();
        // 2) 材料系统：数据类型 → 形态 → 118 元素 + 化合物/合金 + 宝石/矿石/电路系列
        Materials.init();
        // 3) 把材料落成真实物品/方块/流体
        Items.register();
        Fluids.register();
        // 3.5) GTO/GTM 装饰方块（外壳/线圈/陶瓷机械方块…）+ 量子与材料类物品
        Casings.register();
        SpecialItems.register();
        // 4) 全部注册完成后再冻结材料注册表
        MaterialRegistry.freeze();

        // 5) 客户端：染色 + Athena 动态贴图（仅客户端执行）
        //    NeoForge 1.21.1 已移除 DistExecutor，改用运行时边检测 FMLEnvironment.dist。
        //    GTDecorationClient 仅在 init() 内部引用客户端类，本类加载在服务端安全。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            GTDecorationClient.init(modEventBus);
        }

        LOGGER.info("GTDecoration loaded: {} materials registered.",
                MaterialRegistry.registered().size());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
    }
}
