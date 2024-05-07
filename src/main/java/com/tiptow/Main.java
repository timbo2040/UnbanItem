package com.tiptow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("UnbanItem has been enabled!");

        // Add the crafting recipe
        addCraftingRecipe();
    }

    @Override
    public void onDisable() {
        getLogger().info("UnbanItem has been disabled!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BARRIER && event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true); // Prevents using the item
            player.sendMessage(ChatColor.YELLOW + "Enter the username of the player you want to unban:");
            player.sendMessage(ChatColor.GRAY + "(Type 'cancel' to cancel)");

            // Store the player's name for later reference
            player.setMetadata("unbanItem", new org.bukkit.metadata.FixedMetadataValue(this, item));
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = event.getMessage();

        // Check if the player has the metadata set
        if (player.hasMetadata("unbanItem")) {
            ItemStack item = (ItemStack) player.getMetadata("unbanItem").get(0).value();

            if (playerName.equalsIgnoreCase("cancel")) {
                player.sendMessage(ChatColor.YELLOW + "Unban canceled.");
            } else {
                // Unban the player
                if (Bukkit.getOfflinePlayer(playerName).isBanned()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pardon " + playerName);
                    player.sendMessage(ChatColor.GREEN + "You have unbanned " + playerName + "!");
                    // Remove item
                    player.getInventory().removeItem(item);
                    player.updateInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "Player " + playerName + " is not banned.");
                }
                player.removeMetadata("unbanItem", this);
            }
            event.setCancelled(true); // Prevent the chat message from being sent to the chat
        }
    }

    private void addCraftingRecipe() {
        // Create the Unban Item
        ItemStack unbanItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = unbanItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Unban Item");
        unbanItem.setItemMeta(meta);

        // Define the crafting recipe for the Unban Item
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "unban_item"), unbanItem);
        recipe.shape("ABA", "DCD", "EEE");
        recipe.setIngredient('A', Material.SHULKER_SHELL);
        recipe.setIngredient('B', Material.DRAGON_HEAD);
        recipe.setIngredient('C', Material.NETHER_STAR);
        recipe.setIngredient('D', Material.GOLDEN_APPLE);
        recipe.setIngredient('E', Material.DIAMOND_BLOCK);

        // Add the recipe to the server
        Bukkit.addRecipe(recipe);
    }
}

