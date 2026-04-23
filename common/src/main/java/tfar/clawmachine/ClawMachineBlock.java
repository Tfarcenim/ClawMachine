package tfar.clawmachine;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tfar.clawmachine.state.properties.BProperties;
import tfar.clawmachine.state.properties.Corner;
import tfar.clawmachine.state.properties.TripleBlockThird;

import java.util.ArrayList;
import java.util.List;

public class ClawMachineBlock extends HorizontalDirectionalBlock implements EntityBlock {


    //CM = Claw Machine
//1 2 3 = The block layer
//B = Back
//F = Front
//L = Left
//R = Right
//(Your Left and Right looking at the front of it)
    public static final MapCodec<ClawMachineBlock> CODEC = simpleCodec(ClawMachineBlock::new);

    public static final EnumProperty<TripleBlockThird> THIRD = BProperties.TRIPLE_BLOCK_THIRD;
    public static final EnumProperty<Corner> CORNER = BProperties.CORNER;

    public ClawMachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(THIRD, TripleBlockThird.LOWER).setValue(CORNER, Corner.FRONT_LEFT));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        return isAllowedHere(blockpos, level, context, context.getHorizontalDirection()) ? this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                :/*blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)
                ?
                */ null;
    }

    public boolean isAllowedHere(BlockPos pos, Level level, BlockPlaceContext context, Direction facing) {
        if (pos.getY() >= level.getMaxBuildHeight() - 2) return false;
        for (int y = 0; y < 3; y++) {
            BlockPos pos0 = pos.above(y);
            BlockPos pos1 = pos.relative(facing).above(y);
            BlockPos pos2 = pos.relative(facing.getCounterClockWise()).above(y);
            BlockPos pos3 = pos.relative(facing.getCounterClockWise()).relative(facing).above(y);
            if (level.getBlockState(pos0).canBeReplaced(context) && level.getBlockState(pos1).canBeReplaced(context) &&
                    level.getBlockState(pos2).canBeReplaced(context) && level.getBlockState(pos3).canBeReplaced(context)) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            Direction direction = pState.getValue(FACING);
            Corner corner = pState.getValue(CORNER);
            TripleBlockThird third = pState.getValue(THIRD);

            List<BlockPos> others = findOtherPos(pos, direction, corner, third);

            for (BlockPos other : others) {
                pLevel.destroyBlock(other, true);
            }
            super.onRemove(pState, pLevel, pos, pNewState, pIsMoving);
        }
    }

    protected List<BlockPos> findOtherPos(BlockPos pos, Direction facing, Corner corner, TripleBlockThird third) {
        List<BlockPos> posList = new ArrayList<>();

        int yStart = pos.getY() + (1 - third.layer);

        for (int y = yStart; y < yStart + 3; y++) {

            switch (corner) {
                case FRONT_LEFT -> {
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getOpposite()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getCounterClockWise()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getOpposite()).relative(facing.getCounterClockWise()));
                }
                case FRONT_RIGHT -> {
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getOpposite()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getClockWise()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getOpposite()).relative(facing.getClockWise()));
                }
                case BACK_LEFT -> {
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getCounterClockWise()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing).relative(facing.getCounterClockWise()));
                }
                case BACK_RIGHT -> {
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing.getClockWise()));
                    posList.add(new BlockPos(pos.getX(), y, pos.getZ()).relative(facing).relative(facing.getClockWise()));
                }
            }
        }
        posList.remove(pos);
        return posList;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            ClawMachineBlockEntity clawMachineBlockEntity = locate(level, pos, state);
            if (clawMachineBlockEntity != null) {
                player.openMenu(clawMachineBlockEntity);
            }
        }
        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModItems.KEY)){
            ClawMachineBlockEntity clawMachineBlockEntity = locate(level, pos, state);
            if (clawMachineBlockEntity != null) {
                player.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return clawMachineBlockEntity.getDisplayName();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                        return clawMachineBlockEntity.createLoaderMenu(containerId, playerInventory, player);
                    }
                });
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    ClawMachineBlockEntity locate(Level level, BlockPos pos, BlockState state) {
        TripleBlockThird third = state.getValue(THIRD);
        Corner corner = state.getValue(CORNER);
        Direction facing = state.getValue(FACING);
        int y = pos.getY() + (2 - third.layer);

        BlockPos entityPos = switch (corner) {
            case FRONT_LEFT -> new BlockPos(pos.getX(),y,pos.getZ());
            case FRONT_RIGHT -> new BlockPos(pos.getX(),y,pos.getZ()).relative(facing.getClockWise());
            case BACK_LEFT -> new BlockPos(pos.getX(),y,pos.getZ()).relative(facing);
            case BACK_RIGHT -> new BlockPos(pos.getX(),y,pos.getZ()).relative(facing.getClockWise()).relative(facing);
        };
        BlockEntity blockEntity = level.getBlockEntity(entityPos);
        return blockEntity instanceof ClawMachineBlockEntity ? (ClawMachineBlockEntity) blockEntity:null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            if (player.isCreative()) {
                preventDropFromOtherParts(level, pos, state, player);
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Called after a player has successfully harvested this block. This method will only be called if the player has used the correct tool and drops should be spawned.
     */
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    protected static void preventDropFromOtherParts(Level level, BlockPos pos, BlockState state, Player player) {
        TripleBlockThird doubleblockhalf = state.getValue(THIRD);
        if (doubleblockhalf == TripleBlockThird.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(THIRD) == TripleBlockThird.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, THIRD, CORNER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        //place other blocks
        placeLayer(level, pos, state, facing);
    }

    //a-b
    static VoxelShape subtract(VoxelShape a,VoxelShape b) {
        return Shapes.join(a,b, BooleanOp.ONLY_FIRST);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        TripleBlockThird third = state.getValue(THIRD);

        switch (third) {
            case UPPER -> {
                return box(0, 0, 0, 16, 8, 16);
            }
            case MIDDLE -> {
                Direction facing = state.getValue(FACING);
                switch (facing) {
                    default -> {
                        Corner corner = state.getValue(CORNER);
                        return switch (corner) {
                            case FRONT_LEFT -> subtract(box(0,0,4,15,16,16),box(0,0,5,14,16,16));
                            case FRONT_RIGHT -> subtract(box(1,0,4,16,16,16),box(2,0,5,16,16,16));
                            case BACK_LEFT -> subtract(box(0,0,0,15,16,16),box(0,0,0,14,16,15));
                            case BACK_RIGHT -> subtract(box(1,0,0,16,16,16),box(2,0,0,16,16,15));
                        };
                    }
                    case EAST -> {
                        Corner corner = state.getValue(CORNER);
                        return switch (corner) {
                            case FRONT_LEFT -> box(0,0,0,12,16,15);
                            case FRONT_RIGHT -> box(0,0,1,12,16,16);
                            case BACK_LEFT -> box(0,0,0,16,16,15);
                            case BACK_RIGHT -> box(0,0,1,16,16,16);
                        };
                    }
                    case SOUTH -> {
                        Corner corner = state.getValue(CORNER);
                        return switch (corner) {
                            case FRONT_LEFT -> box(1,0,0,16,16,12);
                            case FRONT_RIGHT -> box(0,0,0,15,16,12);
                            case BACK_LEFT -> box(1,0,0,16,16,16);
                            case BACK_RIGHT -> box(0,0,0,15,16,16);
                        };
                    }
                    case WEST -> {
                        Corner corner = state.getValue(CORNER);
                        return switch (corner) {
                            case FRONT_LEFT -> box(4,0,1,16,16,16);
                            case FRONT_RIGHT -> box(4,0,0,16,16,15);
                            case BACK_LEFT -> box(0,0,1,16,16,16);
                            case BACK_RIGHT -> box(0,0,0,16,16,15);
                        };
                    }
                }
            }
        }
        return super.getShape(state, level, pos, context);
    }

    void placeLayer(Level level, BlockPos pos, BlockState state, Direction facing) {

        for (TripleBlockThird third : TripleBlockThird.values()) {

            int y = third.layer - 1;
            BlockPos rightFront = pos.relative(facing.getCounterClockWise()).above(y);
            BlockPos leftBack = pos.relative(facing.getOpposite()).above(y);
            BlockPos rightBack = pos.relative(facing.getCounterClockWise()).relative(facing.getOpposite()).above(y);

            level.setBlock(rightFront, DoublePlantBlock.copyWaterloggedFrom(level, rightFront, this.defaultBlockState().setValue(THIRD, third)
                    .setValue(FACING, facing).setValue(CORNER, Corner.FRONT_RIGHT)), 3);

            level.setBlock(leftBack, DoublePlantBlock.copyWaterloggedFrom(level, leftBack, this.defaultBlockState().setValue(THIRD, third)
                    .setValue(FACING, facing).setValue(CORNER, Corner.BACK_LEFT)), 3);

            level.setBlock(rightBack, DoublePlantBlock.copyWaterloggedFrom(level, rightBack, this.defaultBlockState().setValue(THIRD, third)
                    .setValue(FACING, facing).setValue(CORNER, Corner.BACK_RIGHT)), 3);

            if (third != TripleBlockThird.LOWER) {
                BlockPos blockPos = pos.above(y);
                level.setBlock(blockPos, DoublePlantBlock.copyWaterloggedFrom(level, blockPos, this.defaultBlockState().setValue(THIRD, third)
                        .setValue(FACING, facing).setValue(CORNER, Corner.FRONT_LEFT)), 3);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(THIRD) == TripleBlockThird.MIDDLE && blockState.getValue(CORNER) == Corner.FRONT_LEFT) {
            return new ClawMachineBlockEntity(blockPos, blockState);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : BaseEntityBlock.createTickerHelper(blockEntityType,ModBlockEntityTypes.CLAW_MACHINE,ClawMachineBlockEntity::serverTick);
    }
}
