package com.tfar.datapackhelper.container;

import com.tfar.datapackhelper.DatapackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CraftingTableDesignerContainer extends Container {

  public final ItemStackHandler itemStackHandler = new ItemStackHandler(10);

  public CraftingTableDesignerContainer(int windowId, PlayerInventory inv) {
    super(DatapackHelper.RegistryEntries.Containers.crafting_table,windowId);

    // crafting result
    this.addSlot(new SlotItemHandler(itemStackHandler,0,124, 35));

    // crafting grid
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        addSlot(new SlotItemHandler(itemStackHandler, 1 + x + 3 * y, 30 + 18 * x, 17 + 18 * y));
      }
    }

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
      if (index < 10) {
        if (!this.mergeItemStack(itemstack1, 10, inventorySlots.size(), true)) {
          return ItemStack.EMPTY;
        }
        slot.onSlotChange(itemstack1, itemstack);
      } else {
        if (!this.mergeItemStack(itemstack1, 1, 10, false) && !this.mergeItemStack(itemstack1, 0, 1, false)) {
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
