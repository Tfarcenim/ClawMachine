package tfar.clawmachine;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tfar.clawmachine.state.properties.BProperties;
import tfar.clawmachine.state.properties.Corner;
import tfar.clawmachine.state.properties.TripleBlockThird;

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
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(THIRD,TripleBlockThird.LOWER).setValue(CORNER,Corner.FRONT_LEFT));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)
                ? this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider((containerId, playerInventory, player1) ->
                    new ClawMachineMenu(containerId,playerInventory), Component.literal("Claw Machine")));
        }
        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        TripleBlockThird doubleblockhalf = state.getValue(THIRD);

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        /*if (facing.getAxis() != Direction.Axis.Y
                || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)
                || facingState.is(this) && facingState.getValue(THIRD) != doubleblockhalf) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }*/
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
        builder.add(FACING, THIRD,CORNER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        //place other blocks
        placeLayer(level,pos,state,facing);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
        return new ClawMachineBlockEntity(blockPos,blockState);
    }
}
