package com.shifen0520.gtdecoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 橡胶原木：<b>正常掉落原木</b>，<b>额外有概率掉落粘性树脂</b>。
 *
 * <p>用户需求：「橡胶原木破坏掉落树脂，树脂是额外掉几率的」
 * —— 即原木本身照掉不误，树脂是在此之上的额外产出。
 *
 * <p>所以这里<b>不覆写</b> {@link #getDrops}：让原木的默认掉落表正常生效，
 * 该掉 rubber_log 就掉 rubber_log。只在破坏结算时按概率额外 {@code popResource} 树脂。
 *
 * <p>原木的行为（{@code axis} 属性、横竖放置、木头声音、可燃）全部继承
 * {@link RotatedPillarBlock}，与原版橡木原木一致。
 */
public class ResinLogBlock extends RotatedPillarBlock {

    /** 额外掉落树脂的概率（0.35 = 35%）。 */
    private static final float RESIN_CHANCE = 0.35F;
    /** 触发时树脂的数量区间（含两端）。 */
    private static final int RESIN_MIN = 1;
    private static final int RESIN_MAX = 3;

    public ResinLogBlock(Properties properties) {
        super(properties);
    }

    /**
     * 破坏时按概率额外掉树脂。
     *
     * <p>原木本体的掉落不受影响 —— {@code super.playerWillDestroy} 会走方块自身的
     * 破坏流程（含 loot table），树脂是在这之外叠加的额外产出。
     *
     * <p>注意 1.21.1 的签名返回 {@link BlockState}（表示破坏后该位置的新状态），
     * 必须原样返回 {@code super} 的结果，不能自己造一个。
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // 先走原木自己的破坏与掉落流程（loot table 正常掉 rubber_log）
        BlockState result = super.playerWillDestroy(level, pos, state, player);

        // 服务端才结算掉落，且要判定概率
        if (!level.isClientSide && level.random.nextFloat() < RESIN_CHANCE) {
            Item resin = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(GTDecoration.MODID, "sticky_resin"));
            if (resin == net.minecraft.world.item.Items.AIR) {
                GTDecoration.LOGGER.warn("sticky_resin 未注册，rubber_log 不会掉落树脂。");
            } else {
                int span = RESIN_MAX - RESIN_MIN + 1;
                int count = RESIN_MIN + level.random.nextInt(span);
                for (int i = 0; i < count; i++) {
                    // 1.21.1 的 popResource 收 ItemStack，不是 Item
                    Block.popResource(level, pos, new ItemStack(resin));
                }
            }
        }
        return result;
    }
}
