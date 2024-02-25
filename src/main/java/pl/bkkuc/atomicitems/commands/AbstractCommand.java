package pl.bkkuc.atomicitems.commands;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.bkkuc.atomicitems.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    public AbstractCommand(String command){
        PluginCommand pluginCommand = Plugin.getInstance().getCommand(command);
        if(pluginCommand != null) pluginCommand.setExecutor(this);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> complete(CommandSender sender, String[] args){
        return null;
    }

    private List<String> filter(List<String> filtring, String[] args){
        return filtring.stream().filter(s -> s.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(complete(sender, args) == null) return null;
        return filter(complete(sender, args), args);
    }
}
