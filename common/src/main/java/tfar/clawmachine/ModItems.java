package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item KEY = new Item(new Item.Properties());
    public static final ColorFamily<BlockItem> CLAW_MACHINES = ColorFamily.createAndRegister(BuiltInRegistries.ITEM
            ,color -> new BlockItem(ModBlocks.CLAW_MACHINES.getEntry(color),new Item.Properties()), "claw_machine");

    static {
        Registry.register(BuiltInRegistries.ITEM,ClawMachine.id("key"),KEY);
    }

    public static void init() {

    }
}
