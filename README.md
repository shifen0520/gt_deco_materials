GT Deco & Materials · KubeJS 材料注册案例
本文是 GTDM（GT Deco & Materials）材料系统的完整用法案例。所有代码都可以直接放进
kubejs/startup_scripts/ 使用。
1. 它是怎么工作的
- 材料 = 一组颜色数据 + 一组「形态」。
- 每个「形态」会生成一个注册名：<材料名>_<形态后缀>，例如 my_alloy_ingot。
- 贴图是灰度模板，颜色在运行期按「主色 / 副色」实时染上去（tintindex 0 = 主色，1 = 副色，2+ = 不染色）。
- 语言名、方块状态、模型、矿石掉落表、物品/方块标签全部自动生成，脚本不用管。
2. 最小案例
gt_deco_materials.materialRegistration(event => {
    event.create('my_alloy')
        .color(0x33ddff)                  // 主色 0xRRGGBB
        .forms('ingot', 'dust', 'plate')  // 要哪些形态
        .build()                           // 完成注册
})
进游戏后就会多出 My Alloy Ingot / My Alloy Dust / My Alloy Plate。
3. API 一览
方法
说明
.color(int rgb)
主色，0xRRGGBB。会自动派生副色（主色压暗 30%）
.secondaryColor(int rgb)
显式指定副色，覆盖派生值
.materialSet(String set)
贴图套件（见第 7 节），不写则按形态自动推断
.zhcnname(String name)
简体中文材料名，如 .zhcnname('镍') → 显示「镍锭」
.zhtwname(String name)
繁体中文材料名（zh_tw / zh_hk 共用）
.enname(String name)
英文材料名（一般不用，默认从注册名推导）
.name(String lang, String name)
任意语言的材料名，如 .name('ja_jp', 'ニッケル')
.forms(String... names)
追加形态，可多次调用
.build()
完成注册，返回 Material
不设置本地化名时，材料名由注册名机械推导成英文，中文会显示成「Nickel 锭」这种中英混排。
设置了 .zhcnname('镍') 后，就变成纯中文的「镍锭」「镍板」「镍粉」。
注册窗口只在 STARTUP 阶段开放。build() 之后材料不可删除/修改。
4. 案例 A：金属材料（双色 + 金属套件）
gt_deco_materials.materialRegistration(event => {
    event.create('hardened_steel')
        .color(0x8a9aa8)             // 主色：冷灰蓝
        .secondaryColor(0x5c6b78)    // 副色：更深的灰蓝（做阴影层）
        .materialSet('metallic')     // 金属画风
        .forms(
            'ingot', 'ingot_hot', 'ingot_double',
            'nugget', 'dust', 'dust_small', 'dust_tiny',
            'plate', 'plate_dense', 'plate_double',
            'gear', 'gear_small', 'rod', 'rod_long',
            'bolt', 'screw', 'ring', 'foil',
            'spring', 'spring_small', 'wire_fine',
            'block', 'frame', 'rotor', 'turbine_blade'
        )
        .build()
})
要点：
- secondaryColor 不写也行，会自动取「主色压暗 30%」。
- ingot / ingot_hot / ingot_double 三者共用同一套锭贴图，只差第 4 层 overlay（和 GTM 一致）。
- block（存储块）、frame（框架）、rotor / turbine_blade 会让材料显得"成套"。
5. 案例 B：宝石材料
gt_deco_materials.materialRegistration(event => {
    event.create('void_crystal')
        .color(0x9b6cff)
        .materialSet('gem_vertical')   // 宝石画风（竖向切面）
        .forms(
            'gem', 'gem_flawed', 'gem_chipped',
            'gem_flawless', 'gem_exquisite', 'gem_lens',
            'dust', 'dust_small', 'dust_tiny',
            'plate', 'rod', 'block'
        )
        .build()
})
- 宝石六档品质：gem → gem_flawed → gem_chipped → gem_flawless → gem_exquisite，外加 gem_lens（透镜）。
- 不写 .materialSet() 时，只要材料带宝石形态就会自动用 gem_vertical。
6. 案例 C：矿石材料（母岩 + 掉落链）
gt_deco_materials.materialRegistration(event => {
    event.create('deep_iron')
        .color(0xb87333)
        .materialSet('dull')
        .forms(
            // 物品：粗矿 → 粉碎 → 精炼 → 粉
            'raw_ore', 'crushed_ore', 'crushed_refined', 'purified_ore',
            'dust_impure', 'dust_pure', 'dust',
            // 方块：矿石（可挂多种母岩）
            'ore', 'ore_deepslate', 'ore_netherrack', 'ore_end_stone',
            'ore_granite', 'ore_diorite', 'ore_andesite', 'ore_tuff',
            'ore_blackstone', 'ore_basalt', 'ore_calcite',
            'ore_marble', 'ore_red_granite', 'ore_black_granite',
            'raw_ore_block'
        )
        .build()
})
矿石掉落规则（自动生成，无需配置）：
情况
掉落
精准采集
矿石方块本身
普通挖掘
raw_ore（没有则按 gem → crushed_ore → dust 回落）
时运
掉落数量增加（原版 ore_drops 公式）
爆炸
会衰减（explosion_decay）
矿石方块同时自动带上 mineable/pickaxe、needs_stone_tool、c:ores、c:ores/<材料> 标签。
7. 案例 D：流体材料（一料一形态）
重要：6 个流体形态的后缀都是 fluid，同一个材料挂两个会注册名冲突。所以一个材料只能挂一个流体形态。
gt_deco_materials.materialRegistration(event => {
    // 熔融金属
    event.create('molten_titanium')
        .color(0xd8d8e8)
        .forms('element_liquid')
        .build()

    // 气体
    event.create('chlorine_gas')
        .color(0x9be36a)
        .forms('element_gas')
        .build()

    // 常温液体
    event.create('brine')
        .color(0x4a8fbf)
        .forms('fluid')
        .build()
})
桶物品（<材料>_fluid_bucket）会自动生成，名字与颜色也自动处理。
8. 案例 E：全套件矩阵（测试 / 对比用）
const SETS = ['dull', 'metallic', 'shiny', 'bright', 'magnetic', 'radioactive',
              'fine', 'rough', 'sand', 'glass', 'wood', 'diamond', 'emerald',
              'netherstar', 'quartz', 'opal', 'lapis', 'lignite', 'flint',
              'certus', 'ruby', 'gem_horizontal', 'gem_vertical', 'paper', 'powder']

