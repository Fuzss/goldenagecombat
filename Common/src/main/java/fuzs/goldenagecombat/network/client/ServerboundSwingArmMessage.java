package fuzs.goldenagecombat.network.client;

import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;

public record ServerboundSwingArmMessage(InteractionHand hand) implements ServerboundMessage<ServerboundSwingArmMessage> {

    @Override
    public ServerMessageListener<ServerboundSwingArmMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundSwingArmMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                player.swing(message.hand);
            }
        };
    }
}
