package io.shantek;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1 && commandSender instanceof Player player) {
            List<String> complete = new ArrayList<>();
            if (player.hasPermission("shantek.ultimatebingo.start")) {
                complete.add("start");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
            }
            if (player.hasPermission("shantek.ultimatebingo.configure")) {
                complete.add("cardsize");
                complete.add("condition");
                complete.add("difficulty");
                complete.add("cardtype");
            }
            complete.add("info");
            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
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

        }

        return null;
    }

}
