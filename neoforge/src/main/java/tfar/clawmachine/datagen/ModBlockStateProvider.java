package tfar.clawmachine.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tfar.clawmachine.ClawMachineBlock;
import tfar.clawmachine.Constants;
import tfar.clawmachine.ModBlocks;
import tfar.clawmachine.state.properties.Corner;
import tfar.clawmachine.state.properties.TripleBlockThird;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.CLAW_MACHINE,state -> {

            TripleBlockThird third = state.getValue(ClawMachineBlock.THIRD);
            Corner corner = state.getValue(ClawMachineBlock.CORNER);

            String s = "cm"+third.layer+corner.abr;

            return models().getExistingFile(modLoc("block/claw_machine/"+s));
        });
        //getVariantBuilder(ModBlocks.CLAW_MACHINE).forAllStates(state ->
        //                ConfiguredModel.builder().modelFile(modelFile).build());

        simpleBlockItem(ModBlocks.CLAW_MACHINE,models().getExistingFile(modLoc("block/clawmachine1")));
    }
}
