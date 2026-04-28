package tfar.clawmachine;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static final ColorFamily<ClawMachineBlock> CLAW_MACHINES = ColorFamily.createAndRegister(BuiltInRegistries.BLOCK,
            color -> new ClawMachineBlock(BlockBehaviour.Properties.of()
                    .strength(1).noOcclusion().pushReaction(PushReaction.BLOCK)),"claw_machine");

    static {
    }

    public static void init() {

    }
}
