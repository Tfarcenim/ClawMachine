package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClawMachineBlockEntity extends BlockEntity implements MenuProvider {


    public Vec3 clawPos;
    public Vec3 prevClawPos;

    Vec3 clawVelocity = Vec3.ZERO;
    final Vec3 defaultClawPos;

    public boolean clawClosed;
    public ItemStack grabbedItem = ItemStack.EMPTY;

    boolean moveToStart;

    public ClawMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CLAW_MACHINE, pos, blockState);
        clawBounds = getClawBounds();
        winBounds = getWinBounds();
        defaultClawPos = getDefaultClawPos(blockState.getValue(ClawMachineBlock.FACING));
        clawPos = defaultClawPos;
        prevClawPos = defaultClawPos;
    }

    Vec3 getDefaultClawPos(Direction facing) {
        return switch (facing) {
            default -> new Vec3(0,.5,0);
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag t = new CompoundTag();
        t.putDouble("x",clawPos.x);
        t.putDouble("y",clawPos.y);
        t.putDouble("z",clawPos.z);
        tag.put("claw_pos",t);
        tag.putBoolean("claw_closed", clawClosed);
        tag.put("grabbed_item",grabbedItem.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        clawClosed = tag.getBoolean("claw_closed");
        CompoundTag t = tag.getCompound("claw_pos");
        clawPos = new Vec3(t.getDouble("x"),t.getDouble("y"),t.getDouble("z"));
        prevClawPos = clawPos;
        grabbedItem = ItemStack.parseOptional(registries,tag.getCompound("grabbed_item"));

    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(), ClawMachineBlock.UPDATE_ALL);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Claw Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ClawMachineMenu(containerId,playerInventory, ContainerLevelAccess.create(level,worldPosition));
    }

    @Nullable
    public AbstractContainerMenu createLoaderMenu(int containerId, Inventory playerInventory, Player player) {
        return new ClawMachineLoaderMenu(containerId,playerInventory, ContainerLevelAccess.create(level,worldPosition));
    }

    //red
    // 0 to .5
    // .5 to .25
    //1 to 0
    public AABB getClawHitbox() {
        double w = 3/16d;
        double o = .5 - w/2;
        return new AABB(clawPos.add(o,1/8d,o),clawPos.add(w+o,.25,w+o)).move(worldPosition);
    }

    void serverTick() {
        boolean moved = updateClawPos();
        if (moved) {
            prevClawPos = clawPos;
            if (moveToStart) {
                if (grabbedItem.isEmpty()) {
                    //check for overlapping hitboxes
                    ItemStack stack = tryGrab();
                    if (stack.isEmpty()) {
                        if (clawPos.y <=clawYMin) {
                            clawVelocity = new Vec3(0,clawSpeed,0);
                        } else if (clawPos.y >= .5) {
                            clawPos = new Vec3(clawPos.x, .5, clawPos.z);
                            moveToStart = false;
                            clawVelocity = Vec3.ZERO;
                        }
                    } else {
                        grabbedItem = stack;
                        clawClosed = true;
                        clawVelocity = new Vec3(0,clawSpeed,0);
                    }
                } else {
                    if (clawPos.y >= .5) {
                        clawPos = new Vec3(clawPos.x, .5, clawPos.z);
                        moveToStart = false;
                        clawVelocity = Vec3.ZERO;
                    }
                }
            }
            setChanged();
        }
        List<StaticItemEntity> staticItemEntities = level.getEntitiesOfClass(StaticItemEntity.class,winBounds);
        for (StaticItemEntity staticItemEntity : staticItemEntities) {
            Vec3 pos = Vec3.atBottomCenterOf(worldPosition.relative(getBlockState().getValue(ClawMachineBlock.FACING)));
            ItemEntity itemEntity = new ItemEntity(level,pos.x,pos.y-.5,pos.z,staticItemEntity.getItem().copy());
            level.addFreshEntity(itemEntity);
            staticItemEntity.discard();
        }
    }

    ItemStack tryGrab() {
        AABB clawHitbox = getClawHitbox();
        List<StaticItemEntity> staticItemEntities = level.getEntitiesOfClass(StaticItemEntity.class,clawHitbox);
        if (!staticItemEntities.isEmpty()) {
            StaticItemEntity staticItemEntity = staticItemEntities.getFirst();
            ItemStack stack = staticItemEntity.getItem().copy();
            staticItemEntity.discard();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    boolean updateClawPos() {
        boolean moved = !Vec3.ZERO.equals(clawVelocity);
        clawPos = clawPos.add(clawVelocity);
        if (!getClawBounds().contains(clawPos)) {
            putInBounds();
        }
        return moved;
    }

    static final double clawYMin = -3/16d;
    static final double clawYMax = .5;

    public final AABB clawBounds;

    private AABB getClawBounds(){
        return switch (getBlockState().getValue(ClawMachineBlock.FACING)) {
            default -> new AABB(-1.125,clawYMin,0,.125,clawYMax,1.25);
            case EAST -> new AABB(-9/8d,clawYMin,-9/8d,-1/16d,clawYMax,1/8d);
            case SOUTH -> new AABB(-.125,clawYMin,-1.25,1.125,clawYMax,0);
            case WEST -> new AABB(1/16d,clawYMin,-1/8d,9/8d,clawYMax,9/8d);
        };
    }

    public final AABB winBounds;

    private AABB getWinBounds(){
        double w = 3/8d;
        Direction facing = getBlockState().getValue(ClawMachineBlock.FACING);

        double x = switch (facing) {
            default -> 4/8d;
            case EAST -> 5/16d;
            case SOUTH -> 1/8d;
            case WEST -> 5/16d;
        };

        double z = switch (facing) {
            default -> 5/16d;
            case EAST -> 4/8d;
            case SOUTH -> 5/16d;
            case WEST -> 1/8d;
        };

        AABB box = new AABB(x,-1/4d,z,x+w,0,z+w);
        return box.move(worldPosition);
    }

    void putInBounds() {
        AABB bounds = getClawBounds();
        double x = Math.clamp(clawPos.x,bounds.minX,bounds.maxX);
        double y = Math.clamp(clawPos.y,bounds.minY,bounds.maxY);
        double z = Math.clamp(clawPos.z,bounds.minZ,bounds.maxZ);
        clawPos = new Vec3(x,y,z);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClawMachineBlockEntity clawMachineBlockEntity) {
        clawMachineBlockEntity.serverTick();
    }

    static double clawSpeed = 1/32d;

    public void handleInput(int id) {
        Direction facing = getBlockState().getValue(ClawMachineBlock.FACING);
        if (moveToStart) {return;}
        switch (id) {
            default -> clawVelocity = Vec3.ZERO;
            case 0 -> clawVelocity = Vec3.ZERO;
            case 1 -> {//forward right
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(-clawSpeed,0,clawSpeed);
                    case EAST -> clawVelocity = new Vec3(-clawSpeed,0,-clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(clawSpeed,0,-clawSpeed);
                    case WEST -> clawVelocity = new Vec3(clawSpeed,0,clawSpeed);
                }
            }
            case 2 -> {//right
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(-clawSpeed,0,0);
                    case EAST -> clawVelocity = new Vec3(0,0,-clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(clawSpeed,0,0);
                    case WEST -> clawVelocity = new Vec3(0,0,clawSpeed);
                }
            }
            case 3 -> {//back right
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(-clawSpeed,0,-clawSpeed);
                    case EAST -> clawVelocity = new Vec3(clawSpeed,0,-clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(clawSpeed,0,clawSpeed);
                    case WEST -> clawVelocity = new Vec3(-clawSpeed,0,clawSpeed);
                }
            }
            case 4 -> {//back
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(0,0,-clawSpeed);
                    case EAST -> clawVelocity = new Vec3(clawSpeed,0,0);
                    case SOUTH -> clawVelocity = new Vec3(0,0,clawSpeed);
                    case WEST -> clawVelocity = new Vec3(-clawSpeed,0,0);
                }
            }
            case 5 -> {//back left
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(clawSpeed,0,-clawSpeed);
                    case EAST -> clawVelocity = new Vec3(clawSpeed,0,clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(-clawSpeed,0,clawSpeed);
                    case WEST -> clawVelocity = new Vec3(-clawSpeed,0,-clawSpeed);
                }
            }
            case 6 -> {//left
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(clawSpeed,0,0);
                    case EAST -> clawVelocity = new Vec3(0,0,clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(-clawSpeed,0,0);
                    case WEST -> clawVelocity = new Vec3(0,0,-clawSpeed);
                }
            }
            case 7 -> {//forward left
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(clawSpeed,0,clawSpeed);
                    case EAST -> clawVelocity = new Vec3(-clawSpeed,0,clawSpeed);
                    case SOUTH -> clawVelocity = new Vec3(-clawSpeed,0,-clawSpeed);
                    case WEST -> clawVelocity = new Vec3(clawSpeed,0,-clawSpeed);
                }
            }
            case 8 -> {//forward
                switch (facing) {
                    case NORTH -> clawVelocity = new Vec3(0,0,clawSpeed);
                    case EAST -> clawVelocity = new Vec3(-clawSpeed,0,0);
                    case SOUTH -> clawVelocity = new Vec3(0,0,-clawSpeed);
                    case WEST -> clawVelocity = new Vec3(clawSpeed,0,0);
                }
            }
            case 9 -> {// grab/release
                if (grabbedItem.isEmpty()) {//don't process if already grabbing
                    moveToStart = true;
                    clawVelocity = new Vec3(0,-clawSpeed/2,0);
                } else {
                    Vec3 spawn = clawPos.add(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());
                    StaticItemEntity staticItemEntity = new StaticItemEntity(level,spawn.x+.5,spawn.y,spawn.z+.5,grabbedItem.copy());
                    level.addFreshEntity(staticItemEntity);
                    clawClosed = false;
                    grabbedItem = ItemStack.EMPTY;
                    setChanged();
                }
            }
        }
    }

    public void eject(Player player) {
        List<StaticItemEntity> staticItemEntity = level.getEntitiesOfClass(StaticItemEntity.class,getClawBounds().inflate(1,1,1).move(worldPosition));
        staticItemEntity.forEach(staticItemEntity1 -> {
            staticItemEntity1.setPos(player.position());
        });
    }
}
