package gb.Jack.mantle.commands;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // The server should never execute this command...
        if (commandSender instanceof ConsoleCommandSender) {
            Mantle.log.info("This command can only be used by a player");
            return false;
        }

        Player sender = (Player) commandSender;

        if (strings.length < 1) {
            sender.sendMessage(ChatColor.RED + "You've not entered what action you'd like to perform");
            return false;
        }

        switch (strings[0].toLowerCase()) {
            case "balance":
                User user = Mantle.users.getUser(sender.getUniqueId());

                if (user == null) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while processing your query");
                    return false;
                }

                int bal = user.getBalance();
                sender.sendMessage(ChatColor.GREEN + "Balance: " + ChatColor.GOLD + Mantle.cfg.getString("Banking.Money-Prefix") + bal);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "The action " + ChatColor.GREEN + strings[0] + ChatColor.RED + " is not a valid action");
                return false;
        }

        return true;
    }
}