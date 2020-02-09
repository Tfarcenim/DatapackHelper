package com.tfar.datapackhelper.network;

import com.tfar.datapackhelper.container.CraftingTableDesignerContainer;
import com.tfar.datapackhelper.container.FurnaceDesignerContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class C2SToggleTagMessage {

  private int slotId;

  public C2SToggleTagMessage() {}

  public C2SToggleTagMessage(int slotId) {
    this.slotId = slotId;
  }

 public C2SToggleTagMessage(PacketBuffer buf) {
    slotId = buf.readInt();
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(slotId);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (ctx.get() == null || ctx.get().getSender() == null)return;
      Container anvil = ctx.get().getSender().openContainer;
      if (anvil instanceof FurnaceDesignerContainer || anvil instanceof CraftingTableDesignerContainer){
        ItemStack stack = anvil.inventorySlots.get(slotId).getStack();
        CompoundNBT nbt = stack.getOrCreateChildTag("datapackhelper");
        nbt.putBoolean("use_tag",true);
        Set<ResourceLocation> tags = stack.getItem().getTags();
        List<String> sorted = tags.stream().map(ResourceLocation::toString).sorted().collect(Collectors.toList());
        int size = sorted.size();
        if (size > 0){
          int index = nbt.getInt("index")+1;
          if (index>= size)index=0;
          nbt.putInt("index",index);
          nbt.putString("tag",sorted.get(index));
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
