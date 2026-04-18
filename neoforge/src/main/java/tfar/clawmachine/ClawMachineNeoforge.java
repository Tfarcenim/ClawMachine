package tfar.clawmachine;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import tfar.clawmachine.datagen.ModDatagen;

@Mod(Constants.MOD_ID)
public class ClawMachineNeoforge {

    public ClawMachineNeoforge(IEventBus eventBus) {
        eventBus.addListener(this::register);
        eventBus.addListener(ModDatagen::gather);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        ClawMachine.init();

    }

    void register(RegisterEvent event) {
        ClawMachine.register();
    }
}