package com.tfar.datapackhelper.network;

import com.tfar.datapackhelper.DatapackHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;


public class Message {

  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DatapackHelper.MODID, channelName), () -> "1.0", s -> true, s -> true);
    INSTANCE.registerMessage(0, C2SCycleTagMessage.class,
            C2SCycleTagMessage::encode,
            C2SCycleTagMessage::new,
            C2SCycleTagMessage::handle);

    INSTANCE.registerMessage(1, C2SToggleTagMessage.class,
            C2SToggleTagMessage::encode,
            C2SToggleTagMessage::new,
            C2SToggleTagMessage::handle);
  }
}