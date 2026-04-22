package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static final Block CLAW_MACHINE = new ClawMachineBlock(BlockBehaviour.Properties.of().noOcclusion().pushReaction(PushReaction.BLOCK));

    static {
        Registry.register(BuiltInRegistries.BLOCK,ClawMachine.id("claw_machine"),CLAW_MACHINE);
    }

    public static void init() {

    }
}
