package com.candy.dispenser.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only NeoForge wiring, isolated from the {@code @Mod} class.
 *
 *  <p>Registering {@code IConfigScreenFactory} touches {@code ConfigurationScreen} (a vanilla
 *  {@code Screen} subclass). The JVM verifies every method of a class when it links it, and the
 *  {@code @Mod} class is linked during {@code constructMods} — so doing this inline on {@code @Mod}
 *  would force-load {@code net.minecraft.client.*} on a dedicated server, which strips those
 *  classes, and crash before any {@code Dist.CLIENT} guard could run. Reaching this class only via
 *  a guarded {@code invokestatic} (lazy resolution) keeps the dedicated server from ever loading
 *  or verifying it. */
public final class CandyDispenserNeoForgeClient {
    private CandyDispenserNeoForgeClient() {}

    public static void init(ModContainer container) {
        // NeoForge's built-in ConfigurationScreen renders the already-registered ModConfigSpec —
        // the same Config button main (26.2) gets automatically. NeoForge 26.1 doesn't auto-register
        // it, so wire it explicitly here.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
