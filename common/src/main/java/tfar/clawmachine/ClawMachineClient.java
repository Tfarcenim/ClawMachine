package tfar.clawmachine;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

public class ClawMachineClient {

    public static final ResourceLocation CABLE = ClawMachine.id("block/clawcable");
    public static final ResourceLocation CLOSED = ClawMachine.id("block/clawclosed");
    public static final ResourceLocation OPEN = ClawMachine.id("block/clawopened");
    public static final ResourceLocation TOP = ClawMachine.id("block/clawtop");

    public static void renderer(){
        BlockEntityRenderers.register(ModBlockEntityTypes.CLAW_MACHINE, ClawMachineBlockEntityRenderer::new);

        MenuScreens.register(ModMenuTypes.CLAW_MACHINE,ClawMachineScreen::new);
    }

}
