package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class JoinGroup implements GroupAction {

    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (!player.hasPermission("mantle.group.user.join")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        if (u.inGroup()) {
            player.sendMessage(ChatColor.RED + "You must leave the group you're in before you can join another one");
            return false;
        }

        if (args.size() < 1) {
            player.sendMessage(ChatColor.RED + "You must provide the name of the group you wish to join");
            printUsage(player);
            return false;
        }

        g = Mantle.groups.getGroup(args.get(0));

        if (g == null) {
            player.sendMessage(ChatColor.RED + "The group you have entered doesn't exist");
            player.sendMessage(ChatColor.GREEN + "/groups" + ChatColor.GOLD + " can be used to find all active groups on the server");
            return false;
        }

        if (g.isPassworded()) {
            if (args.size() < 2) {
                player.sendMessage(ChatColor.RED + "This group requires a password to join");
                printUsage(player);
                return false;
            }

            if (!g.isPassword(args.get(1))) {
                player.sendMessage(ChatColor.RED + "The password you've entered for this group is incorrect");
                return false;
            }
        }

        try {
            if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
                g.getTeam().addEntry(player.getName());
            }

            u.setGroupID(g.getDbID());

            Mantle.db.saveUserSettings(u);

            for (User user : g.getOnlineUsers()) {
                user.getPlayerObject().sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GOLD + " has joined the group");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void printUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Usage: /group join [name] [password-if-required]");
    }
}