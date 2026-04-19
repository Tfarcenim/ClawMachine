package tfar.clawmachine.state.properties;

import net.minecraft.util.StringRepresentable;

public enum Corner implements StringRepresentable {
    FRONT_LEFT("fl"),FRONT_RIGHT("fr"),
    BACK_LEFT("bl"),BACK_RIGHT("br");

    public final String abr;

    Corner(String abr) {
        this.abr = abr;
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case FRONT_LEFT -> "front_left";
            case FRONT_RIGHT -> "front_right";
            case BACK_LEFT -> "back_left";
            case BACK_RIGHT -> "back_right";
        };
    }
}
