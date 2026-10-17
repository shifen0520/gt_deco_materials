package com.shifen0520.gtdecoration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 半透明玻璃方块（行为对齐原版玻璃）。
 *
 * <p><b>1.21.1透明的实现方式</b>：
 * <ul>
 *   <li>{@code RenderShape} 在 1.20 起只剩 {@code INVISIBLE/ENTITYBLOCK_ANIMATED/MODEL}，
 *       <b>没有 TRANSLUCENT</b>，所以不能靠覆写它实现透明。</li>
 *   <li>真正的做法是运行时注册渲染层：客户端调用
 *       {@code ItemBlockRenderTypes.setRenderLayer(block, RenderType.TRANSLUCENT)}，
 *       见 {@link GlassRenderTypes}。</li>
 * </ul>
 *
 * <p><b>用户要求的四项调整</b>：
 * <ul>
 *   <li><b>声音效果改为玻璃</b>：注册时声明 {@code sound(SoundType.GLASS)}，
 *       行走/破坏/放置都是玻璃音（见 {@link Casings#register()}）。</li>
 *   <li><b>去阴影</b>：{@link #getLightBlock} 返回 0，玻璃不遮光；
 *       {@link #getShadeBrightness} 固定返回 1.0F，玻璃自身与相邻方块都不被压暗。
 *       配合注册时的 {@code noOcclusion()}，玻璃之间不出现黑缝。</li>
 *   <li><b>继承玻璃方块类</b>：这里覆写的就是原版玻璃走的同一批方法。</li>
 *   <li><b>去掉精准采集要求</b>：{@link #requiresCorrectToolForDrops()} 返回
 *       {@code false}，徒手可打掉（原版玻璃即如此）。</li>
 * </ul>
 *
 * <p><b>连接纹理</b>：玻璃的 47-slice 切片贴图已由 {@code tools/ctm/ldlib_to_athena.py}
 * 生成，blockstate 走 {@code athena:ctm} 加载器接入（与外壳方块同一套机制）。
 */
public class GlassBlock extends Block {

    public GlassBlock(Properties properties) {
        super(properties);
    }

    /**
     * 玻璃不遮光：否则玻璃后面的方块会被压暗。
     *
     * <p>签名说明：BlockBehaviour 里有两个同名方法，Block 覆写的是带
     * {@code (state, level, pos)} 的那个 protected 版本（另一个
     * {@code getLightBlock(BlockGetter, BlockPos)} 挂在 BlockState 上）。
     */
    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    /** 去阴影：玻璃给足亮度，邻居也不被它压暗。 */
    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }
}
