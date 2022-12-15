package com.akroZora.highendtechnology.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class EnderiumBlock extends Block {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
    }
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return pState.getValue(LIT);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide){
            pLevel.setBlock(pPos,pState.setValue(LIT,true),3);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(LIT)) {
            pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(false)), 3);
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    public EnderiumBlock(Properties pProperties) {
        super(pProperties);
    }
}
