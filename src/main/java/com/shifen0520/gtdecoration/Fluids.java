package com.shifen0520.gtdecoration;

import com.shifen0520.gtdecoration.material.Material;
import com.shifen0520.gtdecoration.material.MaterialForm;
import com.shifen0520.gtdecoration.material.MaterialForms;
import com.shifen0520.gtdecoration.material.MaterialRegistry;
import com.shifen0520.gtdecoration.material.data.MaterialDataTypes;
import com.shifen0520.gtdecoration.material.form.FluidForm;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 把带流体形态的材料落成真实流体（含桶，桶进 fluids 标签页）。
 *
 * <p>形态分工对应 GTM：
 * <ul>
 *   <li>{@link MaterialForms#ELEMENT_GAS} / {@link MaterialForms#GAS} —— 气态（元素气体 / 化合物气体）</li>
 *   <li>{@link MaterialForms#ELEMENT_LIQUID} / {@link MaterialForms#LIQUID} —— 熔融（固体对应）</li>
 *   <li>{@link MaterialForms#ELEMENT_FLUID} —— 常温液态元素（汞、溴）</li>
 *   <li>{@link MaterialForms#FLUID} —— 一般化合物流体</li>
 * </ul>
 */
public final class Fluids {

    private Fluids() {
    }

    public static void register() {
        // 收集全部流体形态并按材料名排序，让 fluids 标签页里的桶按字母序排列
        record FluidEntry(Material material, FluidForm form) {
        }
        List<FluidEntry> entries = new ArrayList<>();
        for (Material material : MaterialRegistry.registered()) {
            for (MaterialForm form : material.forms()) {
                if (form instanceof FluidForm fluidForm) {
                    entries.add(new FluidEntry(material, fluidForm));
                }
            }
        }
        entries.sort(Comparator.comparing(e -> e.material.id().getPath()));

        for (FluidEntry entry : entries) {
            Material material = entry.material;
            FluidForm fluidForm = entry.form;
            MaterialForm form = fluidForm;
            String materialPath = material.id().getPath();
            String name = form.registryName(materialPath); // 例如 hydrogen_fluid
                // 流体贴图取自形态共享模板：熔融=灰度岩浆 / 其余=灰度水 / 元素=纯色
                ResourceLocation still =
                        ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, fluidForm.stillTexture());
                ResourceLocation flow =
                        ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, fluidForm.flowTexture());

                // 温度优先取材料真实熔点（铁 1811K / 金 1337K / 钨 3695K）；
                // 常温液态元素（汞、溴）没有熔点数据 —— 常温即为液体，回落形态默认室温 300K。
                int temperature = material.data(MaterialDataTypes.MELTING_POINT)
                        .orElse(fluidForm.temperature());
                int density = material.data(MaterialDataTypes.DENSITY)
                        .orElse(fluidForm.density());

                // 桶的流体覆盖层是灰度贴图，颜色完全靠 tint：优先取材料主色
                int tint = material.data(MaterialDataTypes.PRIMARY_COLOR)
                        .<Integer>map(c -> 0xFF000000 | c.rgb())
                        .orElse(fluidForm.tintColor());

                boolean molten = form == MaterialForms.LIQUID || form == MaterialForms.ELEMENT_LIQUID;

                // 桶物品标签：c:buckets/<材料> + c:buckets（桶是普通物品、走 ITEM 标签）
                // 必须在 .bucket(...) 之前设 .defaultBucketTab(...)：bucket() 内部会读取该字段先 addTab。
                var builder = GTDecoration.REGISTRY
                        .fluid(name, still, flow, BaseFlowingFluid.Flowing::new)
                        .properties(p -> p.density(density).viscosity(fluidForm.viscosity()).temperature(temperature))
                        .lang(molten ? "Molten " + MaterialNames.display(materialPath)
                                : MaterialNames.display(materialPath))
                        .clientExtension(still, flow, tint)
                        .defaultBucketTab(BuiltinSHJTCreativeTabs.FLUIDS_KEY)
                        .bucket(b -> {
                            b.addTag(TagKey.create(Registries.ITEM,
                                    ResourceLocation.fromNamespaceAndPath("c", "buckets/" + materialPath)));
                            b.addTag(TagKey.create(Registries.ITEM,
                                    ResourceLocation.fromNamespaceAndPath("c", "buckets")));
                        });
                // 流体标签：NeoForge 约定流体用「单数材料名」c:<材料>（见 Tags.Fluids 注释：
                // fluid tags should not be plural），例：c:hydrogen_chloride / c:stardust_shards。
                //
                // 关键坑：registrylib 的 FluidTagBatch.addSuppliers() 是【急切解析】的——调用当时就
                // 执行 supplier.get() 取 ResourceLocation。而在 Fluids.register() 执行的「mod 构造阶段」，
                // 流体 DeferredHolder 尚未绑定，于是在【数据生成 runData】下 .tag()/addSuppliers 会抛
                // "Registry entry ... has not been bound yet"（本例正是 flowing_1_octene_fluid）。
                // 因此改用 TagBatch.addIds()：按 ResourceLocation【延迟】添加，等 datagen 的 TagProvider
                // 阶段（流体已注册）再解析，彻底避免构建期触碰未绑定 holder。
                // 这里把 **source 流体**(shjtcore:<材料>_fluid) 与 **flowing 变体**
                // (shjtcore:flowing_<材料>_fluid) 一并挂进 c:<材料>，让 JEI 找到有效原料、消除警告。
                var fluidEntry = builder.register();
                var fluidTag = TagKey.create(Registries.FLUID,
                        ResourceLocation.fromNamespaceAndPath("c", materialPath));
                GTDecoration.REGISTRY.fluidTags().addIds(fluidTag,
                        ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, materialPath + "_fluid"),
                        ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, "flowing_" + materialPath + "_fluid"));
        }
    }
}
