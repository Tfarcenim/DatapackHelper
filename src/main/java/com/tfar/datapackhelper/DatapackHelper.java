package com.tfar.datapackhelper;

import com.tfar.datapackhelper.block.CraftingTableDesignerBlock;
import com.tfar.datapackhelper.block.FurnaceDesignerBlock;
import com.tfar.datapackhelper.container.CraftingTableDesignerContainer;
import com.tfar.datapackhelper.container.FurnaceDesignerContainer;
import com.tfar.datapackhelper.network.Message;
import com.tfar.datapackhelper.screen.CraftingTableDesignerScreen;
import com.tfar.datapackhelper.screen.FurnaceDesignerScreen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DatapackHelper.MODID)
public class DatapackHelper {
  // Directly reference a log4j logger.

  public static final String MODID = "datapackhelper";

  private static final Logger LOGGER = LogManager.getLogger();

  public DatapackHelper() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(final FMLCommonSetupEvent event) {
    Message.registerMessages(MODID);
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(RegistryEntries.Containers.crafting_table, CraftingTableDesignerScreen::new);
    ScreenManager.registerFactory(RegistryEntries.Containers.furnace, FurnaceDesignerScreen::new);
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void blocks(final RegistryEvent.Register<Block> event) {
      // register a new block here

      Block.Properties properties = Block.Properties.create(Material.IRON).hardnessAndResistance(1, 30);
      register(new CraftingTableDesignerBlock(properties), "crafting_table", event.getRegistry());
      register(new FurnaceDesignerBlock(properties), "furnace", event.getRegistry());
    }

    @SubscribeEvent
    public static void items(final RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(65);
      register(new BlockItem(RegistryEntries.crafting_table,properties),"crafting_table",event.getRegistry());
      register(new BlockItem(RegistryEntries.furnace,properties),"furnace",event.getRegistry());
    }

    @SubscribeEvent
    public static void containers(final RegistryEvent.Register<ContainerType<?>> event) {
      register(new ContainerType<>(CraftingTableDesignerContainer::new), "crafting_table", event.getRegistry());
      register(new ContainerType<>(FurnaceDesignerContainer::new), "furnace", event.getRegistry());
    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
    }
  }

  @ObjectHolder(MODID)
  public static class RegistryEntries {
    public static final Block crafting_table = null;
    public static final Block furnace = null;
    @ObjectHolder(MODID)
    public static class Containers {
      public static final ContainerType<CraftingTableDesignerContainer> crafting_table = null;
      public static final ContainerType<FurnaceDesignerContainer> furnace = null;
//      public static final ContainerType<CraftingTableDesignerContainer> crafting_table = null;
//      public static final ContainerType<CraftingTableDesignerContainer> crafting_table_designer = null;
    }
  }
}
