package gb.Jack.mantle.commands.groups;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.utils.Maths;
import gb.Jack.mantle.utils.SortGroups;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        int page = 1;

        Map<Integer, Group> groups = Mantle.groups.getGroups();

        if (!commandSender.hasPermission("mantle.group.user.list")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have access to this command");
            return true;
        }

        if (groups.size() < 1) {
            commandSender.sendMessage("There are no groups active on this server");
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

        List<Group> groupList = new ArrayList<>(groups.values());
        groupList.sort(new SortGroups());

        int perPage = Mantle.cfg.getInt("Groups.Per-Page");
        int listSize = groupList.size();
        int idxStart = (perPage * page) - perPage;
        int totalPages = Mantle.groups.getPages();

        if (idxStart >= listSize) {
            commandSender.sendMessage("The page you've entered is out of range (Total Pages: " + totalPages + ")");
            return false;
        }

        commandSender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.GREEN + "Groups" + ChatColor.YELLOW + " ----");

        for (int i = idxStart; i < (idxStart + perPage) && i < listSize; i++) {
            Group g = groupList.get(i);

            commandSender.sendMessage(ChatColor.YELLOW + Integer.toString(i + 1) + ". " + g.getName());
        }

        commandSender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.GREEN + "Page (" + page + "/" + totalPages + ")" + ChatColor.YELLOW + " ----");

        return true;
    }
}
