package com.tfar.datapackhelper.network;

import com.tfar.datapackhelper.container.CraftingTableDesignerContainer;
import com.tfar.datapackhelper.container.FurnaceDesignerContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SCycleTagMessage {

  private int slotId;

  public C2SCycleTagMessage() {}

  public C2SCycleTagMessage(int slotId) {
    this.slotId = slotId;
  }

 public C2SCycleTagMessage(PacketBuffer buf) {
    slotId = buf.readInt();
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(slotId);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (ctx.get() == null || ctx.get().getSender() == null)return;
      Container anvil = ctx.get().getSender().openContainer;
      if (anvil instanceof CraftingTableDesignerContainer || anvil instanceof FurnaceDesignerContainer){
        ItemStack stack = anvil.inventorySlots.get(slotId).getStack();
        CompoundNBT nbt = stack.getOrCreateChildTag("datapackhelper");
        nbt.putBoolean("use_tag",!nbt.getBoolean("use_tag"));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
