package tfar.clawmachine.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.Constants;
import tfar.clawmachine.ModBlocks;
import tfar.clawmachine.ModItems;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, Constants.MOD_ID,"en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.KEY,"Key");
        ModBlocks.CLAW_MACHINES.map().forEach((color,clawMachineBlock) -> add(clawMachineBlock,
                StringUtils.capitalize(color.getName().replace('_',' '))+ " Claw Machine"));
    }
}
