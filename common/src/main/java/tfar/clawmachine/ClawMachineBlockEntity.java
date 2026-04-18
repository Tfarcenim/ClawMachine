package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        //tag.put("claw_pos",)
        tag.putBoolean("claw",claw);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        claw = tag.getBoolean("claw");
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(), ClawMachineBlock.UPDATE_ALL);
    }
}
