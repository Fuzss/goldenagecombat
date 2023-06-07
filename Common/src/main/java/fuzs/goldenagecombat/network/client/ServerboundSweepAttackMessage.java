package fuzs.goldenagecombat.network.client;

import fuzs.goldenagecombat.handler.SweepAttackHandler;
import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;

public record ServerboundSweepAttackMessage(boolean usingSecondaryAction) implements ServerboundMessage<ServerboundSweepAttackMessage> {

    @Override
    public ServerMessageListener<ServerboundSweepAttackMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundSweepAttackMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                // mimics behavior of ServerboundInteractPacket as that one is used in combat tests
                player.setShiftKeyDown(message.usingSecondaryAction);
                if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                    SweepAttackHandler.attackAir(player);
                }
            }
        };
    }
}
