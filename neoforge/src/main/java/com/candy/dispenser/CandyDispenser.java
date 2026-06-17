package com.candy.dispenser;

import com.candy.dispenser.config.CandyDispenserConfig;
import com.candy.dispenser.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CandyDispenser.MOD_ID)
public class CandyDispenser {
    public static final String MOD_ID = "candy_dispenser";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CandyDispenserConfig.CandyDispenserSettings CONFIG;

    public CandyDispenser(ModContainer container, IEventBus modBus) {
        LOGGER.info("Initializing Candy Dispenser");

        container.registerConfig(ModConfig.Type.COMMON, CandyDispenserConfig.SPEC);
        CONFIG = CandyDispenserConfig.CONFIG;

        ModItems.init(modBus);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            // In-game config screen (Config button on the mods list), driven by the ModConfigSpec
            // registered above. Isolated in a client-only class so the dedicated server never loads
            // or verifies the Screen-referencing code — see CandyDispenserNeoForgeClient.
            com.candy.dispenser.client.CandyDispenserNeoForgeClient.init(container);
        }
    }
}
