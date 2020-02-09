package com.tfar.datapackhelper;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class Keys {
  private static int index = 0;
  private static final String[] keys = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i"};

  private static Map<Pair<String,Boolean>, String> map = new HashMap<>();

  private static void next() {
    index++;
  }

  public static void clear() {
    map.clear();
    map.put(Pair.of("minecraft:air",false), " ");
    index = 0;
  }

  public static Map<Pair<String,Boolean>, String> getMap() {
    return map;
  }

  public static void put(ItemStack item) {
    boolean use_tag = item.getOrCreateChildTag(DatapackHelper.MODID).getBoolean("use_tag");
    if (use_tag){
      String name = item.getOrCreateChildTag(DatapackHelper.MODID).getString("tag");
      map.put(Pair.of(name,true),keys[index]);
    } else {
      String name = item.getItem().getRegistryName().toString();
      map.put(Pair.of(name,false), keys[index]);
    }
    next();
  }
}
