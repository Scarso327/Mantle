package gb.Jack.mantle.commands.warps;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.users.User;
import gb.Jack.mantle.warps.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // The server should never execute this command...
        if (commandSender instanceof ConsoleCommandSender) {
            Mantle.log.info("This command can only be used by a player");
            return false;
        }

        if (!commandSender.hasPermission("mantle.warp.action")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have access to this command");
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "You haven't entered the warp you'd like to use");
            return false;
        }

        Warp warp = Mantle.warps.getWarp(strings[0]);

        if (warp == null) {
            commandSender.sendMessage(ChatColor.RED + "The location you've entered is not recognised. You can find all available warps using " + ChatColor.GREEN + "/warps");
            return false;
        }

        if (warp.isPassworded()) {
            if (strings.length < 2) {
                commandSender.sendMessage(ChatColor.RED + "This warp requires a password");
                return false;
            } else {
                if (!warp.isPassword(strings[1])) {
                    commandSender.sendMessage(ChatColor.RED + "The password you entered is incorrect");
                    return false;
                }
            }
        }

        User sender = Mantle.users.getUser(((Player) commandSender).getUniqueId());
        Location loc = warp.getLocation();

        if (loc == null) {
            commandSender.sendMessage(ChatColor.RED + "The world this warp exists in can't be found");
            return false;
        }

        if (sender.warp(loc)) {
            commandSender.sendMessage(ChatColor.GOLD + "You've warped to " + ChatColor.GREEN + warp.getName());
        } else {
            commandSender.sendMessage(ChatColor.RED + "An error occurred while attempting to teleport you");
        }

        return true;
    }
}
