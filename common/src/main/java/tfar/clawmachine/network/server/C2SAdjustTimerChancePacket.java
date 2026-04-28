package tfar.clawmachine.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.clawmachine.ClawMachineLoaderMenu;
import tfar.clawmachine.network.PacketHandler;
import tfar.clawmachine.platform.Services;

public record C2SAdjustTimerChancePacket(int ticks) implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SAdjustTimerChancePacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, C2SAdjustTimerChancePacket::ticks, C2SAdjustTimerChancePacket::new);

    public static final Type<C2SAdjustTimerChancePacket> TYPE = new Type<>(PacketHandler.packet(C2SAdjustTimerChancePacket.class));

    public static void send(int ticks) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(ticks);
        Services.PLATFORM.sendToServer(new C2SAdjustTimerChancePacket(ticks));
    }

    public void handleServer(ServerPlayer player) {
        if (player.containerMenu instanceof ClawMachineLoaderMenu clawMachineLoaderMenu){
            clawMachineLoaderMenu.setTimer(ticks);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

