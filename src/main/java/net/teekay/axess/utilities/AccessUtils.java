package net.teekay.axess.utilities;

import net.minecraft.world.entity.player.Player;
import net.teekay.axess.access.AccessNetwork;

public class AccessUtils {

    public static boolean canPlayerEditNetwork(Player player, AccessNetwork network) {
        if (network == null) return true;

        return (network.getOwnerUUID().equals(player.getUUID()) || (player.isCreative() && player.hasPermissions(4)));
    }

}
