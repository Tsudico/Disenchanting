package io.github.tsudico.disenchanting.client;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.tsudico.disenchanting.Disenchanting;
import io.github.tsudico.disenchanting.common.container.DisenchantTableContainer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisenchantingClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        //Registers a gui factory that opens our example gui, this reads the block pos from the buffer
        ScreenProviderRegistry.INSTANCE.registerFactory(Disenchanting.DISENCHANT_CONTAINER, DisenchantTableContainerScreen::new);

        LOGGER.info("Initializing Disenchanting Client");
    }

    public static class DisenchantTableContainerScreen extends ContainerScreen<DisenchantTableContainer> {
        private int guiLeft;
        private int guiTop;
        private static final Identifier BG_TEXTURE = new Identifier("disenchanting:textures/gui/container/disenchant_table.png");
        private Shutter shutter;
        private float delta = Float.MIN_VALUE;
        private float lastdelta = Float.MAX_VALUE;
        private int start = 67;
        private int end = 79;
        private int offset = 0;

        public DisenchantTableContainerScreen(DisenchantTableContainer container) {
            super(container, container.playerInventory, new TextComponent("Disenchant Table"));
        }

        @Override
        public void init() {
            super.init();
            guiLeft = (width - containerWidth) / 2;
            guiTop = (height - containerHeight) / 2;
            shutter = Shutter.CLOSED;
        }

        @Override
        public void render(int var1, int var2, float var3) {
            delta = var3;
            if(lastdelta > delta) {
                lastdelta = delta;
            }
            renderBackground();
            super.render(var1, var2, var3);
            drawMouseoverTooltip(var1, var2);
        }


        @Override
        public void drawForeground(int int_1, int int_2) {
            float left = 49.0F;
            float top = 69.0F;
            if( container.getLevelCost() > 0) {
                if(Shutter.CLOSED != shutter) {
                    TranslatableComponent cost = new TranslatableComponent("container.repair.cost", container.getLevelCost());
                    if (container.getLevelCost() <= playerInventory.player.experienceLevel || playerInventory.player.isCreative()) {
                        font.draw(cost.getText(), left, top, 0x00FF00);
                    } else {
                        font.draw(cost.getText(), left, top, 0xFF0000);
                    }
                }
                if(Shutter.CLOSED == shutter || Shutter.CLOSING == shutter) {
                    shutter = Shutter.OPENING;
                }
            } else if(Shutter.OPEN == shutter || Shutter.OPENING == shutter) {
                shutter = Shutter.CLOSING;
            }
            if(Shutter.OPEN != shutter) {
                switch(shutter) {
                    case CLOSED:
                        offset = 0;
                        break;
                    case OPENING:
                        if(++offset >= end - start) {
                            shutter = Shutter.OPEN;
                            offset = end - start;
                        }
                        break;
                    case CLOSING:
                        if(--offset <= 0) {
                            shutter = Shutter.CLOSED;
                            offset = 0;
                        }
                        break;
                }
                fill(47, start, 169, (end - offset), 0xFFc6c6c6);
            }
        }

        @Override
        protected void drawBackground(float v, int i, int i1) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(BG_TEXTURE);
            blit(guiLeft, guiTop, 0, 0, containerWidth, containerHeight);
        }

        private enum Shutter {
            CLOSED,
            OPENING,
            OPEN,
            CLOSING
        }
    }
}
