package com.akroZora.highendtechnology.content.blocktype;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.world.phys.shapes.VoxelShape;

import static mekanism.common.util.VoxelShapeUtils.setShape;
import static net.minecraft.world.level.block.Block.box;

public final class CustomShapes {
    private CustomShapes(){}

    public static final VoxelShape[] MACHINE_WORKBENCH = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];


    static {
        setShape(VoxelShapeUtils.combine(
                box(-16, 0, 0, 16, 16, 16) // desk_top
        ),MACHINE_WORKBENCH);
    }
}
