package io.shantek;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length == 1 && commandSender instanceof Player player){
            List<String> complete = new ArrayList<>();
            if (player.hasPermission("shantek.ultimatebingo.start")){
                complete.add("start");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")){
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")){
                complete.add("settings");
            }

            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
        }

        return null;
    }
}
