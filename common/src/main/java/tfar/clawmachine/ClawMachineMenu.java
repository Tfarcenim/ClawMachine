package tfar.clawmachine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClawMachineMenu extends AbstractContainerMenu {
    protected ClawMachineMenu(int containerId,Inventory inventory) {
        super(ModMenuTypes.CLAW_MACHINE, containerId);
    }

   // public ClawMachineMenu(int menuType, Inventory containerId) {
   //     this(menuType, containerId);
    //}

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return super.clickMenuButton(player, id);
    }

    public enum Controls {
        UP,DOWN,LEFT,RIGHT,FORWARD,BACK,OPEN,CLOSE
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
