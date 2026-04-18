package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class ModBlockEntityTypes {
    public static final BlockEntityType<ClawMachineBlockEntity> CLAW_MACHINE = new BlockEntityType<>(ClawMachineBlockEntity::new, Set.of(ModBlocks.CLAW_MACHINE),null);

    static {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,ClawMachine.id("claw_machine"),CLAW_MACHINE);
    }

    public static void init() {

    }

}
