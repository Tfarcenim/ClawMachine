package tfar.clawmachine;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class HoldingButton extends Button {

    boolean down;
    public HoldingButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, b -> {}, DEFAULT_NARRATION);
    }

    public boolean isDown() {
        return down;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        down = false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        down = true;
    }
}
