package com.tfar.datapackhelper.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tfar.datapackhelper.RedGreenToggleButton;
import com.tfar.datapackhelper.Util;
import com.tfar.datapackhelper.container.CraftingTableDesignerContainer;
import com.tfar.datapackhelper.network.C2SCycleTagMessage;
import com.tfar.datapackhelper.network.C2SToggleTagMessage;
import com.tfar.datapackhelper.network.Message;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class CraftingTableDesignerScreen extends ContainerScreen<CraftingTableDesignerContainer> {

  public static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

  public boolean shapeless = false;

  public CraftingTableDesignerScreen(CraftingTableDesignerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void init() {
    super.init();
    int id = 0;
    this.addButton(new Button(guiLeft + 114, guiTop + 59, 36, 20, "Save", this::save));
    this.addButton(new RedGreenToggleButton(guiLeft + 97, guiTop + 18, 7, 7, this::toggleShape, ++id));
  }

  private void toggleShape(Button button) {
    ((RedGreenToggleButton) button).toggle();
    this.shapeless = !shapeless;
  }

  private void save(Button button) {
    Util.saveCrafting(this);
  }

  @Override
  public List<String> getTooltipFromItem(ItemStack stack) {
    List<String> tooltip = super.getTooltipFromItem(stack);
    if (stack.hasTag() && stack.getOrCreateChildTag("datapackhelper").getBoolean("use_tag")) {
      String tag = stack.getOrCreateChildTag("datapackhelper").getString("tag");
      tooltip.add(new StringTextComponent("Tag: " + tag).getFormattedText());
    }
    return tooltip;
  }

  @Override
  public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
    if (Screen.hasControlDown()) {
      Slot slot = getSlotUnderMouse();
      if (slot != null && slot.slotNumber < 10)
        Message.INSTANCE.sendToServer(new C2SCycleTagMessage(slot.slotNumber));
      return true;
    }
    if (Screen.hasAltDown()) {
      Slot slot = getSlotUnderMouse();
      if (slot != null && slot.slotNumber < 10)
        Message.INSTANCE.sendToServer(new C2SToggleTagMessage(slot.slotNumber));
      return true;
    }
    return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
  }

  /**
   * Draws the background layer of this container (behind the items).
   *
   * @param partialTicks
   * @param mouseX
   * @param mouseY
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
    blit(guiLeft, guiTop, 0, 0, xSize, ySize);
  }

  @Override
  public int getSlotColor(int index) {
    return super.getSlotColor(index);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 94, 0x404040);
    this.font.drawString("shapeless", 108, 17, 0x404040);
    this.font.drawString(this.title.getFormattedText(), 12, this.ySize - 160, 0x404040);
  }
}
