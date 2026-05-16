package com.candy.dispenser.item;

import com.candy.dispenser.CandyDispenser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@EventBusSubscriber(modid = CandyDispenser.MOD_ID)
public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CandyDispenser.MOD_ID);

    public static final DeferredHolder<Item, Item> CANDY_MIX = registerItem("candy_mix", Item::new,
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.8f)
            .build()));

    public static final DeferredHolder<Item, Item> CANDY = registerItem("candy", Item::new,
        new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.8f)
            .build()));

    public static final DeferredHolder<Item, Item> CANDY_DISPENSER = registerItem("candy_dispenser",
        CandyDispenserItem::new,
        new Item.Properties().stacksTo(1));

    private static ResourceKey<Item> candyItemId(String name) {
        return ResourceKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(CandyDispenser.MOD_ID, name));
    }

    private static DeferredHolder<Item, Item> registerItem(String name, Function<Item.Properties, Item> ctor, Item.Properties props) {
        ResourceKey<Item> key = candyItemId(name);
        Item.Properties withId = props.setId(key);
        return ITEMS.register(name, () -> ctor.apply(withId));
    }

    public static void init(IEventBus modBus) {
        CandyDispenser.LOGGER.info("Registering Mod Items for {}", CandyDispenser.MOD_ID);
        ITEMS.register(modBus);
    }

    @SubscribeEvent
    public static void addItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(new net.minecraft.world.item.ItemStack(CANDY_MIX.get()));
            event.accept(new net.minecraft.world.item.ItemStack(CANDY.get()));
            event.accept(new net.minecraft.world.item.ItemStack(CANDY_DISPENSER.get()));
        }
    }
}
