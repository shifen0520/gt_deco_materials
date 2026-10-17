package com.shifen0520.gtdecoration;

import com.gto.registrylib.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Map;

/**
 * 三个内置创造模式标签页，分别收三类产物：
 * <ul>
 *   <li>{@code materials} —— 材料物品：锭/粉/板/杆/齿轮等（材料默认标签页）</li>
 *   <li>{@code fluids} —— 流体桶</li>
 *   <li>{@code blocks} —— 材料方块（压缩方块等）</li>
 * </ul>
 */
public final class BuiltinSHJTCreativeTabs {

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> MATERIALS;
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> FLUIDS;
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> BLOCKS;
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> RESEARCH;

    public static final ResourceKey<CreativeModeTab> MATERIALS_KEY = key("materials");
    public static final ResourceKey<CreativeModeTab> FLUIDS_KEY = key("fluids");
    /** 方块类物品统一注册到这里。 */
    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = key("blocks");
    /** 科研系统物品（身份卡等）。 */
    public static final ResourceKey<CreativeModeTab> RESEARCH_KEY = key("research");

    private BuiltinSHJTCreativeTabs() {
    }

    private static ResourceKey<CreativeModeTab> key(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, path));
    }

    public static void init() {
    }

    static {
        MATERIALS = GTDecoration.REGISTRY.creativeTab("materials", "SHJTech Materials", Map.of());
        FLUIDS = GTDecoration.REGISTRY.creativeTab("fluids", "SHJTech Fluids", Map.of());
        BLOCKS = GTDecoration.REGISTRY.creativeTab("blocks", "SHJTech Blocks", Map.of());
        RESEARCH = GTDecoration.REGISTRY.creativeTab("research", "SHJTech Research", Map.of());

        // 未显式指定标签页的物品（各类材料物品）默认落到 materials
        GTDecoration.REGISTRY.defaultCreativeTab(MATERIALS_KEY);
    }
}
