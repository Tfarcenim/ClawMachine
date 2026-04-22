package tfar.clawmachine;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ClawMachineLoaderMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    final ClawMachineBlockEntity clawMachineBlockEntity;

    final SimpleContainer container;

    public ClawMachineLoaderMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CLAW_MACHINE_LOADER, containerId);
        this.access = access;

        clawMachineBlockEntity = access.evaluate((level, pos) -> {
            BlockEntity blockEntity =  level.getBlockEntity(pos);
            return blockEntity instanceof ClawMachineBlockEntity ? (ClawMachineBlockEntity) blockEntity : null;
        }).orElse(null);

        container = new SimpleContainer(2){
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(this);
            }
        };
        addSlot(new Slot(container,0,26,45));
        addSlot(new Slot(container,1,140,40));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 ->{
                if (clawMachineBlockEntity != null) {
                    clawMachineBlockEntity.eject();
                }
            }
        }
        return super.clickMenuButton(player,id);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.container && clawMachineBlockEntity != null) {
            ItemStack input = container.getItem(0);
            if (!input.isEmpty()) {
                Vec3 pos = Vec3.atCenterOf(clawMachineBlockEntity.getBlockPos());
                ItemEntity itemEntity = new ItemEntity(clawMachineBlockEntity.getLevel(),pos.x,pos.y,pos.z,input);
                itemEntity.setUnlimitedLifetime();
                itemEntity.setPickUpDelay(200);
                clawMachineBlockEntity.getLevel().addFreshEntity(itemEntity);
                container.setItem(0,ItemStack.EMPTY);
            }
        }
    }

    public ClawMachineLoaderMenu(int menuType, Inventory containerId) {
        this(menuType, containerId,ContainerLevelAccess.NULL);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
