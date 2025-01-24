package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class RegistryValueEntry<T> {

    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;

    /** The registry key for this registry object. */
    public final ResourceLocation REG_KEY;
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;


    /** Creates an entry used to compare internally with the entries in a registry entry value list. */
    RegistryValueEntry( IForgeRegistry<T> registry, T registryObject ) {
        FIELD = null;
        VALUES = null;
        REG_KEY = registry.getKey( registryObject );
    }

    /**
     * Creates an entry with the specified values that acts as a default matching all entries for the target registry.
     * Used for creating default configs.
     */
    public RegistryValueEntry( double... values ) { this( null, values ); }

    /** Creates an entry with the specified values. Used for creating default configs. */
    public RegistryValueEntry( @Nullable ResourceLocation regKey, double... values ) {
        this( null, regKey, values );
    }

    /** Creates an entry with the specified values. */
    public RegistryValueEntry( @Nullable AbstractConfigField field, @Nullable ResourceLocation regKey, double... values ) {
        FIELD = field;
        REG_KEY = regKey;
        VALUES = values;
    }

    /** @return Loads the registry object from registry. Returns true if successful. */
    private boolean validate( IForgeRegistry<T> registry ) {
        if( REG_KEY == null ) return true; // Null reg key means this is a default entry

        if( !registry.containsKey( REG_KEY ) ) {
            ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Invalid entry: {}",
                    FIELD.getClass(), FIELD.getKey(), REG_KEY.toString() );
            return false;
        }
        return true;
    }

    /** @return Returns true if the given registry value entry's registry ID is the same as this one. */
    public boolean contains( IForgeRegistry<T> registry, RegistryValueEntry<T> entry ) {
        if( !validate( registry ) ) return false;

        return entry.REG_KEY.equals( REG_KEY );
    }

    /**
     * @return The string representation of this registry value entry, as it would appear in a config file.
     * <p>
     * Format is "registry_key value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the entity type registry key
        StringBuilder str = new StringBuilder( REG_KEY.toString() );

        // Append values array
        if( VALUES != null && VALUES.length > 0 ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}
