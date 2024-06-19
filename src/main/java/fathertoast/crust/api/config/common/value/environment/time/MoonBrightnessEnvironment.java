package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;

public class MoonBrightnessEnvironment extends CompareFloatEnvironment {
    
    public MoonBrightnessEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public MoonBrightnessEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected float getMinValue() { return 0.0F; }
    
    /** @return The maximum value that can be given to the value. */
    @Override
    protected float getMaxValue() { return 1.0F; }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( Level level, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : DimensionType.MOON_BRIGHTNESS_PER_PHASE[level.dimensionType().moonPhase( level.dayTime() )];
    }
}