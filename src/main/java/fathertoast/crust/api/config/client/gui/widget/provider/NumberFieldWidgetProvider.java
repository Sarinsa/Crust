package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Displays a text box for a number value.
 */
public class NumberFieldWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final AbstractConfigField FIELD;
    /** Converts the input number into a raw toml value. */
    protected final Function<Number, Object> TO_RAW;
    /** Returns true when the input number is valid. */
    protected final Function<Number, Boolean> VALIDATOR;
    
    public NumberFieldWidgetProvider( AbstractConfigField field, Function<Number, Object> toRaw, Function<Number, Boolean> validator ) {
        FIELD = field;
        TO_RAW = toRaw;
        VALIDATOR = validator;
    }
    
    /**
     * Called to initialize the field's gui components.
     * <p>
     * Positions of the widgets provided (x, y) are relative to the top-left corner of the "field value widget" space.
     * The space available for field value widgets is a {@link #VALUE_WIDTH} by {@link #VALUE_HEIGHT} rectangle
     * (in GUI pixels) that is right-aligned in the parent list widget.
     *
     * @param components   The list to populate with widgets.
     * @param listEntry    The field component (widget "row" within a scrollable list).
     * @param displayValue The current raw value to display in the GUI.
     */
    @Override
    public void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        EditBox editBox = new EditBox( listEntry.minecraft().font,
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( 127 );

        editBox.setValue( TomlHelper.toLiteral( displayValue ) );
        editBox.setResponder( ( value ) -> {
            Number newValue = TomlHelper.parseNumber( value );
            if( newValue == null || !VALIDATOR.apply( newValue ) ) {
                editBox.setTextColor( INVALID_COLOR );
                listEntry.clearValue();
            }
            else {
                editBox.setTextColor( DEFAULT_COLOR );
                listEntry.updateValue( TO_RAW.apply( newValue ) );
            }
        } );
        
        components.add( editBox );
    }
}