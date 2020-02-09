package com.tfar.datapackhelper.container;

import com.tfar.datapackhelper.DatapackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FurnaceDesignerContainer extends Container {

  public final ItemStackHandler itemStackHandler = new ItemStackHandler(2);

  public FurnaceDesignerContainer(int windowId, PlayerInventory inv) {
    super(DatapackHelper.RegistryEntries.Containers.furnace,windowId);

    // crafting result
    addSlot(new SlotItemHandler(itemStackHandler,0,116, 25));
    //cooktime
    addSlot(new SlotItemHandler(itemStackHandler, 1, 56, 25));
    // inventory
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(inv, 9 + x + 9 * y, 8 + 18 * x, 84 + 18 * y));
      }
    }

    // hotbar
    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(inv, x, 8 + 18 * x, 142));
    }
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index < 2) {
        if (!this.mergeItemStack(itemstack1, 2, inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(itemstack1, itemstack);
      } else {
        if (!this.mergeItemStack(itemstack1, 1, 2, false) && !this.mergeItemStack(itemstack1, 0, 1, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(playerIn, itemstack1);
    }

    return itemstack;
  }

  /**
   * Determines whether supplied player can use this container
   *
   * @param playerIn
   */
  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
