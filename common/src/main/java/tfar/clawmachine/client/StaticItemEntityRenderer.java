package tfar.clawmachine.client;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.clawmachine.StaticItemEntity;

public class StaticItemEntityRenderer extends EntityRenderer<StaticItemEntity> {
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    protected StaticItemEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public ResourceLocation getTextureLocation(StaticItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }


    @Override
    public void render(StaticItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        ItemStack itemstack = entity.getItem();
        this.random.setSeed(getSeedForItemStack(itemstack));
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, entity.level(), null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        float f = 0.25F;
        float f1 = 0;
        float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0F, f1 + 0.25F * f2*0, 0.0F);
        float f3 = 0;//entity.getSpin(partialTicks);
        poseStack.mulPose(Axis.YP.rotation(f3));
        renderMultipleFromCount(this.itemRenderer, poseStack, buffer, packedLight, itemstack, bakedmodel, flag, this.random);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public static int getSeedForItemStack(ItemStack stack) {
        return stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
    }

    static int getRenderedAmount(int count) {
        if (count <= 1) {
            return 1;
        } else if (count <= 16) {
            return 2;
        } else if (count <= 32) {
            return 3;
        } else {
            return count <= 48 ? 4 : 5;
        }
    }

    public static void renderMultipleFromCount(
            ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack item, RandomSource random, Level level
    ) {
        BakedModel bakedmodel = itemRenderer.getModel(item, level, null, 0);
        renderMultipleFromCount(itemRenderer, poseStack, buffer, packedLight, item, bakedmodel, bakedmodel.isGui3d(), random);
    }

    public static void renderMultipleFromCount(
            ItemRenderer itemRenderer,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ItemStack item,
            BakedModel model,
            boolean isGui3d,
            RandomSource random
    ) {
        int i = getRenderedAmount(item.getCount());
        float f = model.getTransforms().ground.scale.x();
        float f1 = model.getTransforms().ground.scale.y();
        float f2 = model.getTransforms().ground.scale.z();
        if (!isGui3d) {
            float f3 = -0.0F * (float)(i - 1) * 0.5F * f;
            float f4 = -0.0F * (float)(i - 1) * 0.5F * f1;
            float f5 = -0.09375F * (float)(i - 1) * 0.5F * f2;
            poseStack.translate(f3, f4, f5);
        }

        boolean shouldSpread = true;//net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(item).shouldSpreadAsEntity(item);
        for (int j = 0; j < i; j++) {
            poseStack.pushPose();
            if (j > 0 && shouldSpread) {
                if (isGui3d) {
                    float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(f7, f9, f6);
                } else {
                    float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(f8, f10, 0.0F);
                }
            }

            itemRenderer.render(item, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
            poseStack.popPose();
            if (!isGui3d) {
                poseStack.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
            }
        }
    }
}
