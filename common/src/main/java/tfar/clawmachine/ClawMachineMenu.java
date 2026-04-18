package tfar.clawmachine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ClawMachineMenu extends AbstractContainerMenu {
    public ClawMachineMenu(int containerId,Inventory inventory) {
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
        Controls controls = Controls.values()[id];

        switch (controls) {
            case LEFT -> {
            }
            case RIGHT -> {
            }
            case FORWARD -> {
            }
            case BACK -> {
            }

            case GRAB -> {
            }
        }

        return true;
    }

    public enum Controls {
        LEFT,RIGHT,FORWARD,BACK, GRAB
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
