
package tfar.clawmachine.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import tfar.clawmachine.ClawMachine;
import tfar.clawmachine.ClawMachineLoaderMenu;
import tfar.clawmachine.network.server.C2SAdjustTimerChancePacket;
import tfar.clawmachine.network.server.C2SAdjustWinChancePacket;

public class ClawMachineLoaderScreen extends AbstractContainerScreen<ClawMachineLoaderMenu> {
    public ClawMachineLoaderScreen(ClawMachineLoaderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight+=19;
        inventoryLabelY+=19;

        menu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {

            }

            @Override
            public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
                switch (dataSlotIndex) {
                    case 1 -> {
                        winChanceSlider.setValueNoUpdate(value/100d);
                    }
                    case 2 -> {
                        timerSlider.setValue(value/20d);
                    }
                }
            }
        });
    }
    private static final ResourceLocation CONTAINER_LOCATION = ClawMachine.id("textures/gui/claw_machine_loader.png");

    private WinChanceSlider winChanceSlider;
    private ExtendedSlider timerSlider;

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
        winChanceSlider = new WinChanceSlider(leftPos + 71, topPos + 6, 100, 18, Component.literal("Win: 0%"), 0);
        addRenderableWidget(winChanceSlider);

        timerSlider = new TimerSlider(leftPos + 71, topPos + 27, 100, 18,Component.literal("Timer: "),Component.literal(" seconds"),
                5,60,60,.5,0,true);
        addRenderableWidget(timerSlider);

        addRenderableWidget(new Button.Builder(Component.literal("Eject"),button -> sendButtonToServer(0))
                .bounds(leftPos+120,topPos+80,44,18).build());
    }

    private void sendButtonToServer(int action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.repositionElements();
    }

    public static class WinChanceSlider extends AbstractSliderButton {

        public WinChanceSlider(int x, int y, int width, int height, Component message, double value) {
            super(x, y, width, height, message, value);
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Win: "+(int)(value*100)).append("%"));
        }

        public void setValueNoUpdate(double value) {
            this.value = Mth.clamp(value, 0.0, 1.0);
            this.updateMessage();
        }

        @Override
        protected void applyValue() {
            C2SAdjustWinChancePacket.send((int) (value*100));
        }
    }

    public static class TimerSlider extends ExtendedSlider{

        public TimerSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue,
                           double stepSize, int precision, boolean drawString) {
            super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
        }

        @Override
        protected void applyValue() {
            super.applyValue();
            C2SAdjustTimerChancePacket.send((int) (getValue()*20));
        }
    }
}
