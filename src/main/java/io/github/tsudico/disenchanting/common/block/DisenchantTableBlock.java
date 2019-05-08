package io.github.tsudico.disenchanting.common.block;

import io.github.tsudico.disenchanting.Disenchanting;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DisenchantTableBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
        return SHAPE;
    }

    public DisenchantTableBlock(Block.Settings block$Settings) { super(block$Settings); }

    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    public BlockEntity createBlockEntity(BlockView blockView) {
        return new EnchantingTableBlockEntity();
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        // Container registration
        if(!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(Disenchanting.DISENCHANT_CONTAINER, player, buf -> buf.writeBlockPos(blockPos));
        }
        return true;
    }

    public void onBroken(IWorld world, BlockPos blockPos, BlockState blockState) {
        dropStack(world.getWorld(), blockPos, getItem().getDefaultStack());
    }
}
