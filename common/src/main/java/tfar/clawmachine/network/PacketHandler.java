package tfar.clawmachine.network;

import net.minecraft.resources.ResourceLocation;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.network.server.C2SAdjustTimerChancePacket;
import tfar.clawmachine.network.server.C2SAdjustWinChancePacket;
import tfar.clawmachine.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerServerPacket(C2SAdjustWinChancePacket.TYPE, C2SAdjustWinChancePacket.STREAM_CODEC);
        Services.PLATFORM.registerServerPacket(C2SAdjustTimerChancePacket.TYPE, C2SAdjustTimerChancePacket.STREAM_CODEC);


        ///////server to client

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return ClawMachine.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
