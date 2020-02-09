package com.tfar.datapackhelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

public class ToggleImageButton extends Button {

  public Type type;

  public ToggleImageButton(int xpos, int ypos, int width, int height, IPressable onPress, Type type) {
    super(xpos, ypos, width, height, "", onPress);
    this.type = type;
  }

  public void toggle(){
    int ordinal = type.ordinal();
    ordinal++;
    if (ordinal > 2)ordinal = 0;
    type = Type.values()[ordinal];
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_, p_render_2_, p_render_3_);
    drawItemStack(type.getIcon(), x+2, y+2);
  }

  /**
   * Draws an ItemStack.
   *
   * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
   */
  private void drawItemStack(ItemStack stack, int x, int y) {

    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    RenderSystem.translatef(0.0F, 0.0F, 32.0F);
    this.setBlitOffset(200);
    itemRenderer.zLevel = 200.0F;
    itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
    this.setBlitOffset(0);
    itemRenderer.zLevel = 0.0F;
  }

  public enum Type {
    smelting, blasting, smoking;

    public ItemStack getIcon(){
      switch (this){
        default:
        case smelting:return new ItemStack(Blocks.FURNACE);
        case smoking:return new ItemStack(Blocks.SMOKER);
        case blasting:return new ItemStack(Blocks.BLAST_FURNACE);
      }
    }
  }
}
