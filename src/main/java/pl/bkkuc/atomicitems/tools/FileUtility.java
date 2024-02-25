package pl.bkkuc.atomicitems.tools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.bkkuc.atomicitems.Plugin;

import java.io.File;
import java.io.IOException;

public class FileUtility {

    public static FileConfiguration get(String name){
        File f = new File(Plugin.getInstance().getDataFolder(), name);
        if(Plugin.getInstance().getResource(name) == null) return save(YamlConfiguration.loadConfiguration(f), name);
        if(!f.exists()) Plugin.getInstance().saveResource(name, false);
        return YamlConfiguration.loadConfiguration(f);
    }

    public static FileConfiguration save(FileConfiguration configuration, String name){
        try {
            configuration.save(new File(Plugin.getInstance().getDataFolder(), name));
        } catch (IOException e){
            e.printStackTrace();
        }
        return configuration;
    }
}
