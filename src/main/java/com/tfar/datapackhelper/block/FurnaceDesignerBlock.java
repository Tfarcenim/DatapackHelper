package com.tfar.datapackhelper.block;

import com.tfar.datapackhelper.container.FurnaceDesignerContainer;
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

public class FurnaceDesignerBlock extends Block implements INamedContainerProvider {
  public FurnaceDesignerBlock(Properties properties) {
    super(properties);
  }

  public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (p_225533_2_.isRemote) {
      return ActionResultType.SUCCESS;
    } else {
      player.openContainer(this);
      return ActionResultType.SUCCESS;
    }
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(getTranslationKey());
  }

  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return new FurnaceDesignerContainer(p_createMenu_1_,p_createMenu_2_);
  }
}
