package tfar.clawmachine.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.RenderType;
import tfar.clawmachine.ModBlocks;

public class ClawMachineFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModBlocks.CLAW_MACHINES.forEach(b -> BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout()));
        ClawMachineClient.renderer();
        ModelLoadingPlugin.register(new ModelLoadingPlugin() {
            @Override
            public void onInitializeModelLoader(Context pluginContext) {
                pluginContext.addModels(ClawMachineClient.CABLE);
                pluginContext.addModels(ClawMachineClient.CLOSED);
                pluginContext.addModels(ClawMachineClient.OPEN);
                pluginContext.addModels(ClawMachineClient.CLOSED_90);
                pluginContext.addModels(ClawMachineClient.OPEN_90);
                pluginContext.addModels(ClawMachineClient.TOP);
            }
        });
    }
}
