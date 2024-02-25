package pl.bkkuc.atomicitems.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.bkkuc.atomicitems.Plugin;

import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemManager {

    private final FileConfiguration items;

    private Map<String, Item> registeredItems;
    private Map<Player, Map<String, Integer>> cooldown;

    public ItemManager(){
        items = Plugin.getInstance().getItems();
        registeredItems = new HashMap<>();
        cooldown = new HashMap<>();

        init();
    }

    public void init(){
        ConfigurationSection section = items.getConfigurationSection("items");
        if(section == null || section.getKeys(false).isEmpty()) {
            Plugin.getInstance().getLogger().warning("Секция 'items' пуста или не найдена.");
            return;
        }
        for(String name: section.getKeys(false)){
            Item item = new Item(name);
            registeredItems.put(name, item);
        }
        Plugin.getInstance().getLogger().info("Зарегистрировано " + registeredItems.size() + " предметов.");
    }

    public int getCooldown(Player player, String value){
        if(!cooldown.containsKey(player)) return -1;

        Map<String, Integer> cd = cooldown.get(player);

        return cd.getOrDefault(value, -1);
    }

    public void setCooldown(Player player, String value, int seconds){
        Map<String, Integer> cd = cooldown.get(player);

        if(cd == null) cd = new HashMap<>();

        cd.put(value, seconds);
        cooldown.put(player, cd);
    }

    public void removeCooldown(Player player, String value){
        if(!cooldown.containsKey(player)) return;
        Map<String, Integer> cd = cooldown.get(player);

        cd.remove(value);
        cooldown.put(player, cd);
    }
}
