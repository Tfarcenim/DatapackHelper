package com.tfar.datapackhelper.block;

import com.tfar.datapackhelper.container.CraftingTableDesignerContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CraftingTableDesignerBlock extends Block implements INamedContainerProvider {
  public CraftingTableDesignerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public ActionResultType func_225533_a_(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote) {
      player.openContainer(this);
    }
    return ActionResultType.SUCCESS;
}

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(getTranslationKey());
  }

  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return new CraftingTableDesignerContainer(p_createMenu_1_,p_createMenu_2_);
  }
}
