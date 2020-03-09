package com.tfar.datapackhelper;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tfar.datapackhelper.network.FileFactory;
import com.tfar.datapackhelper.screen.CraftingTableDesignerScreen;
import com.tfar.datapackhelper.screen.FurnaceDesignerScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {

  public static void saveCrafting(CraftingTableDesignerScreen screen){
    try {
      ItemStackHandler handler = screen.getContainer().itemStackHandler;
      if (IntStream.range(1,10).mapToObj(handler::getStackInSlot).allMatch(ItemStack::isEmpty) || handler.getStackInSlot(0).isEmpty())return;
      Keys.clear();
      JsonObject json = new JsonObject();
      json.addProperty("type", screen.shapeless ? "minecraft:crafting_shapeless" : "minecraft:crafting_shaped");
      List<List<ItemStack>> pattern = new ArrayList<>();
      if (screen.shapeless) {

        JsonArray jsonArray = new JsonArray();

        IntStream.range(1,10)
                .mapToObj(handler::getStackInSlot)
                .filter(stack1 -> !stack1.isEmpty())
                .forEach(stack -> {
                  JsonObject jsonObject2 = new JsonObject();

                  boolean use_tag = stack.getOrCreateChildTag(DatapackHelper.MODID).getBoolean("use_tag");
                  if (use_tag)
                    jsonObject2.addProperty("tag",stack.getOrCreateChildTag(DatapackHelper.MODID).getString("tag"));
                  else
                    jsonObject2.addProperty("item",stack.getItem().getRegistryName().toString());
                  jsonArray.add(jsonObject2);
                });

        json.add("ingredients",jsonArray);
      }
      else {
        for (int y = 0; y < 3; y++) {
          ItemStack x1 = handler.getStackInSlot(1 + 3 * y);
          ItemStack x2 = handler.getStackInSlot(2 + 3 * y);
          ItemStack x3 = handler.getStackInSlot(3 + 3 * y);
          pattern.add(Lists.newArrayList(x1, x2, x3));
        }
        List<ItemStack> itemRow = pattern.get(2);
        if (isEmpty(itemRow)) {
          pattern.remove(2);
        } else {
          List<ItemStack> itemlist1 = pattern.get(0);
          if (isEmpty(itemlist1)) {
            pattern.remove(0);
          }
        }

        if (pattern.size() == 2) {
          List<ItemStack> itemlist2 = pattern.get(1);
          if (isEmpty(itemlist2)) {
            pattern.remove(1);
          } else {
            List<ItemStack> itemlist3 = pattern.get(0);
            if (isEmpty(itemlist3)) {
              pattern.remove(0);
            }
          }
        }

        final int rows = pattern.size();
        List<ItemStack> itemList = new ArrayList<>();
        for (List<ItemStack> items : pattern) {
          itemList.add(items.get(2));
        }
        if (isEmpty(itemList)) {
          for (List<ItemStack> items : pattern) {
            items.remove(2);
          }
        } else {
          itemList.clear();
          for (List<ItemStack> items : pattern) {
            itemList.add(items.get(0));
          }
          if (isEmpty(itemList)) {
            for (List<ItemStack> items : pattern) {
              items.remove(0);
            }
          }
        }

        if (pattern.get(0).size() == 2) {
          itemList.clear();
          for (int i = 0; i < rows; i++) {
            List<ItemStack> items = pattern.get(i);
            itemList.add(items.get(1));
          }
          if (isEmpty(itemList)) {
            for (List<ItemStack> items : pattern) {
              items.remove(1);
            }
          } else {
            itemList.clear();
            for (List<ItemStack> items : pattern) {
              itemList.add(items.get(0));
            }
            if (isEmpty(itemList)) {
              for (List<ItemStack> items : pattern) {
                items.remove(0);
              }
            }
          }
        }

        final int width = pattern.get(0).size();
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < width; j++) {
            Keys.put(pattern.get(i).get(j));
          }
        }

        List<String> lines = new ArrayList<>();
        for (List<ItemStack> items : pattern) {
          String line = IntStream.range(0, width).mapToObj(j -> {
            Pair<String,Boolean> pair;
            ItemStack stack = items.get(j);
            if (stack.hasTag()){
              if (stack.getOrCreateChildTag(DatapackHelper.MODID).getBoolean("use_tag")){
                String name = stack.getOrCreateChildTag(DatapackHelper.MODID).getString("tag");
                pair = Pair.of(name,true);
              }
              else {
                String name = stack.getItem().getRegistryName().toString();
                pair = Pair.of(name,false);
              }
            } else {
              String name = stack.getItem().getRegistryName().toString();
              pair = Pair.of(name,false);
            }
            return Keys.getMap().get(pair);
          }).collect(Collectors.joining());
          lines.add(line);
        }

        JsonObject jsonObject = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        lines.forEach(jsonArray::add);
        json.add("pattern", jsonArray);

        Keys.getMap().forEach((stringBooleanPair, key) -> {
          if (!key.equals(" ")) {
            JsonObject jsonObject1 = new JsonObject();
            boolean use_tag = stringBooleanPair.getRight();
            if (use_tag) {
              jsonObject1.addProperty("tag", stringBooleanPair.getLeft());
            } else
              jsonObject1.addProperty("item", stringBooleanPair.getLeft());
            jsonObject.add(key, jsonObject1);
          }
        });

        json.add("key",jsonObject);
      }


      ItemStack result = handler.getStackInSlot(0);
      JsonObject jsonObject1 = new JsonObject();
      jsonObject1.addProperty("item",result.getItem().getRegistryName().toString());
      if (result.getCount() > 1)
        jsonObject1.addProperty("count",result.getCount());

      json.add("result",jsonObject1);

      Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      String path = "datapackhelper/recipes/crafting_table";
      Path main = Paths.get(path);
      Files.createDirectories(main);
      File file = FileFactory.getNext(path);
      FileWriter writer = new FileWriter(file);

      writer.write(g.toJson(json));
      writer.flush();

      System.out.println();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void saveFurnace(FurnaceDesignerScreen screen) {

  }

  public static boolean isEmpty(List<ItemStack> items){
    return items.stream().allMatch(ItemStack::isEmpty);
  }

}
