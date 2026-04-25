package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
        addSlot(new Slot(container,1,134,47));

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
                    clawMachineBlockEntity.eject(player);
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
                spawnPrize(input);
            }
        }
    }

    void spawnPrize(ItemStack stack) {
        BlockState state = clawMachineBlockEntity.getBlockState();
        Direction facing = state.getValue(ClawMachineBlock.FACING);
        Vec3 pos = pickPos(facing,clawMachineBlockEntity.getBlockPos(),clawMachineBlockEntity.getLevel().random);
        StaticItemEntity itemEntity = new StaticItemEntity(clawMachineBlockEntity.getLevel(),pos.x,pos.y,pos.z,stack.copyWithCount(1));
        clawMachineBlockEntity.getLevel().addFreshEntity(itemEntity);
        container.removeItem(0,1);
    }

    //inner bounds = 1 3/4s x 1 5/8s
    Vec3 pickPos(Direction facing, BlockPos controlPos, RandomSource random) {
        float width = ModEntityTypes.STATIC_ITEM_ENTITY.getDimensions().width();

        int tries = 0;

        while (true) {
            tries++;


            double randX = random.nextDouble() * (26/16d - width/2);
            double randZ = random.nextDouble() * (24/16d - width/2);

            Vec3 potential = switch (facing) {
                case NORTH -> {
                    double x = controlPos.getX() - randX - width/2 + 14/16d;

                    double z = controlPos.getZ()+randZ+5/16d + width/2;
                    yield  new Vec3(x,controlPos.getY()+5/8d,z);
                }

                case EAST -> {
                    double x = controlPos.getX() - randZ - width/2 + 14/16d;

                    double z = controlPos.getZ()+randX+5/16d + width/2;
                    yield  new Vec3(x,controlPos.getY()+5/8d,z);
                }

                case SOUTH -> {
                    double x = controlPos.getX() + randX + width/2 + 2/16d;

                    double z = controlPos.getZ()-randZ + 7/16d + width/2;
                    yield  new Vec3(x,controlPos.getY()+5/8d,z);
                }

                case WEST -> {
                    double x = controlPos.getX() + randZ - width/2 + 9/16d;

                    double z = controlPos.getZ()+randX+2/16d + width/2;
                    yield  new Vec3(x,controlPos.getY()+5/8d,z);
                }

                default -> Vec3.ZERO;
            };

            return potential;


         //   if (tries > 100) return new Vec3(controlPos.getX()+.5,controlPos.getY()+.75,controlPos.getZ()+.5);

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
        return stillValid(access,player,ModBlocks.CLAW_MACHINE);
    }
}
