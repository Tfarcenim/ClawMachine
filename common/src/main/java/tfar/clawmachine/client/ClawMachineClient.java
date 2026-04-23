package tfar.clawmachine.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.ModBlockEntityTypes;
import tfar.clawmachine.ModEntityTypes;
import tfar.clawmachine.ModMenuTypes;

public class ClawMachineClient {

    public static final ResourceLocation CABLE = ClawMachine.id("block/clawcable");
    public static final ResourceLocation CLOSED = ClawMachine.id("block/clawclosed");
    public static final ResourceLocation OPEN = ClawMachine.id("block/clawopened");
    public static final ResourceLocation TOP = ClawMachine.id("block/clawtop");

    public static void renderer(){
        BlockEntityRenderers.register(ModBlockEntityTypes.CLAW_MACHINE, ClawMachineBlockEntityRenderer::new);
        EntityRenderers.register(ModEntityTypes.STATIC_ITEM_ENTITY,StaticItemEntityRenderer::new);
        MenuScreens.register(ModMenuTypes.CLAW_MACHINE,ClawMachineScreen::new);
        MenuScreens.register(ModMenuTypes.CLAW_MACHINE_LOADER,ClawMachineLoaderScreen::new);
    }

}
