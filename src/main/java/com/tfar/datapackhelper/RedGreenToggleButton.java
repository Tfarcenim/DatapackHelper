package com.tfar.datapackhelper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;

public class RedGreenToggleButton extends SmallButton {

  protected boolean toggled;
  public final int id;

  public RedGreenToggleButton(int x, int y, int widthIn, int heightIn, Button.IPressable callback,int id) {
    super(x, y, widthIn, heightIn,"", callback);
    this.toggled = false;
    this.id = id;
  }

  public void toggle(){
    this.toggled = !this.toggled;
  }

  @Override
  public void tint() {
    if (toggled) RenderSystem.color3f(0,1,0);
    else RenderSystem.color3f(1,0,0);
  }
}