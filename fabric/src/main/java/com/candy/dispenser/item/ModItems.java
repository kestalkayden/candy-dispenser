package com.candy.dispenser.item;

import com.candy.dispenser.CandyDispenser;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ModItems {

    public static final Item CANDY_MIX = registerItem("candy_mix", Item::new,
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.8f)
            .build()));

    public static final Item CANDY = registerItem("candy", Item::new,
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.8f)
            .build()));

    public static final Item CANDY_DISPENSER = registerItem("candy_dispenser",
        CandyDispenserItem::new,
        new Item.Properties().stacksTo(1));

    private static ResourceKey<Item> candyItemId(String name) {
        return ResourceKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(CandyDispenser.MOD_ID, name));
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> ctor, Item.Properties props) {
        ResourceKey<Item> key = candyItemId(name);
        Item item = ctor.apply(props.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void registerItems() {
        CandyDispenser.LOGGER.info("Registering Mod Items for {}", CandyDispenser.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.accept(new ItemStack(CANDY_MIX));
            entries.accept(new ItemStack(CANDY));
            entries.accept(new ItemStack(CANDY_DISPENSER));
        });
    }
}
