package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.core.Crust;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * The initial loading for this is done in the Crust mod's constructor ({@link Crust#Crust()}).
 * <p>
 * We also add functionality to the Forge "Config" button in the client setup event
 * ({@link fathertoast.crust.client.ClientRegister#onClientSetup(FMLClientSetupEvent)}).
 */
public final class CrustConfig {
    
    /** Crust's config manager. Defines the mod config folder. */
    private static final ConfigManager MANAGER = ConfigManager.create( "Crust" );
    
    /** File for configuring modes. */
    public static final CrustModesConfigFile MODES = new CrustModesConfigFile( MANAGER, "modes" );
    /** File for configuring default game rules. */
    public static final GameRulesCrustConfigFile DEFAULT_GAME_RULES = new GameRulesCrustConfigFile( MANAGER, "default_game_rules" );
}