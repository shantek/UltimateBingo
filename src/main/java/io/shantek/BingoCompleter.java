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
                complete.add("start");
                complete.add("gui");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
                complete.add("reload");
            }
            // Marked for removal - replaced by GUI
            /*
            if (player.hasPermission("shantek.ultimatebingo.configure")) {
                complete.add("cardsize");
                complete.add("condition");
                complete.add("difficulty");
                complete.add("cardtype");
                complete.add("gamemode");
            }
            */
            complete.add("info");
            complete.add("reveal");
            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());

            // Marked for removal - replaced by GUI
            /*
        } else if (args.length == 2 && args[0].equalsIgnoreCase("cardtype") && commandSender.hasPermission("shantek.ultimatebingo.configure")) {


            List<String> complete = new ArrayList<>();
            complete.add("identical");
            complete.add("unique");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());

        } else if (args.length == 2 && args[0].equalsIgnoreCase("cardsize") && commandSender.hasPermission("shantek.ultimatebingo.configure")) {
            List<String> complete = new ArrayList<>();
            complete.add("small");
            complete.add("medium");
            complete.add("large");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());

        } else if (args.length == 2 && args[0].equalsIgnoreCase("condition") && commandSender.hasPermission("shantek.ultimatebingo.configure")) {
            List<String> complete = new ArrayList<>();
            complete.add("fullcard");
            complete.add("bingo");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());

        } else if (args.length == 2 && args[0].equalsIgnoreCase("difficulty") && commandSender.hasPermission("shantek.ultimatebingo.configure")) {
            List<String> complete = new ArrayList<>();
            complete.add("easy");
            complete.add("normal");
            complete.add("hard");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());
*/
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reveal")) {

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
