package tfar.clawmachine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClawMachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ClawMachineBlockEntity clawMachineBlockEntity;
    private final ContainerData containerData;

    public ClawMachineMenu(int menuType, Inventory containerId) {
        this(menuType, containerId, ContainerLevelAccess.NULL, new SimpleContainerData(ClawMachineBlockEntity.DATA_SLOTS));
    }
    public ClawMachineMenu(int containerId, Inventory inventory, ContainerLevelAccess access, ContainerData containerData) {
        super(ModMenuTypes.CLAW_MACHINE, containerId);
        this.access = access;

        clawMachineBlockEntity = access.evaluate((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            return blockEntity instanceof ClawMachineBlockEntity ? (ClawMachineBlockEntity) blockEntity : null;
        }).orElse(null);
        this.containerData = containerData;

        if (clawMachineBlockEntity != null) {
            clawMachineBlockEntity.setActivePlayer(inventory.player.getUUID());
        }
        addDataSlots(containerData);
    }

    public int getCredits(){
        return containerData.get(0);
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
                clawMachineBlockEntity.clearActivePlayer();
                clawMachineBlockEntity.handleInput(0);
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return canContinuePlaying(access, player);
    }

    protected boolean canContinuePlaying(ContainerLevelAccess access, Player player) {
        return stillValid(access, player) && clawMachineBlockEntity.isAllowedToPlay(player);
    }

    protected static boolean stillValid(ContainerLevelAccess access, Player player) {
        return access.evaluate(
                (level, pos) -> level.getBlockState(pos).getBlock()
                        instanceof ClawMachineBlock && player.canInteractWithBlock(pos, 4.0), true
        );
    }
}
