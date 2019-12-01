package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KickGroup implements GroupAction {
    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (!player.hasPermission("mantle.group.user.leave")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        if (args.size() < 1) {
            player.sendMessage(ChatColor.RED + "You must provide the name of the player you wish to kick");
            printUsage(player);
            return false;
        }

        User member = Mantle.users.getUser(args.get(0));

        if (member == null) {
            player.sendMessage(ChatColor.RED + "This player doesn't exist");
            return false;
        }

        if (member.getDbID().equals(u.getDbID()) || member.getDbID().equals(g.getOwnerID())) {
            player.sendMessage(ChatColor.RED + "You can't kick yourself or the group leader");
            return false;
        }

        if (!member.getGroupID().equals(u.getGroupID())) {
            player.sendMessage(ChatColor.RED + "This player isn't in your group");
            return false;
        }

        try {
            Player mObject = member.getPlayerObject();

            if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
                g.getTeam().removeEntry(mObject.getName());
            }

            member.setGroupID(-1);

            Mantle.db.saveUserSettings(member);

            String reason = args.size() > 1 ? " with the reason: " + args.get(1) : " without a reason";

            mObject.sendMessage(ChatColor.GOLD + "You've been kicked from the group " + ChatColor.GREEN + g.getName() + reason);

            ArrayList<User> members = g.getOnlineUsers();

            if (members.size() > 0) {
                for (User user : members) {
                    user.getPlayerObject().sendMessage(ChatColor.GREEN + mObject.getName() + ChatColor.GOLD + " has been kicked from the group");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void printUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Usage: /group kick [name] [reason-optional]");
    }
}
