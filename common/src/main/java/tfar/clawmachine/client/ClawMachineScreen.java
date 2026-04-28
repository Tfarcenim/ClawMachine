package tfar.clawmachine.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.ClawMachineMenu;
import tfar.clawmachine.HoldingButton;

public class ClawMachineScreen extends AbstractContainerScreen<ClawMachineMenu> {
    public ClawMachineScreen(ClawMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    public static final ResourceLocation BACKGROUND = ClawMachine.id("background");

    protected HoldingButton forwardButton;
    protected HoldingButton backButton;
    protected HoldingButton leftButton;
    protected HoldingButton rightButton;

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int w = 150;
        int midX = width/2 - w/2;
        guiGraphics.blitSprite(BACKGROUND,midX,topPos+96,w,66);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics,partialTick,mouseX,mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font,"Credits: "+menu.getCredits(),100,106,0x007f00,false);

        int remaining = menu.getTimer()-menu.getProgress();

        guiGraphics.drawString(font,"Timer: "+remaining/20,100,106+36,0x7f0000,false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        int xS = 30;
        int yS = 100;

        leftButton = new HoldingButton(leftPos+xS,topPos+yS+20,18,18,Component.literal("L"));
        addRenderableWidget(leftButton);

        rightButton = new HoldingButton(leftPos+40+xS,topPos+yS+20,18,18,Component.literal("R"));
        addRenderableWidget(rightButton);

        forwardButton = new HoldingButton(leftPos+20+xS,topPos+yS,18,18,Component.literal("F"));
        addRenderableWidget(forwardButton);

        backButton = new HoldingButton(leftPos+20+xS,topPos+40+yS,18,18,Component.literal("B"));

        addRenderableWidget(backButton);

        addRenderableWidget(new Button.Builder(Component.literal("GRAB"),button -> sendButtonToServer(9))
                .bounds(leftPos+80+xS,topPos+20+yS,36,18).build());
    }

    private void sendButtonToServer(int action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action);
    }

    @Override
    //[-128,127] is possible

    protected void containerTick() {
        super.containerTick();
        boolean left = leftButton.isDown() || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT);
        boolean right = rightButton.isDown() || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT);
        boolean forward = forwardButton.isDown() || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_UP);
        boolean back = backButton.isDown() || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_DOWN);

        boolean grab = InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_KP_0);

        if (grab) {
            sendButtonToServer(9);
            return;
        }

        if (left && right) {
            left = right = false;
        }

        if (forward && back) {
            forward = back = false;
        }
        /*

        7    8    1

        6    0    2

        5    4    3

         */


        if (right && forward) {
            sendButtonToServer(1);
        } else if (right && back) {
            sendButtonToServer(3);
        } else if (right) {
            sendButtonToServer(2);
        } else if (left && forward) {
            sendButtonToServer(7);
        } else if (left && back) {
            sendButtonToServer(5);
        } else if (left) {
            sendButtonToServer(6);
        } else if (forward) {
            sendButtonToServer(8);
        } else if (back) {
            sendButtonToServer(4);
        } else {
            sendButtonToServer(0);
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
