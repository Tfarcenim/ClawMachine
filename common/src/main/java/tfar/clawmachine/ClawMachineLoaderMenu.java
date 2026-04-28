package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ClawMachineLoaderMenu extends AbstractContainerMenu{
    private final ContainerLevelAccess access;

    final ClawMachineBlockEntity clawMachineBlockEntity;
    private final ContainerData containerData;

    final SimpleContainer inputContainer;

    public ClawMachineLoaderMenu(int menuType, Inventory containerId) {
        this(menuType, containerId,ContainerLevelAccess.NULL,new SimpleContainer(2),new SimpleContainerData(ClawMachineBlockEntity.DATA_SLOTS));
    }

    public ClawMachineLoaderMenu(int containerId, Inventory inventory, ContainerLevelAccess access, SimpleContainer paymentContainer, ContainerData containerData) {
        super(ModMenuTypes.CLAW_MACHINE_LOADER, containerId);
        this.access = access;

        clawMachineBlockEntity = access.evaluate((level, pos) -> {
            BlockEntity blockEntity =  level.getBlockEntity(pos);
            return blockEntity instanceof ClawMachineBlockEntity ? (ClawMachineBlockEntity) blockEntity : null;
        }).orElse(null);
        this.containerData = containerData;

        inputContainer = new SimpleContainer(1){
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(this);
            }
        };

        int y0 = 55;
        addSlot(new Slot(inputContainer,0,26,y0));


        addSlot(new Slot(paymentContainer,0,26 + 3 * 18,y0));
        addSlot(new Slot(paymentContainer,1,26 + 5 * 18,y0));

        int yS = 84+19;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, yS + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 58+yS));
        }
        addDataSlots(containerData);
    }


    public double getWinChance() {
        return containerData.get(1) / 100d;
    }

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);
        this.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 ->{
                if (clawMachineBlockEntity != null) {
                    clawMachineBlockEntity.eject(player);
                }
            }
        }
        return super.clickMenuButton(player,id);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.inputContainer && clawMachineBlockEntity != null) {
            ItemStack input = container.getItem(0);
            if (!input.isEmpty()) {
                spawnPrize(input);
            }
        }
    }

    void spawnPrize(ItemStack stack) {
        BlockState state = clawMachineBlockEntity.getBlockState();
        Direction facing = state.getValue(ClawMachineBlock.FACING);
        Vec3 pos = pickPos(facing,clawMachineBlockEntity.getBlockPos(),clawMachineBlockEntity.getLevel().random);
        StaticItemEntity itemEntity = new StaticItemEntity(clawMachineBlockEntity.getLevel(),pos.x,pos.y,pos.z,stack.copyWithCount(1));
        itemEntity.randomRotations();
        clawMachineBlockEntity.getLevel().addFreshEntity(itemEntity);
        inputContainer.removeItem(0,1);
    }

    //inner bounds = 1 3/4s x 1 5/8s
    Vec3 pickPos(Direction facing, BlockPos controlPos, RandomSource random) {

        AABB clawBounds = clawMachineBlockEntity.clawBounds.move(controlPos);

        Vec3 pick = pickRandomPointIn(clawBounds,random);

        int tries = 0;

        while (true) {
            tries++;

            return pick;


         //   if (tries > 100) return new Vec3(controlPos.getX()+.5,controlPos.getY()+.75,controlPos.getZ()+.5);

        }


    }

    static Vec3 pickRandomPointIn(AABB aabb, RandomSource random) {

        boolean boundsTesting = false;



        double d = .5;
        double xWidth = aabb.getXsize();
        double zWidth = aabb.getZsize();

        if (boundsTesting) {

            boolean b1 = random.nextBoolean();
            boolean b2 = random.nextBoolean();
            double xRand = aabb.minX+ xWidth * (b1 ? 1 : 0);
            double zRand = aabb.minZ+ zWidth * (b2 ? 1 : 0);
            return new Vec3(xRand+.5,aabb.maxY-1/4d,zRand+.5);
        }

        double xRand = aabb.minX+ xWidth * random.nextDouble();
        double zRand = aabb.minZ+ zWidth * random.nextDouble();
        return new Vec3(xRand+.5,aabb.maxY-1/4d,zRand+.5);
    }


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }


    @Override
    public boolean stillValid(Player player) {
        return ClawMachineMenu.stillValid(access,player);
    }

    public void setWinChance(int v) {
        clawMachineBlockEntity.winChance = v/100d;
        clawMachineBlockEntity.setChanged();
    }

    public void setTimer(int ticks) {
        clawMachineBlockEntity.timer = ticks;
        clawMachineBlockEntity.setChanged();
    }
}
