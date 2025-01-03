package com.koralix.oneforall.mixin.features.carefulbreak;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.settings.PlayerSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.Block.getDroppedStacks;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void dropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
        if (world instanceof ServerWorld && entity instanceof PlayerEntity player) {
            if (PlayerSettings.CAREFUL_BREAK.value(player.getUuid()).isActive(player)) {
                getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, entity, stack).forEach((itemStack) -> {
                    Item item = itemStack.getItem();
                    int itemAmount = itemStack.getCount();
                    if (((PlayerEntity) entity).getInventory().insertStack(itemStack)) {
                        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (OneForAll.rng().nextFloat() - OneForAll.rng().nextFloat()) * 1.4F + 2.0F);
                        ((PlayerEntity) entity).increaseStat(Stats.PICKED_UP.getOrCreateStat(item), itemAmount);
                    } else {
                        Block.dropStack(world, pos, itemStack);
                    }
                });
                state.onStacksDropped((ServerWorld) world, pos, stack, true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "onBreak", at = @At("HEAD"))
    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (PlayerSettings.CAREFUL_BREAK.value(player.getUuid()).isActive(player)) {
            if (Blocks.PISTON_HEAD.equals(state.getBlock()))
                fixMultiBlock(state.get(FacingBlock.FACING).getOpposite(), pos, world, player, PistonBlock.class);
            else if (state.getBlock() instanceof BedBlock && state.get(BedBlock.PART).equals(BedPart.FOOT))
                fixMultiBlock(state.get(HorizontalFacingBlock.FACING), pos, world, player, BedBlock.class);
            else if ((state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TallPlantBlock) && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
                fixMultiBlock(Direction.DOWN, pos, world, player, DoorBlock.class, TallPlantBlock.class);
        }
    }

    @Unique
    @SafeVarargs
    private void fixMultiBlock(Direction direction, BlockPos pos, World world, PlayerEntity player, Class<? extends Block>... blockClass) {
        pos = pos.offset(direction);
        BlockState blockState = world.getBlockState(pos);
        for (Class<? extends Block> clazz: blockClass) {
            if (clazz.isInstance(blockState.getBlock())) {
                Block.dropStacks(blockState, world, pos, null, player, player.getMainHandStack());
                world.removeBlock(pos, false);
                return;
            }
        }
    }
}
