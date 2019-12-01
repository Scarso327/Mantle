package gb.Jack.mantle.commands;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AutoComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            String cmd = command.getName().toLowerCase();

            switch (cmd) {
                case "warp":
                    if (strings.length == 1) {
                        return warpList(strings[0]);
                    }
                    break;
                case "bank":
                    if (strings.length == 1) {
                        return bankList(strings[0]);
                    }
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> bankList(String string) {
        List<String> tabList = new ArrayList<>();

        tabList.add("balance");

        for (String s : tabList) {
            if (!s.toLowerCase().startsWith(string.toLowerCase())) {
                tabList.remove(s);
            }
        }

        return tabList;
    }

    private List<String> warpList(String string) {
        List<String> tabList = new ArrayList<>();
        List<Warp> warpList = new ArrayList<>(Mantle.warps.getWarps().values());

        for (Warp warp : warpList) {
            if (warp.getName().toLowerCase().startsWith(string.toLowerCase())) {
                tabList.add(warp.getName());
            }
        }

        return tabList;
    }
}
