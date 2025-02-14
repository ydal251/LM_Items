package io.github.stumper66.lm_items.plugins;

import io.github.stumper66.lm_items.ExternalItemRequest;
import io.github.stumper66.lm_items.GetItemResult;
import io.github.stumper66.lm_items.ItemsAPI;
import io.github.stumper66.lm_items.SupportsExtras;
import io.github.stumper66.lm_items.Utils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class MMOItems implements ItemsAPI, SupportsExtras {
    public MMOItems() {
        checkDeps();
        this.supportedTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (this.isInstalled)
            buildSupportedItemTypes();
    }

    private boolean isInstalled;
    private final Set<String> supportedTypes;

    private void checkDeps(){
        for (final String dep : List.of(getName(), "MythicLib")){
            if (Bukkit.getPluginManager().getPlugin(dep) == null)
                return;
        }

        this.isInstalled = true;
    }

    public boolean getIsInstalled() {
        return this.isInstalled;
    }

    public @NotNull String getName(){
        return "MMOItems";
    }

    public @NotNull GetItemResult getItem(final @NotNull ExternalItemRequest itemRequest){
        final GetItemResult result = new GetItemResult(this.isInstalled);

        if (!result.pluginIsInstalled)
            return result;

        final net.Indyuce.mmoitems.MMOItems plugin = net.Indyuce.mmoitems.MMOItems.plugin;

        net.Indyuce.mmoitems.api.item.mmoitem.MMOItem mmoitem;
        if (itemRequest.extras != null && itemRequest.extras.containsKey("ItemLevel")){
            final int itemLevel = Utils.getIntValue(itemRequest.extras, "ItemLevel", 1);
            mmoitem = plugin.getMMOItem(plugin.getTypes().get(itemRequest.itemType), itemRequest.itemId, itemLevel, null);
        }
        else{
            mmoitem = plugin.getMMOItem(plugin.getTypes().get(itemRequest.itemType), itemRequest.itemId);
        }

        if (mmoitem == null) return result;

        result.itemStack = mmoitem.newBuilder().build();

        if (itemRequest.amount != null && result.itemStack != null) {
            final double useAmount = itemRequest.amount;
            result.itemStack.setAmount((int) useAmount);
        }

        return result;
    }

    private void buildSupportedItemTypes(){
        final net.Indyuce.mmoitems.MMOItems mmoItems = net.Indyuce.mmoitems.MMOItems.plugin;

        final List<String> names = new ArrayList<>(mmoItems.getTypes().getAll().size());
        for (final net.Indyuce.mmoitems.api.Type type : mmoItems.getTypes().getAll())
            this.supportedTypes.add(type.toString());

        Collections.sort(names);
        this.supportedTypes.addAll(names);
    }

    public @NotNull Collection<String> getItemTypes() {
        if (this.isInstalled)
            return this.supportedTypes;
        else
            return Collections.emptyList();
    }

    public @NotNull Collection<String> getSupportedExtras(){
        return List.of("ItemLevel");
    }
}
