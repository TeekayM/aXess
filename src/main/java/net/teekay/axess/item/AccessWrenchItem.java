package net.teekay.axess.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AccessWrenchItem extends Item {
    public AccessWrenchItem() {
        super(new Properties().stacksTo(1));
    }
}
