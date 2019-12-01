package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class GroupInfo implements GroupAction {

    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (!player.hasPermission("mantle.group.user.info")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        g.printInfo(player, "Info");

        return true;
    }

    @Override
    public void printUsage(Player player) { }
}
