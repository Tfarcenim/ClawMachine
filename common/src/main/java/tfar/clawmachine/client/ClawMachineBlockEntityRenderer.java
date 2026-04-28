package tfar.clawmachine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tfar.clawmachine.ClawMachineBlockEntity;
import tfar.clawmachine.ModBlocks;
import tfar.clawmachine.platform.Services;

public class ClawMachineBlockEntityRenderer implements BlockEntityRenderer<ClawMachineBlockEntity> {
    final BlockRenderDispatcher blockRenderDispatcher;
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public ClawMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ClawMachineBlockEntity clawMachineBlockEntity,float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = clawMachineBlockEntity.getLevel();
        BlockPos pos = clawMachineBlockEntity.getBlockPos();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.cutout());

        Vec3 clawPos = clawMachineBlockEntity.clawPos;

        double d0 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.x, clawPos.x);
        double d1 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.y, clawPos.y);
        double d2 = Mth.lerp(partialTick, clawMachineBlockEntity.prevClawPos.z, clawPos.z);

        poseStack.pushPose();

        poseStack.translate(d0,0,d2);

        BlockState dummy = Blocks.STONE.defaultBlockState();//I don't think this matters does it?


                //not affected by y translations
        this.blockRenderDispatcher
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        Services.PLATFORM.getModel(ClawMachineClient.TOP),
                        dummy,
                        pos,
                        poseStack,
                        vertexconsumer,
                        false,
                        RandomSource.create(),
                        0,
                        packedOverlay
                );

        poseStack.pushPose();
        poseStack.translate(0,d1,0);

        this.blockRenderDispatcher
                .getModelRenderer()
                .tesselateBlock(
                        level,
                        clawMachineBlockEntity.clawClosed ? Services.PLATFORM.getModel(ClawMachineClient.CLOSED): Services.PLATFORM.getModel(ClawMachineClient.OPEN),
                        dummy,
                        pos,
                        poseStack,
                        vertexconsumer,
                        false,
                        RandomSource.create(),
                        0,
                        packedOverlay
                );


        double c = 1 + 1/16d;
        //simulate cable stretching
        float scale = (float) (1 - d1/c);
        poseStack.scale(1,scale ,1);//1 at 0 and 0 at 1 1/16
        this.blockRenderDispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                            level,
                            Services.PLATFORM.getModel(ClawMachineClient.CABLE),
                            dummy,
                            pos,
                            poseStack,
                            vertexconsumer,
                            false,
                            RandomSource.create(),
                            0,
                            packedOverlay
                    );

        poseStack.popPose();
        poseStack.popPose();


        ItemStack itemstack = clawMachineBlockEntity.grabbedItem;
        if (!itemstack.isEmpty()) {
            poseStack.pushPose();

            this.random.setSeed(ItemEntityRenderer.getSeedForItemStack(itemstack));
            BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, level, null, 0);
            boolean flag = bakedmodel.isGui3d();
            float f = 0.25F;
            float f1 = 0;
            float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
            poseStack.translate(0.0F, -1/8d, 0.0F);
            poseStack.translate(d0+.5,d1,d2+.5);
            float f3 = 0;//entity.getSpin(partialTicks);
            poseStack.mulPose(Axis.YP.rotation(f3));
            ItemEntityRenderer.renderMultipleFromCount(this.itemRenderer, poseStack, bufferSource, packedLight, itemstack, bakedmodel, flag, this.random);


            poseStack.popPose();
        }


        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            drawRenderBoundingBox(poseStack, consumer, clawMachineBlockEntity);
        }

    }

    private void drawRenderBoundingBox(PoseStack poseStack, VertexConsumer consumer, ClawMachineBlockEntity be) {
        AABB aabb = be.getClawHitbox();
        BlockPos pos = be.getBlockPos();

        poseStack.pushPose();
        poseStack.translate(-pos.getX(),-pos.getY(),-pos.getZ());
        LevelRenderer.renderLineBox(poseStack, consumer, aabb, 1F, 0F, 0F, 1F);
        poseStack.popPose();

        poseStack.pushPose();
        AABB aabb1 = be.winBounds;
        poseStack.translate(-pos.getX(),-pos.getY(),-pos.getZ());
        LevelRenderer.renderLineBox(poseStack, consumer,aabb1 , 0F, 1F, 0F, 1F);
        poseStack.popPose();

        poseStack.pushPose();
        AABB aabb2 = be.clawBounds;
        poseStack.translate(.5,0,.5);
        LevelRenderer.renderLineBox(poseStack, consumer,aabb2 , 1F, 1F, 0F, 1F);
        poseStack.popPose();
    }

}
