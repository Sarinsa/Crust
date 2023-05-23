package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.EnumFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with an enum value.
 */
@SuppressWarnings( "unused" )
public class EnumField<T extends Enum<T>> extends GenericField<T> {
    
    /** Valid field values. */
    private final T[] valuesValid;
    
    /** Creates a new field that accepts any enum value. */
    public EnumField( String key, T defaultValue, @Nullable String... description ) {
        //noinspection unchecked
        this( key, defaultValue, (T[]) defaultValue.getClass().getEnumConstants(), description );
    }
    
    /** Creates a new field that accepts the specified set of enum values. */
    public EnumField( String key, T defaultValue, T[] validValues, @Nullable String... description ) {
        super( key, defaultValue, description );
        if( validValues.length == 0 )
            throw new IllegalArgumentException( "Cannot create field with no valid values! Invalid field: " + key );
        valuesValid = validValues;
    }
    
    /** @return An array of all values allowed by this field. */
    public T[] validValues() { return valuesValid; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoValidValues( "Enum", valueDefault, (Object[]) valuesValid ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        T newValue = null;
        if( raw instanceof String ) {
            // Parse the value
            newValue = parseValue( (String) raw );
        }
        if( newValue == null ) {
            // Value cannot be parsed to this field
            if( raw != null ) {
                ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to default. Invalid value: {}",
                        getClass(), getKey(), raw );
            }
            newValue = valueDefault;
        }
        value = newValue;
    }
    
    /** @return Attempts to parse the string literal as one of the valid values for this field and returns it, or null if invalid. */
    @Nullable
    private T parseValue( String name ) {
        for( T val : valuesValid ) {
            if( val.name().equalsIgnoreCase( name ) ) return val;
        }
        return null;
    }
    
    /** @return The raw toml value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public Object getRaw() { return value == null ? null : TomlHelper.enumToString( value ); }
    
    /** @return The default raw toml value of this field. */
    @Override
    public Object getRawDefault() { return TomlHelper.enumToString( valueDefault ); }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new EnumFieldWidgetProvider<>( this ); }
}