SETS.forEach(set => {
    event.create('demo_' + set)
        .color(0x9aa7b0)
        .materialSet(set)
        .forms('ingot', 'plate', 'gear', 'dust', 'gem', 'block', 'ore')
        .build()
})
同一主色、同一形态，只有套件不同，方便一眼看出画风差异。
9. 可用形态清单（60 个）
物品形态（37）
分类
形态
锭
ingot ingot_hot ingot_double raw_ore
粉
dust dust_small dust_tiny dust_impure dust_pure
板
plate plate_dense plate_double
齿轮
gear gear_small
杆
rod rod_long
紧固
bolt screw ring foil wire_fine
弹簧
spring spring_small
粒
nugget
其他
round turbine_blade rotor nanites
矿石加工
crushed_ore crushed_refined purified_ore
宝石
gem gem_flawed gem_chipped gem_flawless gem_exquisite gem_lens
方块形态（17）
分类
形态
存储 / 机械
block frame
粗矿块
raw_ore_block
矿石
ore ore_deepslate ore_netherrack ore_end_stone ore_granite ore_diorite ore_andesite ore_tuff ore_blackstone ore_basalt ore_calcite ore_marble ore_red_granite ore_black_granite
流体形态（6，一料一个）
fluid gas liquid element_gas element_liquid element_fluid
10. 材质套件（25 个）
dull  metallic  shiny  bright  magnetic  radioactive  fine  rough
sand  glass  wood  diamond  emerald  netherstar  quartz  opal
lapis  lignite  flint  certus  ruby  gem_horizontal  gem_vertical
paper  powder
- 套件贴图放在 textures/{item,block}/material/<套件>/，某层缺失时自动回落 dull。
- paper / powder 是方块专用套件：物品层没有贴图，会自动回落 dull。
- 不写 .materialSet() 时的自动推断：
  - 有宝石形态 → gem_vertical
  - 有金属件形态（锭/板/齿轮/杆/螺栓/环/箔/弹簧/粒/转子/细导线）→ metallic
  - 其余 → dull
