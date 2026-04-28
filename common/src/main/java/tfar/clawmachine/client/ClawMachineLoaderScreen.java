
package tfar.clawmachine.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.ClawMachineLoaderMenu;
import tfar.clawmachine.network.server.C2SAdjustWinChancePacket;

public class ClawMachineLoaderScreen extends AbstractContainerScreen<ClawMachineLoaderMenu> {
    public ClawMachineLoaderScreen(ClawMachineLoaderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    private static final ResourceLocation CONTAINER_LOCATION = ClawMachine.id("textures/gui/claw_machine_loader.png");

    private WinChanceSlider winChanceSlider;

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        winChanceSlider = new WinChanceSlider(leftPos+120,topPos+32,100,18,Component.literal("Win"),menu.getWinChance());
        addRenderableWidget(new Button.Builder(Component.literal("Eject"),button -> sendButtonToServer(0))
                .bounds(leftPos+120,topPos+64,44,18).build());
    }

    private void sendButtonToServer(int action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action);
    }

    public class WinChanceSlider extends AbstractSliderButton {

        public WinChanceSlider(int x, int y, int width, int height, Component message, double value) {
            super(x, y, width, height, message, value);
        }

        @Override
        protected void updateMessage() {

        }

        @Override
        protected void applyValue() {
            C2SAdjustWinChancePacket.send((int) value);
        }
    }

}
