package pl.bkkuc.atomicitems.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.bkkuc.atomicitems.Plugin;
import pl.bkkuc.atomicitems.manager.Item;
import pl.bkkuc.atomicitems.manager.ItemBuilder;
import pl.bkkuc.atomicitems.tools.ConfigData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainCommand extends AbstractCommand {

    private final ConfigData config = Plugin.getInstance().getConfigData();

    public MainCommand(String command) {
        super(command);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("atomicitems.use")){
            sender.sendMessage(config.getNO_PERMISSIONS());
            return;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            config.getHELP_PAGE().forEach(sender::sendMessage);
            return;
        }
        if(args[0].equalsIgnoreCase("give")){
            if(args.length < 3){
                sender.sendMessage(config.getGIVE_USAGE());
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            Item item = Plugin.getInstance().getItemManager().getRegisteredItems().get(args[2]);

            if(player == null){
                sender.sendMessage(config.getINVALID_PLAYER());
                return;
            }
            if(item == null){
                sender.sendMessage(config.getINVALID_ITEM());
                return;
            }

            sender.sendMessage(config.getSUCCESSFULLY()
                    .replace("$player", player.getName())
                    .replace("$item", item.getName()));

            ItemStack itemStack = ItemBuilder.get(item);

            if(player.getInventory().firstEmpty() == -1){
                player.getWorld().dropItem(player.getLocation(), itemStack);
            } else {
                player.getInventory().addItem(itemStack);
            }

            return;
        }
        else if(args[0].equalsIgnoreCase("reload")){
            try {

                Plugin.getInstance().reloadFiles();

            } catch (Exception e){
                e.printStackTrace();
                sender.sendMessage(config.getRELOAD_ERROR());
                return;
            }
            return;
        }
        sender.sendMessage(config.getINVALID_SUB_COMMAND());
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(!sender.hasPermission("atomicitems.use")) return null;

        if(args.length == 1) return Arrays.asList("give", "reload");
        if(args.length == 3 && args[0].equalsIgnoreCase("give")) return new ArrayList<>(Plugin.getInstance().getItemManager().getRegisteredItems().keySet());

        return super.complete(sender, args);
    }
}
