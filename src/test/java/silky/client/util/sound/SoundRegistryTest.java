/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.sound;

import silky.client.features.gui.clickgui.sound.GuiSound;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class SoundRegistryTest {
    @Test
    void classGraphDiscoversAnnotatedCatalog() {
        SoundRegistry registry = SoundRegistry.get();
        registry.discover("silky.client.features.gui.clickgui.sound");

        Identifier id = Identifier.fromNamespaceAndPath("silky", "gui/guiscroll");
        SoundDefinition definition = registry.find(id);
        assertNotNull(definition);
        assertEquals(Identifier.fromNamespaceAndPath("silky", "sounds/gui/guiscroll.wav"),
                definition.resource());
        assertEquals(definition, GuiSound.SCROLL.soundDefinition());
    }

    @Test
    void discoversCatalogNestedInsideItsOwner() {
        SoundRegistry registry = SoundRegistry.get();
        registry.discover("silky.client.features.gui.hud.draggable.impl");

        SoundDefinition definition = registry.find(
                Identifier.fromNamespaceAndPath("silky", "notifications/enable_1")
        );
        assertNotNull(definition);
        assertEquals(Identifier.fromNamespaceAndPath("silky", "sounds/enable/enable1.wav"),
                definition.resource());
    }
}
