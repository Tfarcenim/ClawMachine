package tfar.clawmachine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tfar.clawmachine.ClawMachineBlockEntity;
import tfar.clawmachine.ModBlocks;
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

        poseStack.pushPose();
        Vec3 clawPos = clawMachineBlockEntity.clawPos;

        double d0 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.x, clawPos.x);
        double d1 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.y, clawPos.y);
        double d2 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.z, clawPos.z);

        poseStack.translate(d0,d1,d2);

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

        this.blockRenderDispatcher
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        Services.PLATFORM.getModel(ClawMachineClient.TOP),
                        ModBlocks.CLAW_MACHINE.defaultBlockState(),
                        pos,
                        poseStack,
                        vertexconsumer,
                        false,
                        RandomSource.create(),
                        0,
                        packedOverlay
                );

        this.blockRenderDispatcher
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        clawMachineBlockEntity.clawClosed ? Services.PLATFORM.getModel(ClawMachineClient.CLOSED): Services.PLATFORM.getModel(ClawMachineClient.OPEN),
                        ModBlocks.CLAW_MACHINE.defaultBlockState(),
                        pos,
                        poseStack,
                        vertexconsumer,
                        false,
                        RandomSource.create(),
                        0,
                        packedOverlay
                );

            poseStack.popPose();
    }
}
