package tfar.clawmachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import tfar.clawmachine.platform.Services;

public class ClawMachineBlockEntityRenderer implements BlockEntityRenderer<ClawMachineBlockEntity> {
    final BlockRenderDispatcher blockRenderDispatcher;
    public ClawMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ClawMachineBlockEntity clawMachineBlockEntity,float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Level level = clawMachineBlockEntity.getLevel();
        BlockPos pos = clawMachineBlockEntity.getBlockPos();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.cutout());


        this.blockRenderDispatcher
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        Services.PLATFORM.getModel(ClawMachineClient.CABLE),
                        ModBlocks.CLAW_MACHINE.defaultBlockState(),
                        pos,
                        poseStack,
                        vertexconsumer,
                        false,
                        RandomSource.create(),
                        0,
                        packedOverlay
                );
    }
}
