package pl.bkkuc.atomicitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.bkkuc.atomicitems.Plugin;
import pl.bkkuc.atomicitems.manager.Ability;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    private final Plugin plugin;

    public PlayerListener(Plugin plugin){
        this.plugin = plugin;
    }

    public static List<Player> push = new ArrayList<>();

    @EventHandler
    public void onPlayerItemUse(PlayerInteractEvent e) {
        push.remove(e.getPlayer());
        if (!e.getAction().name().contains("RIGHT")) return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("atomicitems")) return;

        Player player = e.getPlayer();

        String value = nbtItem.getString("atomicitems");

        int cooldown = plugin.getItemManager().getCooldown(player, value);

        if (cooldown != -1) {
            player.sendMessage("У вас есть задержка: " + plugin.getItemManager().getCooldown(player, value));
            return;
        }
        plugin.getItemManager().setCooldown(player, value, plugin.getItems().getInt("cooldowns." + value, 20));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getItemManager().getCooldown(player, value) <= 0) {
                    plugin.getItemManager().removeCooldown(player, value);
                    cancel();
                    return;
                }
                plugin.getItemManager().setCooldown(player, value, plugin.getItemManager().getCooldown(player, value) - 1);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        Ability.startAbility(player, value.toLowerCase());

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        ItemStack item = e.getItemInHand();
        if(item.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(item);
        if(!nbtItem.hasTag("atomicitems")) return;

        e.setCancelled(true);
    }
}
