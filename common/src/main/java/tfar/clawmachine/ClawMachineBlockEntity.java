package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class ClawMachineBlockEntity extends BlockEntity {

    Vector3d clawPos = new Vector3d();
    boolean claw;

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
        tag.putBoolean("claw",claw);
    }

    public void processControls(double x,double y,double z) {
        clawPos.add(x,y,z);
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        claw = tag.getBoolean("claw");
        CompoundTag t = tag.getCompound("claw_pos");
        clawPos = new Vector3d(t.getDouble("x"),t.getDouble("y"),t.getDouble("z"));
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
}
