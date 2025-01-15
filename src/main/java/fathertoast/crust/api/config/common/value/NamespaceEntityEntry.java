package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.field.AbstractConfigField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Entity list entry that covers all entity types under a given mod namespace.
 * It is intended to have the lowest priority in an entity list when finding best match.
 */
public class NamespaceEntityEntry {

    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;

    /** The registry key for this entry's entity type. */
    public final String NAMESPACE;

    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;



    /** Creates an entry with the specified values. Used for creating default configs. */
    public NamespaceEntityEntry( @Nonnull String namespace, double... values ) {
        this( null, namespace, values );
    }

    /** Creates an entry with the specified values. */
    public NamespaceEntityEntry( @Nullable AbstractConfigField field, @Nonnull String namespace, double... values ) {
        Objects.requireNonNull( namespace );

        FIELD = field;
        NAMESPACE = namespace;
        VALUES = values;
    }

    /** @return True if nothing is wrong with this entry. Currently unused. */
    private boolean validate() {
        // No real validation needed.
        // Could potentially check if namespace exists, but no big deal.
        return true;
    }

    /**
     * @return Returns true if the given entry's registry key matches the namespace of this namespace-entry.
     */
    public boolean contains( EntityEntry entry ) {
        if( !validate() ) return false;

        return entry.ENTITY_KEY.getNamespace().equals( NAMESPACE );
    }

    /**
     * @return The string representation of this entity list multi-entry, as it would appear in a config file.
     * <p>
     * Format is "namespace:* value0 value1 ...".
     */
    @Override
    public String toString() {

        // Start with the entity type registry key
        StringBuilder str = new StringBuilder( NAMESPACE ).append( ":*" );

        // Append values array
        if( VALUES != null ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}
