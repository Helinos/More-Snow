package net.helinos.moresnow.gui;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.Scissor;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.lang.I18n;

@Environment(EnvType.CLIENT)
public class ScreenModOptions extends Screen {
    private boolean doScroll;
    private double scrollAmount;
    private final int TOP_SPACING = 24;
    private final int PADDING = 8;
    private int bottomSpacing;
    private int scrollRegionHeight;
    
    public ScreenModOptions(Screen parent) {
        super(parent);
        this.scrollAmount = 0.0;
        this.doScroll = true;
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new ButtonElement(0, this.width / 2 - 100, this.height - 24, 200, 20, I18n.getInstance().translateKey("gui.options.button.done")));
        MoreSnow.MOD_OPTIONS.initComponents(this.mc);
        this.bottomSpacing = this.height - 28;
        this.scrollRegionHeight = bottomSpacing - TOP_SPACING;
    }

    @Override
    public void render(int mouseX, int mouseY, float renderPartialTicks) {
        if (this.doScroll) {
            if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                this.scroll(Mouse.getDWheel() / -0.05);
            } else {
                this.scroll(Mouse.getDWheel() / -0.01);
            }

            this.onScroll();
        }

        this.renderBackground();
        this.overlayBackground(0, this.width, 0, TOP_SPACING);
        this.overlayBackground(0, this.width, bottomSpacing, this.height);

        Scissor.enable(0, TOP_SPACING, this.width, bottomSpacing - TOP_SPACING);
        if (mouseY >= TOP_SPACING && mouseY <= bottomSpacing) {
            this.drawItems(PADDING, TOP_SPACING - (int) this.scrollAmount, this.width - PADDING, mouseX, mouseY);
        } else {
            this.drawItems(PADDING, TOP_SPACING - (int) this.scrollAmount, this.width - PADDING, -1, -1);
        }
        Scissor.disable();

        if (this.mc.currentWorld == null) {
            final int SHADOW_SIZE = 4;
            Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3008);
            GL11.glShadeModel(7425);
            GL11.glDisable(3553);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 0);
            tessellator.addVertexWithUV(0.0, TOP_SPACING + SHADOW_SIZE, 0.0, 0.0, 1.0);
            tessellator.addVertexWithUV(this.width, TOP_SPACING + SHADOW_SIZE, 0.0, 1.0, 1.0);
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV(this.width, TOP_SPACING, 0.0, 1.0, 0.0);
            tessellator.addVertexWithUV(0.0, TOP_SPACING, 0.0, 0.0, 0.0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV(0.0, bottomSpacing, 0.0, 0.0, 1.0);
            tessellator.addVertexWithUV(this.width, bottomSpacing, 0.0, 1.0, 1.0);
            tessellator.setColorRGBA_I(0, 0);
            tessellator.addVertexWithUV(this.width, bottomSpacing - SHADOW_SIZE, 0.0, 1.0, 0.0);
            tessellator.addVertexWithUV(0.0, bottomSpacing - SHADOW_SIZE, 0.0, 0.0, 0.0);
            tessellator.draw();
            GL11.glEnable(3553);
            GL11.glShadeModel(7424);
            GL11.glEnable(3008);
            GL11.glDisable(3042);
        }

        I18n i18n = I18n.getInstance();
        this.drawStringCentered(this.font, i18n.translateKey("gui.moresnow.options.title"), this.width / 2, 5, 0xFFFFFF);
        super.render(mouseX, mouseY, renderPartialTicks);
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        if (this.mc.currentWorld == null) {
           Tessellator tessellator = Tessellator.instance;
           this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/background.png").bind();
           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
           float scale = 32.0F;
           tessellator.startDrawingQuads();
           tessellator.setColorOpaque_I(0x202020);
           tessellator.addVertexWithUV(0, bottomSpacing, 0, 0, (bottomSpacing + this.scrollAmount) / scale);
           tessellator.addVertexWithUV(this.width, bottomSpacing, 0, this.width / scale, (bottomSpacing + this.scrollAmount) / scale);
           tessellator.addVertexWithUV(this.width, TOP_SPACING, 0, this.width / scale, (TOP_SPACING + this.scrollAmount) / scale);
           tessellator.addVertexWithUV(0, TOP_SPACING, 0, 0, (TOP_SPACING + this.scrollAmount) / scale);
           tessellator.draw();
        } else {
           this.drawRect(0, 0, this.width, TOP_SPACING, 0x5F000000);
           this.drawRect(0, bottomSpacing, this.width, this.height, 0x5F000000);
        }
    }

    private void scroll(double amount) {
        if (amount != 0.0) {
            this.scrollAmount += amount;
            this.onScroll();
        }
    }

    private void onScroll() {
        int totalPageHeight = this.getTotalPageHeight();
        if (!(this.scrollAmount < 0.0) && scrollRegionHeight <= totalPageHeight) {
           if (this.scrollAmount > (totalPageHeight - scrollRegionHeight)) {
              this.scrollAmount = (totalPageHeight - scrollRegionHeight);
           }
        } else {
           this.scrollAmount = 0.0F;
        }
    }

    private int getTotalPageHeight() {
        int totalPageHeight = 0;

        OptionsComponent component;
        for(Iterator<?> var2 = MoreSnow.MOD_OPTIONS.getComponents().iterator(); var2.hasNext(); totalPageHeight += component.getHeight()) {
            component = (OptionsComponent)var2.next();
        }

        return totalPageHeight;
    }


    private void overlayBackground(int minX, int maxX, int minY, int maxY) {
        if (this.mc.currentWorld == null) {
            Tessellator tessellator = Tessellator.instance;
            this.mc.textureManager.loadTexture("/assets/minecraft/textures/gui/background.png").bind();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            final double SCALE = 32.0;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(0x404040);
            tessellator.addVertexWithUV(minX, maxY, 0.0, minX / SCALE, maxY / SCALE);
            tessellator.addVertexWithUV(maxX, maxY, 0.0, maxX / SCALE, maxY / SCALE);
            tessellator.addVertexWithUV(maxX, minY, 0.0, maxX / SCALE, minY / SCALE);
            tessellator.addVertexWithUV(minX, minY, 0.0, minX / SCALE, minY / SCALE);
            tessellator.draw();
      }
   }

    private void drawItems(int x, int y, int width, int mouseX, int mouseY) {
        OptionsComponent component;
        for(Iterator<OptionsComponent> item = MoreSnow.MOD_OPTIONS.getComponents().iterator(); item.hasNext(); y += component.getHeight()) {
            component = item.next();
            component.render(x, y, width, mouseX - x, mouseY - y);
        }
    }
}
