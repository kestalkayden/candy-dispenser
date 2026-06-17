package com.candy.dispenser.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu integration — supplies the "Config" button on the Fabric mod list screen.
 *
 * <p>Registered under the {@code "modmenu"} entrypoint in {@code fabric.mod.json}. Client-only:
 * it references {@link CandyDispenserConfigScreen} (which imports {@code Screen}), so it must never
 * load on a dedicated server. Fabric's modmenu entrypoint loading guarantees that.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new CandyDispenserConfigScreen(parent);
    }
}
