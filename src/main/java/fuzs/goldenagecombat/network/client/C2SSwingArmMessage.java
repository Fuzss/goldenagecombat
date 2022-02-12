package fuzs.goldenagecombat.network.client;

import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class C2SSwingArmMessage implements Message {
    private InteractionHand hand;

    public C2SSwingArmMessage() {
    }

    public C2SSwingArmMessage(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
    }

    @Override
    public SwingArmHandler makeHandler() {
        return new SwingArmHandler();
    }

    private static class SwingArmHandler extends PacketHandler<C2SSwingArmMessage> {
        @Override
        public void handle(C2SSwingArmMessage packet, Player player, Object gameInstance) {
            player.swing(packet.hand);
        }
    }
}
