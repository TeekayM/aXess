package net.teekay.axess.network.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.block.keycardeditor.KeycardEditorBlockEntity;
import net.teekay.axess.item.keycard.AbstractKeycardItem;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.screen.KeycardEditorMenu;
import net.teekay.axess.utilities.AccessUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSModifyKeycardPacket implements IAxessPacket {
    public BlockPos blockEntityPos;
    public UUID networkUUID;
    public UUID accessLevelUUID;

    public CtSModifyKeycardPacket(BlockPos blockEntityPos, AccessNetwork network, AccessLevel level) {
        this.blockEntityPos = blockEntityPos;
        this.networkUUID = network.getUUID();
        this.accessLevelUUID = level.getUUID();
    }

    public CtSModifyKeycardPacket(FriendlyByteBuf buffer) {
        this.blockEntityPos = buffer.readBlockPos();
        this.networkUUID = buffer.readUUID();
        this.accessLevelUUID = buffer.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockEntityPos);
        buffer.writeUUID(networkUUID);
        buffer.writeUUID(accessLevelUUID);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(false);
            return;
        }

        context.enqueueWork(() -> {
            try {
                ServerPlayer player = context.getSender();
                if (player == null) return;

                if (!(player.containerMenu instanceof KeycardEditorMenu menu)) return;
                KeycardEditorBlockEntity keycardEditor = menu.blockEntity;

                Optional<IItemHandler> itemHandler = keycardEditor.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.NORTH).resolve();
                if (itemHandler.isEmpty()) return;

                ItemStack stack = itemHandler.get().getStackInSlot(KeycardEditorBlockEntity.KEYCARD_SLOT);
                if (!(stack.getItem() instanceof AbstractKeycardItem keycardItem)) return;

                AccessNetwork prevNetwork = keycardItem.getAccessNetwork(stack, player.level());
                if (prevNetwork != null) if (!AccessUtils.canPlayerEditNetwork(player, prevNetwork)) return;

                AccessNetworkDataServer serverNetworkData = AccessNetworkDataServer.get(player.getServer());
                AccessNetwork network = serverNetworkData.getNetwork(networkUUID);
                if (network == null) return;

                AccessLevel accessLevel = network.getAccessLevel(accessLevelUUID);
                if (accessLevel == null) return;

                keycardItem.setAccessNetwork(stack, network);
                keycardItem.setAccessLevel(stack, accessLevel);

                //stack.setHoverName(Component.literal(accessLevel.getName()).append(" ").append(Component.translatable("item." + Axess.MODID + ".keycard")));

                keycardEditor.setChanged();
            } catch (Exception e) {}
        });
    }
}
