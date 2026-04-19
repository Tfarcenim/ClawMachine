package tfar.clawmachine.state.properties;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BProperties {
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    public static final EnumProperty<TripleBlockThird> TRIPLE_BLOCK_THIRD = EnumProperty.create("third", TripleBlockThird.class);
}
