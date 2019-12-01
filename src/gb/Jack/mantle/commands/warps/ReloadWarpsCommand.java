package gb.Jack.mantle.commands.warps;

import gb.Jack.mantle.Mantle;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadWarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("mantle.warp.reload")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have access to this command");
            return true;
        }

        commandSender.sendMessage(ChatColor.GREEN + "Reloading Warps...");
        Mantle.warps.setupWarps(commandSender instanceof ConsoleCommandSender ? null : (Player) commandSender);
        return true;
    }
}