11. 自动生成的东西（不用手写）
内容
位置
语言条目（11 种语言）
kubejs/assets/gt_deco_materials/lang/<语言>.json
方块状态 / 方块物品模型
kubejs/assets/gt_deco_materials/blockstates/、models/item/
矿石掉落表
kubejs/data/gt_deco_materials/loot_table/blocks/
方块/物品标签
kubejs/data/{minecraft,c}/tags/{block,item}/
语言说明（英文为主流，支持任意自定义语言文件）：
- en_us 是主力/兜底语言，每次启动按注册名重新生成。
- 另外内置 10 种译名表：zh_cn、zh_tw、zh_hk、ja_jp、ko_kr、de_de、fr_fr、es_es、ru_ru、pt_br
（中文覆盖全部 54 个形态；其余为常用形态，未翻译的逐条回落英文）。
- 自定义语言文件：在 kubejs/assets/gt_deco_materials/lang/ 下新建 任意语言代码 的 json
（如 it_it.json、uk_ua.json，甚至 xx_custom.json），只需写你翻译的那几条；
下次启动会自动把缺失的 key 用英文补齐，你已写的翻译一律不覆盖。
- 其它语种即使没有文件也没关系，Minecraft 会逐 key 回落到 en_us。
- 材料名始终是机械推导的英文（运行期无法翻译任意 KubeJS 材料名），
所以中文显示为 「Kubejs Test 锭」这种「英文材料名 + 本地形态名」。
这些都在启动时生成，所以新增材料后需要重启游戏（不是 /reload）。
12. 常见坑
1. 流体形态不要挂多个：后缀都是 fluid，会注册名冲突。一个材料一个流体形态。
2. KubeJS（Rhino）不支持 f(...array) 展开，forms(...) 的形态名必须逐个写死；
想复用就用函数包一层（见案例 E）。
3. 材料注册后不可删除：改脚本移除材料后，旧存档里的对应物品会消失。
4. 重复注册会报错：event.canRegister() 只判断窗口是否开放；重复执行脚本前建议先判重
（event.all() 里查一下 id）。
5. 套件名拼错会静默回退：materialSet('metalic') 不合法 → 按形态自动推断 → 可能不是你要的。

GT Deco & Materials · Java API 材料注册案例
给附属模组用的写法。KubeJS 的写法见 ../kubejs_examples/。
示例代码：ExampleMaterials.java（可直接抄进你的附属 mod）。
1. 接入三步
① mods.toml 声明依赖并排在本 mod 之后
[[dependencies.yourmodid]]
    modId = "gt_deco_materials"
    mandatory = true
    versionRange = "[1.0.5,)"
    ordering = "AFTER"      # 关键：保证 GTDM 初始化完再派发注册事件
    side = "BOTH"
