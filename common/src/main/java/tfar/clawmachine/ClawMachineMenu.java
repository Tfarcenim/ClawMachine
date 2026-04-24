package tfar.clawmachine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClawMachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public ClawMachineMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CLAW_MACHINE, containerId);
        this.access = access;
    }

    public ClawMachineMenu(int menuType, Inventory containerId) {
        this(menuType, containerId,ContainerLevelAccess.NULL);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        access.execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ClawMachineBlockEntity clawMachineBlockEntity) {
                clawMachineBlockEntity.handleInput(id);
            }
        });
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ClawMachineBlockEntity clawMachineBlockEntity) {
                clawMachineBlockEntity.handleInput(0);
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access,player,ModBlocks.CLAW_MACHINE);
    }
}
