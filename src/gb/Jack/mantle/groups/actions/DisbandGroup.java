package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisbandGroup implements GroupAction {

    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (!player.hasPermission("mantle.group.manage.disband")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        try {
            Mantle.db.deleteGroup(u.getGroupID());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            String gName = g.getName();
            Mantle.groups.unregisterGroup(g.getDbID());

            ArrayList<User> members = g.getOnlineUsers();

            for (User user : members) {
                user.setGroupID(-1);
                user.getPlayerObject().sendMessage(ChatColor.GOLD + "Your group, " + ChatColor.GREEN + gName + ChatColor.GOLD + ", has been disbanded");
            }
        }

        return true;
    }

    @Override
    public void printUsage(Player player) { }
}
