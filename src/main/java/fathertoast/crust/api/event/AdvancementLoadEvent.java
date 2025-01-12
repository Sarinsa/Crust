package fathertoast.crust.api.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.common.MinecraftForge;

/**
 * Fired when advancements are loaded from json.
 * This event allows for easy modification of
 * an advancement's criteria before it is built.<br>
 * <br>
 *
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} bus, does not
 * have a Result and is not cancelable.
 */
public final class AdvancementLoadEvent extends Event {

    private final ResourceLocation advancementId;
    private final Advancement.Builder builder;

    public AdvancementLoadEvent( ResourceLocation advancementId, Advancement.Builder builder ) {
        this.advancementId = advancementId;
        this.builder = builder;
    }

    /** @return The advancement builder of the advancement being loaded. */
    public Advancement.Builder getBuilder() {
        return this.builder;
    }

    /** @return The ID of the advancement being loaded. */
    public ResourceLocation getId() {
        return this.advancementId;
    }
}
