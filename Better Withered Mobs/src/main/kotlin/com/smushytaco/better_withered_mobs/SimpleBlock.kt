package com.smushytaco.better_withered_mobs

import eu.pb4.polymer.blocks.api.BlockModelType
import eu.pb4.polymer.blocks.api.PolymerBlockModel
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier

class SimpleBlock(settings: Settings?, path: String) : Block(settings), PolymerTexturedBlock{
    private var BLOCKSTATE: BlockState? = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Identifier.of(BetterWitheredMobs.MOD_ID, path)))
    override fun getPolymerBlockState(state: BlockState?): BlockState? {
        return BLOCKSTATE;
    }
}