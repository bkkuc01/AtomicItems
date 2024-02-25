package pl.bkkuc.atomicitems.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import pl.bkkuc.atomicitems.Plugin;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigData {

    private final FileConfiguration configuration;

    String PREFIX = Plugin.getInstance().getConfig().getString("messages.prefix", "");

    String NO_PERMISSIONS;
    List<String> HELP_PAGE;
    String INVALID_SUB_COMMAND;

    String GIVE_USAGE;
    String INVALID_PLAYER;
    String INVALID_ITEM;
    String SUCCESSFULLY;

    String RELOAD_ERROR;
    String RELOAD_SUCCESSFULLY;

    public ConfigData(FileConfiguration configuration){
        this.configuration = configuration;

        this.NO_PERMISSIONS = validator("messages.noPermissions");
        this.HELP_PAGE = configuration.getStringList("messages.helpPage")
                .stream().map(ColorUtility::color).collect(Collectors.toList());
        this.INVALID_SUB_COMMAND = validator("messages.invalidSubCommand");

        this.GIVE_USAGE = validator("messages.commands.give.usage");
        this.INVALID_PLAYER = validator("messages.commands.give.invalidPlayer");
        this.INVALID_ITEM = validator("messages.commands.give.invalidItem");
        this.SUCCESSFULLY = validator("messages.commands.give.successfully");

        this.RELOAD_ERROR = validator("messages.commands.reload.error");
        this.RELOAD_SUCCESSFULLY = validator("messages.commands.reload.successfully");
    }

    private String validator(String path){
        return ColorUtility.color(configuration.getString(path, "")
                .replace("$prefix", PREFIX));
    }
}
