package tfar.clawmachine.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tfar.clawmachine.Constants;
import tfar.clawmachine.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile modelFile = models().getExistingFile(modLoc("block/clawmachine"));

        horizontalBlock(ModBlocks.CLAW_MACHINE,modelFile);
        //getVariantBuilder(ModBlocks.CLAW_MACHINE).forAllStates(state ->
        //                ConfiguredModel.builder().modelFile(modelFile).build());

        simpleBlockItem(ModBlocks.CLAW_MACHINE,modelFile);
    }
}
