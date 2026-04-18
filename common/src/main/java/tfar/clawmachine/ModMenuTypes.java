package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<ClawMachineMenu> CLAW_MACHINE = new MenuType<>(ClawMachineMenu::new, FeatureFlags.VANILLA_SET);

    static {
        Registry.register(BuiltInRegistries.MENU,ClawMachine.id("claw_machine"),CLAW_MACHINE);
    }

    public static void init(){

    }

}
