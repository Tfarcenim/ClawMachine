package tfar.clawmachine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<ClawMachineMenu> CLAW_MACHINE = new MenuType<>((int menuType, Inventory containerId) -> new ClawMachineMenu(menuType, containerId), FeatureFlags.VANILLA_SET);
}
