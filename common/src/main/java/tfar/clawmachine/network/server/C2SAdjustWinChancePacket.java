package tfar.clawmachine.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.clawmachine.network.PacketHandler;
import tfar.clawmachine.platform.Services;

public class C2SAdjustWinChancePacket implements C2SModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SAdjustWinChancePacket> STREAM_CODEC =
            StreamCodec.ofMember(C2SAdjustWinChancePacket::write, C2SAdjustWinChancePacket::new);

    public static final CustomPacketPayload.Type<C2SAdjustWinChancePacket> TYPE = new CustomPacketPayload.Type<>(
            PacketHandler.packet(C2SAdjustWinChancePacket.class));

    private final int percentage;

    public C2SAdjustWinChancePacket(int percentage) {
        this.percentage = percentage;
    }

    public C2SAdjustWinChancePacket(FriendlyByteBuf buf) {
        percentage = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(percentage);
    }

    public static void send(int percentage) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(percentage);
        Services.PLATFORM.sendToServer(new C2SAdjustWinChancePacket(percentage));
    }

    public void handleServer(ServerPlayer player) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

