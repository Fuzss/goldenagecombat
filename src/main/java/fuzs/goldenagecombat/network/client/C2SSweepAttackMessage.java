package fuzs.goldenagecombat.network.client;

import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class C2SSweepAttackMessage implements Message {
    private boolean usingSecondaryAction;

    public C2SSweepAttackMessage() {

    }

    public C2SSweepAttackMessage(boolean usingSecondaryAction) {
        this.usingSecondaryAction = usingSecondaryAction;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.usingSecondaryAction);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.usingSecondaryAction = buf.readBoolean();
    }

    @Override
    public SweepAttackHandler makeHandler() {
        return new SweepAttackHandler();
    }

    private static class SweepAttackHandler extends PacketHandler<C2SSweepAttackMessage> {
        @Override
        public void handle(C2SSweepAttackMessage packet, Player player, Object gameInstance) {
            // mimics behavior of ServerboundInteractPacket as that one is used in combat tests
            player.setShiftKeyDown(packet.usingSecondaryAction);
            if (((ServerPlayer) player).gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                fuzs.goldenagecombat.handler.SweepAttackHandler.attackAir(player);
            }
        }
    }
}
