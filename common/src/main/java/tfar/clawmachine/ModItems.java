package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item CLAW_MACHINE = new BlockItem(ModBlocks.CLAW_MACHINE,new Item.Properties());

    static {
        Registry.register(BuiltInRegistries.ITEM,ClawMachine.id("claw_machine"),CLAW_MACHINE);
    }

    public static void init() {

    }
}
