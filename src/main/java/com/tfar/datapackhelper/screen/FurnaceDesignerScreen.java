package com.tfar.datapackhelper.screen;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tfar.datapackhelper.ToggleImageButton;
import com.tfar.datapackhelper.container.FurnaceDesignerContainer;
import com.tfar.datapackhelper.DatapackHelper;
import com.tfar.datapackhelper.network.C2SCycleTagMessage;
import com.tfar.datapackhelper.network.C2SToggleTagMessage;
import com.tfar.datapackhelper.network.FileFactory;
import com.tfar.datapackhelper.network.Message;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.core.util.Integers;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class FurnaceDesignerScreen extends ContainerScreen<FurnaceDesignerContainer> {

  private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation(DatapackHelper.MODID, "textures/container/furnace.png");

  protected TextFieldWidget cooktime;
  protected TextFieldWidget experience;
  public ToggleImageButton.Type type = ToggleImageButton.Type.smelting;


  public FurnaceDesignerScreen(FurnaceDesignerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.cooktime.render(mouseX, mouseY, partialTicks);
    this.experience.render(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void init() {
    super.init();
    int id = 0;
    this.minecraft.keyboardListener.enableRepeatEvents(true);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.cooktime = new TextFieldWidget(this.font, i + 70, j + 50, 60, 12, I18n.format("container.repair"));
    this.cooktime.setCanLoseFocus(true);
    //this.cooktime.changeFocus(true);
    this.cooktime.setTextColor(-1);
    this.cooktime.setDisabledTextColour(0x808080);
    this.cooktime.setEnableBackgroundDrawing(false);
    this.cooktime.setMaxStringLength(10);
    this.cooktime.setResponder(this::onTimeEdited);

    this.experience = new TextFieldWidget(this.font, i + 70, j + 62, 60, 12, I18n.format("container.repair"));
    this.experience.setCanLoseFocus(true);
    //this.experience.changeFocus(true);
    this.experience.setTextColor(-1);
    this.experience.setDisabledTextColour(0x808080);
    this.experience.setEnableBackgroundDrawing(false);
    this.experience.setMaxStringLength(10);
    this.experience.setResponder(this::onXpEdited);

    cooktime.setText("200");
    experience.setText("0");

    this.children.add(this.cooktime);
    this.children.add(this.experience);
    this.setFocusedDefault(this.experience);
    this.setFocusedDefault(this.cooktime);

    this.addButton(new Button(guiLeft + 132, guiTop + 59, 36, 20, "Save", this::save));

    this.addButton(new ToggleImageButton(guiLeft + 28, guiTop + 22, 20, 20, this::cycleFurnace, type));
  }

  private void cycleFurnace(Button button) {
    ((ToggleImageButton) button).toggle();
    int ordinal = type.ordinal();
    ordinal++;
    if (ordinal > 2) ordinal = 0;
    type = ToggleImageButton.Type.values()[ordinal];
  }

  private void onTimeEdited(String s) {
    int color = Ints.tryParse(s) != null ? -1 : 0xff0000;
    cooktime.setTextColor(color);
  }

  private void onXpEdited(String s) {
    int color = Doubles.tryParse(s) != null ? -1 : 0xff0000;
    experience.setTextColor(color);
  }

  private void save(Button button) {
    ItemStackHandler handler = container.itemStackHandler;
    ItemStack input = handler.getStackInSlot(1);
    ItemStack result = handler.getStackInSlot(0);
    if (result.isEmpty() || input.isEmpty()) return;
    JsonObject json = new JsonObject();
    json.addProperty("type", "minecraft:" + type.toString());
    try {
      JsonObject ingredient = new JsonObject();

      boolean use_tag = input.getOrCreateChildTag(DatapackHelper.MODID).getBoolean("use_tag");
      if (use_tag)
        ingredient.addProperty("tag", input.getOrCreateChildTag(DatapackHelper.MODID).getString("tag"));
      else
        ingredient.addProperty("item", input.getItem().getRegistryName().toString());

      json.add("ingredient", ingredient);

      json.addProperty("result", result.getItem().getRegistryName().toString());
      json.addProperty("experience", Double.parseDouble(this.experience.getText()));
      json.addProperty("cookingtime", Integer.parseInt(this.cooktime.getText()));

      Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      String path = "datapackhelper/recipes/" + type.toString();
      Path main = Paths.get(path);
      Files.createDirectories(main);
      File file = FileFactory.getNext(path);
      FileWriter writer = new FileWriter(file);

      writer.write(g.toJson(json));
      writer.flush();

      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  @Nonnull
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

  @Override
  public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
    if (keyCode == GLFW_KEY_ESCAPE) {
      this.minecraft.player.closeScreen();
    }
    return this.cooktime.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_) || this.cooktime.canWrite()
            || this.experience.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_) || this.experience.canWrite()

            || super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
  }

  public void removed() {
    super.removed();
    this.minecraft.keyboardListener.enableRepeatEvents(false);
  }

  public boolean isEmpty(List<ItemStack> items) {
    return items.stream().allMatch(ItemStack::isEmpty);
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
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    int x = 67;
    int y = 48;
    minecraft.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
    blit(guiLeft, guiTop, 0, 0, xSize, ySize);

    fill(i + x, j + y, i + 57 + x, j + 11 + y, 0xff000000);
    y += 12;
    fill(i + x, j + y, i + 57 + x, j + 11 + y, 0xff000000);
  }

  @Override
  public int getSlotColor(int index) {
    return super.getSlotColor(index);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 94, 0x404040);
    this.font.drawString("Cook time: ", 8, this.ySize - 115, 0x404040);
    this.font.drawString("Experience: ", 8, this.ySize - 105, 0x404040);
    this.font.drawString(this.title.getFormattedText(), 23, this.ySize - 160, 0x404040);
  }
}
