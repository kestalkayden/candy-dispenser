package com.candy.dispenser;

import com.candy.dispenser.config.CandyDispenserConfig;
import com.candy.dispenser.item.ModItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandyDispenser implements ModInitializer {
    public static final String MOD_ID = "candy_dispenser";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CandyDispenserConfig CONFIG;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Candy Dispenser");

        AutoConfig.register(CandyDispenserConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(CandyDispenserConfig.class).getConfig();

        ModItems.registerItems();
        CandyDispenserHandler.initialize();
    }
}
