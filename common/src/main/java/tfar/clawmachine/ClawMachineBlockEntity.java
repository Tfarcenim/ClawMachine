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

public class ClawMachineBlockEntity extends BlockEntity implements MenuProvider {


    public Vec3 clawPos = Vec3.ZERO;
    public Vec3 prevClawPos = Vec3.ZERO;

    Vec3 clawVelocity = Vec3.ZERO;

    public boolean clawClosed;

    int ticksSinceGrab;

    ItemStack grabbed = ItemStack.EMPTY;

    public ClawMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CLAW_MACHINE, pos, blockState);
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
    }

    public void processControls(double x,double y,double z) {
        clawPos.add(x,y,z);
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        clawClosed = tag.getBoolean("claw_closed");
        CompoundTag t = tag.getCompound("claw_pos");
        clawPos = new Vec3(t.getDouble("x"),t.getDouble("y"),t.getDouble("z"));
        prevClawPos = clawPos;
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

    void serverTick() {
        boolean moved = updateClawPos();
        if (moved) {
            prevClawPos = clawPos;
            setChanged();
        }
    }

    boolean updateClawPos() {
        boolean moved = !Vec3.ZERO.equals(clawVelocity);
        clawPos = clawPos.add(clawVelocity);
        if (!getBounds().contains(clawPos)) {
            putInBounds();
        }
        return moved;
    }

    AABB getBounds(){
        return switch (getBlockState().getValue(ClawMachineBlock.FACING)) {
            default -> {
                yield new AABB(-1.125,-1,0,.125,1,1.25);
            }
            case EAST -> {
                yield new AABB(-2,-2,-2,2,2,2);

            }
            case SOUTH -> {
                yield new AABB(-.125,-1,-1.25,1.125,1,0);

            }
            case WEST -> {
                yield new AABB(-2,-2,-2,2,2,2);
            }
        };
    }

    void putInBounds() {
        AABB bounds = getBounds();
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

            }
        }
    }

    public void eject() {

    }
}
