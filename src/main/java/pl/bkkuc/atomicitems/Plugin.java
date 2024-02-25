package pl.bkkuc.atomicitems;

import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pl.bkkuc.atomicitems.commands.MainCommand;
import pl.bkkuc.atomicitems.listeners.PlayerListener;
import pl.bkkuc.atomicitems.manager.ItemManager;
import pl.bkkuc.atomicitems.tools.ConfigData;
import pl.bkkuc.atomicitems.tools.FileUtility;

@Getter
public final class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance;

    private PlayerPoints playerPoints;

    private ConfigData configData;

    private FileConfiguration items;

    private ItemManager itemManager;

    @Override
    public void onEnable() {
        try {

            instance = this;

            loadFiles();
            loadDepends();

            itemManager = new ItemManager();

            new MainCommand("atomicitems");
            Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);


        } catch (Exception e){
            getLogger().warning("Ошибка при попытке загрузить плагин.");
            getLogger().warning("Кратко: " + e.getMessage());
            getLogger().warning(" ");
            e.printStackTrace();
        }
    }

    private void loadFiles() {
        saveDefaultConfig();
        configData = new ConfigData(getConfig());

        items = FileUtility.get("items.yml");
    }

    public void reloadFiles(){
        reloadConfig();
        configData = new ConfigData(getConfig());

        items = FileUtility.get("items.yml");
    }

    private void loadDepends(){
        playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
