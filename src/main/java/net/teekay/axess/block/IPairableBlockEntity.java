package net.teekay.axess.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPairableBlock {
    boolean canPairWith(BlockEntity be);
    boolean canBePairedBy(Player player);
    void handlePairing(BlockEntity be);
}