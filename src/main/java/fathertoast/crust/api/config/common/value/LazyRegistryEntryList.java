package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A list of entries used to match registry entries. Can safely be loaded before its target registry is loaded, but
 * is not able to error check as well. Note that any tags that may be added will not go through any validation.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@SuppressWarnings( "unused" )
public class LazyRegistryEntryList<T> extends RegistryEntryList<T> {
    
    /** The field containing this list. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    /** True if the underlying set has been populated from the print list. */
    private boolean populated;
    
    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * <p>
     * This method of creation can only use entries that are loaded (typically only vanilla entries)
     * and cannot take advantage of the * notation.
     */
    @SuppressWarnings("unchecked")
    public LazyRegistryEntryList( IForgeRegistry<T> registry, List<T> entries ) {
        super( registry, (T[]) entries.toArray() );
        FIELD = null;
        populated = true;
    }

    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * Also allows adding tags.
     * <p>
     * This method of creation can only use entries that are loaded (typically only vanilla entries)
     * and cannot take advantage of the * notation.
     */
    public LazyRegistryEntryList( IForgeRegistry<T> registry, List<TagKey<T>> tags, List<T> entries ) {
        this( registry, entries );
        tags( tags );
    }

    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * <p>
     * This method of creation is less safe, but can take advantage of the regular vanilla entries, deferred entries,
     * resource locations, and raw strings.
     * <p>
     * @param vanilla If true, assume all entries are from vanilla (already loaded)
     */
    @SuppressWarnings("unchecked")
    public LazyRegistryEntryList( IForgeRegistry<T> registry, boolean vanilla, Object... entries ) {
        super( registry );
        FIELD = null;

        if ( vanilla ) {
            populated = true;
        }
        else if ( entries.length > 0 ) {
            for (Object entry : entries) {
                if (registry.containsValue((T)entry)) {
                    final ResourceLocation regKey = registry.getKey((T)entry);
                    if (regKey == null) {
                        throw new IllegalArgumentException("Invalid default lazy registry list entry! " + entry);
                    }
                    PRINT_LIST.add(regKey.toString());
                }
                else if (entry instanceof RegistryObject) {
                    PRINT_LIST.add(((RegistryObject<?>) entry).getId().toString());
                }
                else if (entry instanceof ResourceLocation) {
                    PRINT_LIST.add(entry.toString());
                }
                else if (entry instanceof String) {
                    PRINT_LIST.add((String) entry);
                }
                else {
                    throw new IllegalArgumentException("Invalid default lazy registry list entry! " + entry);
                }
            }
        }
    }

    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     * Also allows adding tags.
     * <p>
     * This method of creation is less safe, but can take advantage of the regular vanilla entries, deferred entries,
     * resource locations, and raw strings.
     */
    public LazyRegistryEntryList( IForgeRegistry<T> registry, boolean vanilla, List<TagKey<T>> tags, Object... entries ) {
        this( registry, vanilla, entries );
        tags( tags );
    }
    
    /**
     * Create a new registry entry list from a list of registry key strings.
     */
    public LazyRegistryEntryList( AbstractConfigField field, IForgeRegistry<T> registry, List<String> entries ) {
        super( registry );
        FIELD = field;
        for( String line : entries ) {
            if ( line.startsWith( "#" ) ) {
                // Get substring after '#' and check if it passes as a valid resource location
                ResourceLocation tagLocation = ResourceLocation.tryParse( line.substring( 1 ) );

                // Not a valid resource location, outrageous
                if ( tagLocation == null ) {
                    ConfigUtil.LOG.warn( "Invalid tag key for {} \"{}\"! Skipping tag. Invalid tag key: {}",
                            field.getClass(), field.getKey(), line );
                }
                else {
                    tag( new TagKey<>( registry.getRegistryKey(), tagLocation ) );
                }
            }
            else if( line.endsWith( "*" ) ) {
                PRINT_LIST.add( line );
            }
            else {
                PRINT_LIST.add( new ResourceLocation( line ).toString() );
            }
        }
    }
    
    /** Fills out the registry entry set with the actual registry entries. */
    private void populateEntries() {
        if( populated ) return;
        populated = true;
        
        for( String line : PRINT_LIST ) {
            if( line.endsWith( "*" ) ) {
                // Handle special case; add all entries in namespace
                if( !mergeFromNamespace( line.substring( 0, line.length() - 1 ) ) ) {
                    ConfigUtil.LOG.warn( "Namespace entry for {} \"{}\" did not match anything! Questionable entry: {}",
                            FIELD.getClass(), FIELD.getKey(), line );
                }
            }
            else {
                // Add a single registry entry
                final ResourceLocation regKey = new ResourceLocation( line );
                if( !mergeFrom( regKey ) ) {
                    ConfigUtil.LOG.warn( "Invalid or duplicate entry for {} \"{}\"! Invalid entry: {}",
                            FIELD.getClass(), FIELD.getKey(), line );
                }
            }
        }
    }
    
    /** @return The entries in this list, except tag contents. */
    @Override
    public Set<T> getEntries() {
        populateEntries();
        return super.getEntries();
    }
    
    /** @return Returns true if there are no entries in this list. */
    @Override
    public boolean isEmpty() { return populated ? super.isEmpty() : PRINT_LIST.isEmpty(); }
    
    /** @return Returns true if the entry is contained in this list. */
    @Override
    public boolean contains( @Nullable T entry ) {
        populateEntries();
        return super.contains( entry );
    }

    @Override
    public boolean containsOrTag(@Nullable T entry, Predicate<TagKey<T>> tagPredicate) {
        populateEntries();
        return super.containsOrTag(entry, tagPredicate);
    }
}