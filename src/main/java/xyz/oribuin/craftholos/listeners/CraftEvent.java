package xyz.oribuin.craftholos.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import xyz.oribuin.craftholos.CraftHolograms;
import xyz.oribuin.craftholos.persist.Ch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CraftEvent implements Listener {

    private CraftHolograms plugin;

    public CraftEvent(CraftHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftingTable(CraftItemEvent event) {
        /**
         * Variable Defining
         * Variables:
         * Duration until deletion
         * Player who crafted item
         */
        int duration = plugin.getConfig().getInt("duration") * 20;
        Player player = (Player) event.getInventory().getViewers().get(0);

        /*
         * Should the plugin be enabled?
         */
        if (!plugin.getConfig().getBoolean("plugin-enabled", true)) return;

        /*
         * If a player does not have permission to use the plugin
         * do nothing
         */

        if (!player.hasPermission("craftholo.use")) return;

        /*
         * Defining location settings and variables
         * if the location is null: do nothing
         * if the location = player location: do nothing
         * if the world is disabled: do nothing
         */
        Location location = event.getInventory().getLocation();
        if (location == null) return;

        if (plugin.getConfig().getStringList("disabled-worlds").contains(location.getWorld().getName())) return;

        /*
         * Center the hologram in the middle of the Crafting Table
         */

        location.setY(location.getY() + 2);
        location.setX(location.getX() + 0.500);
        location.setZ(location.getZ() + 0.500);

        /*
         * Create the hologram at the correct location
         * Define the result variable
         */
        Hologram holo = HologramsAPI.createHologram(plugin, location);
        Material result = event.getRecipe().getResult().getType();

        /*
         * If the option to display name is enabled, add name line to hologram
         */

        if (plugin.getConfig().getBoolean("name-display", true))
            holo.appendTextLine(Ch.cl(plugin.getConfig().getString("format")
                    .replaceAll("\\{result}", result.name().replaceAll("_", " "))));

        /*
         * If options to display item is enabled, add item to as a line on hologram
         */

        if (plugin.getConfig().getBoolean("item-display", true))
            holo.appendItemLine(event.getRecipe().getResult());

        /*
         * Wait (duration) seconds before deleting hologram
         */
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                holo.delete();
            }
        }, duration);

    }
}
