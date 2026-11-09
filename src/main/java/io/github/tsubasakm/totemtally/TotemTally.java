package io.github.tsubasakm.totemtally;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;

/**
 * Makes the Totem of Undying stack by changing its default max stack size.
 * Because the default itself changes, totems stack wherever they are: inventories, containers,
 * hoppers and dropped items. Totems saved with an explicit max_stack_size of 64 (for example by
 * a datapack) lose that entry on load, since it now equals the default, so they stack with the rest.
 * Vanilla already uses up only one totem from a stack when it saves you.
 */
public final class TotemTally implements ModInitializer {
    public static final String MOD_ID = "totem_tally";
    public static final int MAX_STACK_SIZE = 64;

    @Override
    public void onInitialize() {
        DefaultItemComponentEvents.MODIFY.register(context ->
                context.modify(Items.TOTEM_OF_UNDYING, builder -> builder.set(DataComponents.MAX_STACK_SIZE, MAX_STACK_SIZE)));
    }
}
