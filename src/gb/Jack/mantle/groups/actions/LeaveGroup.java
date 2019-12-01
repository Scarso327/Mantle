package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveGroup implements GroupAction {

    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (g.isOwner(u.getDbID())) {
            player.sendMessage(ChatColor.RED + "You can't leave a group you own");
            return false;
        }

        if (!player.hasPermission("mantle.group.user.leave")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        try {
            if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
                g.getTeam().removeEntry(player.getName());
            }

            u.setGroupID(-1);

            Mantle.db.saveUserSettings(u);

            player.sendMessage(ChatColor.GOLD + "You've left the group " + ChatColor.GREEN + g.getName());

            ArrayList<User> members = g.getOnlineUsers();

            if (members.size() > 0) {
                for (User user : members) {
                    user.getPlayerObject().sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " has left the group");
                }
            } else {
                Mantle.groups.unregisterGroup(g.getDbID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void printUsage(Player player) {

    }
}
