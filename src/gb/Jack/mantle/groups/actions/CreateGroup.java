package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class CreateGroup implements GroupAction {

    @Override
    public boolean run(Player player, User u, Group g, List<String> args) {

        if (!player.hasPermission("mantle.group.manage.create")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        if (u.inGroup()) {
            player.sendMessage(ChatColor.RED + "You must leave the group you're in before you can create one");
            return false;
        }

        if (args.size() < 2) {
            player.sendMessage(ChatColor.RED + "You need to provide a tag and name for your group at a minimum");
            printUsage(player);
            return false;
        }

        String tag = args.get(0);
        String name = args.get(1);
        String password = args.size() == 3 ? args.get(2) : "";

        if (tag.length() < 1 || tag.length() > 4) {
            player.sendMessage(ChatColor.RED + "The group tag must be at least 1 character long with a max of 4 characters");
            return false;
        }

        if (name.length() < 1 || name.length() > 14) {
            player.sendMessage(ChatColor.RED + "The group name must be at least 1 character long with a max of 14 characters");
            return false;
        }

        if (password.length() > 16) {
            player.sendMessage(ChatColor.RED + "The password can only be a max of 16 characters");
            return false;
        }

        if (!tag.matches("^[A-Za-z0-9_]+$") || !name.matches("^[A-Za-z0-9_]+$") || (!password.matches("^[A-Za-z0-9_]+$") && !password.equals(""))) {
            player.sendMessage(ChatColor.RED + "The group tag name, password can't contain special characters");
            return false;
        }

        if (Mantle.db.isGroupTagTaken(tag)) {
            player.sendMessage(ChatColor.RED + "The tag, " + ChatColor.GOLD + tag + ChatColor.RED + ", is already taken by another group");
            return false;
        }

        if (Mantle.db.isGroupNameTaken(name)) {
            player.sendMessage(ChatColor.RED + "The name, " + ChatColor.GOLD + name + ChatColor.RED + ", is already taken by another group");
            return false;
        }

        try {
            int id = Mantle.db.createGroup(u.getDbID(), tag, name, password);

            if (id > 0) {
                Group newG = new Group();

                newG.setDbID(id);
                newG.setOwnerID(u.getDbID());
                newG.setTag(tag);
                newG.setName(name);
                newG.setPassword(password);

                Mantle.groups.registerGroup(newG);
                u.setGroupID(id);

                if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
                    newG.getTeam().addEntry(player.getName());
                }

                newG.printInfo(player, "Created");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while inserting the new group into the database");
            return false;
        }

        return true;
    }

    public void printUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Usage: /group create [tag] [name] [password-optional]");
    }
}