② mod 构造器里挂监听
public YourMod(IEventBus modEventBus) {
    ExampleMaterials.init();   // 内部：NeoForge.EVENT_BUS.addListener(...)
}
③ 事件回调里注册
private static void onMaterialRegistry(MaterialRegistryEvent event) {
    if (!event.canRegister()) return;

    MaterialRegistry.register("my_alloy")
            .color(0xFF5577)
            .forms(MaterialForms.INGOT, MaterialForms.DUST)
            .build();
}
事件是 NeoForge 事件总线（NeoForge.EVENT_BUS）上的普通事件，
不是 mod 总线。它在 GTDM 落成物品/方块之前触发，此时注册窗口开放。
2. Builder API
方法
说明
.color(int rgb)
主色 0xRRGGBB；自动派生副色（压暗 30%）
.secondaryColor(int rgb)
显式副色（tintindex 1 层）
.materialSet(String)
材质套件：dull metallic shiny bright magnetic radioactive fine rough sand glass wood diamond emerald netherstar quartz opal lapis lignite flint certus ruby gem_horizontal gem_vertical paper powder
.name(lang, value)
任意语言材料名
.zhcnname(String)
简体中文材料名（镍 → 「镍锭」）
.zhtwname(String)
繁体中文材料名（zh_tw / zh_hk）
.enname(String)
英文材料名（一般不用）
.data(MaterialDataUse<?>)
附加数据，见下表
.forms(MaterialForm...)
形态常量
.forms(String...)
按名字指定形态（动态拼装用）
.build()
完成注册，返回 Material
数据（MaterialDataTypes）：
.data(MaterialDataTypes.PRIMARY_COLOR.rgb(0xFF5577))  // 一般不直接用，用 .color()
.data(MaterialDataTypes.SECONDARY_COLOR.rgb(0x5C6B78))
.data(MaterialDataTypes.MELTING_POINT.value(1358))    // 开尔文
.data(MaterialDataTypes.DENSITY.value(8960))          // kg/m³
.data(MaterialDataTypes.MASS.mass(56))                // 质量
3. 形态常量（MaterialForms.*）
分类
常量
锭
INGOT INGOT_HOT INGOT_DOUBLE RAW_ORE
粉
DUST DUST_SMALL DUST_TINY DUST_IMPURE DUST_PURE
板
PLATE PLATE_DENSE PLATE_DOUBLE
齿轮
GEAR GEAR_SMALL
杆
ROD ROD_LONG
紧固
BOLT SCREW RING FOIL WIRE_FINE
弹簧
SPRING SPRING_SMALL
粒 / 其他
NUGGET ROUND TURBINE_BLADE ROTOR NANITES
矿石加工
CRUSHED_ORE CRUSHED_REFINED PURIFIED_ORE
宝石
GEM GEM_FLAWED GEM_CHIPPED GEM_FLAWLESS GEM_EXQUISITE GEM_LENS
方块
BLOCK FRAME RAW_ORE_BLOCK
矿石母岩
ORE ORE_DEEPSLATE ORE_NETHERRACK ORE_END_STONE ORE_GRANITE ORE_DIORITE ORE_ANDESITE ORE_TUFF ORE_BLACKSTONE ORE_BASALT ORE_CALCITE ORE_MARBLE ORE_RED_GRANITE ORE_BLACK_GRANITE
流体
FLUID GAS LIQUID ELEMENT_GAS ELEMENT_LIQUID ELEMENT_FLUID
流体四个后缀都是 fluid，一个材料只能挂一个流体形态。
4. 自动生成（不用自己写）
内容
位置
语言（en_us + 10 种内置 + 任意自定义文件补齐）
kubejs/assets/gt_deco_materials/lang/
方块状态 / 方块物品模型 / 桶模型
kubejs/assets/gt_deco_materials/
矿石掉落表（粗矿 + 时运 + 精准采集）
kubejs/data/gt_deco_materials/loot_table/blocks/
方块/物品标签（mineable/pickaxe、needs_stone_tool、c:ores、c:<通用标签>）
kubejs/data/
都是启动时生成，改完要重启游戏（不是 /reload）。
5. 注意
1. 注册窗口：只在 MaterialRegistryEvent 派发期间开放；之后 build() 会在
onServerStarting 封账后抛异常。
2. 不能删除材料：build() 之后不可撤销，删除会让存档里的物品变空。
3. 重复 ID 会直接抛异常，报错里带「谁先注册的」。
4. 附属方的注册顺序：mods.toml 里务必 ordering = "AFTER"，否则可能
在 GTDM 初始化前收到不到事件。
5. 材质套件名拼错不会报错：sanitize() 返回 null 后按形态自动推断，
排查时看 Material#materialSet() 的实际值。
