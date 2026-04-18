package tfar.clawmachine;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ClawMachineScreen extends AbstractContainerScreen<ClawMachineMenu> {
    public ClawMachineScreen(ClawMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new Button.Builder(Component.literal("LEFT"),button -> sendButtonToServer(ClawMachineMenu.Controls.LEFT))
                .bounds(leftPos,topPos+120,18,18).build());

        addRenderableWidget(new Button.Builder(Component.literal("RIGHT"),button -> sendButtonToServer(ClawMachineMenu.Controls.RIGHT))
                .bounds(leftPos+40,topPos+120,18,18).build());

        addRenderableWidget(new Button.Builder(Component.literal("FORWARD"),button -> sendButtonToServer(ClawMachineMenu.Controls.FORWARD))
                .bounds(leftPos+20,topPos+100,18,18).build());

        addRenderableWidget(new Button.Builder(Component.literal("BACK"),button -> sendButtonToServer(ClawMachineMenu.Controls.BACK))
                .bounds(leftPos+20,topPos+140,18,18).build());

        addRenderableWidget(new Button.Builder(Component.literal("GRAB"),button -> sendButtonToServer(ClawMachineMenu.Controls.GRAB))
                .bounds(leftPos+80,topPos+120,18,18).build());
    }

    private void sendButtonToServer(ClawMachineMenu.Controls action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action.ordinal());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
