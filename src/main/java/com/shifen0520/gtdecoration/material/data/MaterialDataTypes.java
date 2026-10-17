package com.shifen0520.gtdecoration.material.data;

import com.shifen0520.gtdecoration.material.MaterialRegistry;
import net.minecraft.resources.ResourceLocation;

public class MaterialDataTypes {

    public static final RgbColorDataType PRIMARY_COLOR = MaterialDataRegistry.register(
            "primary_color", new RgbColorDataType(ResourceLocation.fromNamespaceAndPath("gt_deco_materials", "primary_color"))
    );

    public static final RgbColorDataType SECONDARY_COLOR = MaterialDataRegistry.register(
            "secondary_color", new RgbColorDataType(ResourceLocation.fromNamespaceAndPath("gt_deco_materials", "secondary_color"))
    );

    public static final MassDataType MASS = MaterialDataRegistry.register(
            "mass", new MassDataType(ResourceLocation.fromNamespaceAndPath("gt_deco_materials", "mass"))
    );

    /** 熔点（开尔文）。流体温度优先取此值；常温液态元素（汞、溴）不设，走形态默认室温。 */
    public static final IntDataType MELTING_POINT = MaterialDataRegistry.register(
            "melting_point", new IntDataType(ResourceLocation.fromNamespaceAndPath("gt_deco_materials", "melting_point"))
    );

    /** 密度（kg/m³）。流体密度优先取此值，缺省回落到形态默认密度。 */
    public static final IntDataType DENSITY = MaterialDataRegistry.register(
            "density", new IntDataType(ResourceLocation.fromNamespaceAndPath("gt_deco_materials", "density"))
    );

    public static void init() {

    }
}
