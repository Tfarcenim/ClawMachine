package tfar.clawmachine.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
        this.random.setSeed(ItemEntityRenderer.getSeedForItemStack(itemstack));
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, entity.level(), null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        float f = 0.25F;
        float f1 = 0;
        float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0F, f1 + 0.25F * f2*0, 0.0F);
        float f3 = 0;//entity.getSpin(partialTicks);
        poseStack.mulPose(Axis.YP.rotation(f3));
        ItemEntityRenderer.renderMultipleFromCount(this.itemRenderer, poseStack, buffer, packedLight, itemstack, bakedmodel, flag, this.random);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
