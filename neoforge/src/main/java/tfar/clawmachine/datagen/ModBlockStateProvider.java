package tfar.clawmachine.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tfar.clawmachine.ClawMachineBlock;
import tfar.clawmachine.Constants;
import tfar.clawmachine.ModBlocks;
import tfar.clawmachine.ModItems;
import tfar.clawmachine.state.properties.Corner;
import tfar.clawmachine.state.properties.TripleBlockThird;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        ModBlocks.CLAW_MACHINES.map().forEach(
                (color,clawMachineBlock) -> {
                    ResourceLocation texture = modLoc("block/"+color.getName()+"_claw_machine");

                    horizontalBlock(clawMachineBlock, state -> {
                        TripleBlockThird third = state.getValue(ClawMachineBlock.THIRD);
                        Corner corner = state.getValue(ClawMachineBlock.CORNER);

                        String s = "cm" + third.layer + corner.abr;
                        ModelFile.ExistingModelFile file =  models().getExistingFile(modLoc("block/claw_machine/" + s));


                        ModelFile file2 = models().withExistingParent(color.getName()+"_"+s,modLoc("block/claw_machine/" + s))
                                .texture("0",texture)
                                .texture("particle",texture)
                                ;

                        return file2;
                    });
                    simpleBlockItem(clawMachineBlock,models().withExistingParent(color.getName()+"_claw_machine",modLoc("item/claw_machine")
                    ).texture("0",texture).texture("particle",texture)
                    );
                });

        generatedItem(ModItems.KEY);

        //getVariantBuilder(ModBlocks.CLAW_MACHINE).forAllStates(state ->
        //                ConfiguredModel.builder().modelFile(modelFile).build());

    }

    private String name(Item item) {
        return key(item).getPath();
    }

    private void generatedItem(Item item, ResourceLocation texture) {
        String path = name(item);
        itemModels().singleTexture(path, mcLoc("item/generated"),
                "layer0", texture);
    }

    private ResourceLocation key(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private void generatedItem(Item item) {
        generatedItem(item, modLoc("item/" + name(item)));
    }

}
