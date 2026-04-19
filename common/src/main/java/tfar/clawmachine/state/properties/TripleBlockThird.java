package tfar.clawmachine.state.properties;

import net.minecraft.util.StringRepresentable;

public enum TripleBlockThird implements StringRepresentable {
    UPPER(3),MIDDLE(2),LOWER(1);

    public final int layer;

    TripleBlockThird(int i) {
        this.layer = i;
    }

    @Override
    public String getSerializedName() {
        return switch (this){
            case UPPER -> "upper";
            case MIDDLE -> "middle";
            case LOWER -> "lower";
        };
    }
}
