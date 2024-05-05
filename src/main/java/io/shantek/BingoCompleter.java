package io.shantek;

import org.bukkit.Bukkit;
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
        if (args.length == 1 && commandSender instanceof Player player) {
            List<String> complete = new ArrayList<>();
            if (player.hasPermission("shantek.ultimatebingo.start")) {
                complete.add("gui");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
                complete.add("reload");
            }

            complete.add("info");
            complete.add("card");
            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());


        } else if (args.length == 2 && args[0].equalsIgnoreCase("card")) {

            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
        }
    else if (args.length == 2 && args[0].equalsIgnoreCase("gamemode") && commandSender.hasPermission("shantek.ultimatebingo.configure")) {

            List<String> complete = new ArrayList<>();
            complete.add("traditional");
            complete.add("reveal");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());
    }

        return null;
    }
}
