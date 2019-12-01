package gb.Jack.mantle.commands.warps;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.utils.Maths;
import gb.Jack.mantle.utils.SortWarps;
import gb.Jack.mantle.warps.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int page = 1;

        Map<String, Warp> warps = Mantle.warps.getWarps();

        if (!commandSender.hasPermission("mantle.warp.list")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have access to this command");
            return true;
        }

        if (warps.size() < 1) {
            commandSender.sendMessage("There are no warps on this server");
            return true;
        }

        if (strings.length > 0) {
            if (Maths.isInteger(strings[0])) {
                page = Integer.parseInt(strings[0]);
            } else {
                commandSender.sendMessage("The first argument must be a number (Page number)");
                return false;
            }
        }

        List<Warp> warpList = new ArrayList<>(warps.values());
        warpList.sort(new SortWarps());

        int perPage = Mantle.cfg.getInt("Warps.Per-Page");
        int listSize = warpList.size();
        int idxStart = (perPage * page) - perPage;
        int totalPages = Mantle.warps.getPages();

        if (idxStart >= listSize) {
            commandSender.sendMessage("The page you've entered is out of range (Total Pages: " + totalPages + ")");
            return false;
        }

        commandSender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.GREEN + "Warps" + ChatColor.YELLOW + " ----");

        for (int i = idxStart; i < (idxStart + perPage) && i < listSize; i++) {
            Warp w = warpList.get(i);

            commandSender.sendMessage(ChatColor.YELLOW + Integer.toString(i + 1) + ". " + w.getName() + ChatColor.GREEN + w.getAccessString());
        }

        commandSender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.GREEN + "Page (" + page + "/" + totalPages + ")" + ChatColor.YELLOW + " ----");

        return true;
    }
}
