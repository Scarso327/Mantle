package gb.Jack.mantle.commands;

import gb.Jack.mantle.Mantle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GiveHeadCommand implements CommandExecutor {

    private final Map<UUID, Long> activeCooldowns = new HashMap<>();
    public static final int cooldown = 2; // In Minutes

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // The server should never execute this command...
        if (commandSender instanceof ConsoleCommandSender) {
            Mantle.log.info("This command can only be used by a player");
            return false;
        }

        Player senderObject = (Player) commandSender;

        // Usage check...
        if (strings.length > 1) {
            senderObject.sendMessage(ChatColor.RED + "Incorrect usage: /givehead [player name]");
            return false;
        }

        // Check if we even have space...
        if (senderObject.getInventory().firstEmpty() == -1) {
            senderObject.sendMessage(ChatColor.RED + "Your inventory is full");
            return false;
        }

        // Create the skull item...
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

        String name;

        if (strings.length == 1) {
            if (!senderObject.hasPermission("mantle.givehead.others") && !strings[0].equals(senderObject.getName())) {
                senderObject.sendMessage(ChatColor.RED + "You don't have permission to get the heads of others");
                return false;
            }

            if (!strings[0].matches("^[A-Za-z0-9_]+$")) {
                senderObject.sendMessage(ChatColor.RED + "Couldn't find the player " + strings[0]);
                return false;
            }

            name = strings[0];
        } else {
            if (!senderObject.hasPermission("mantle.givehead.self")) {
                senderObject.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                return false;
            }

            name = senderObject.getName();
        }

        if (IsOnCooldown(senderObject.getUniqueId()) && !senderObject.hasPermission("mantle.givehead.bypass")) {
            senderObject.sendMessage(ChatColor.RED + "This command is on cooldown for another " + ChatColor.GREEN + (cooldown - (getCooldown(senderObject.getUniqueId()))) + " minutes");
            return false;
        }

        ArrayList<String> lore = new ArrayList<String>();
        lore.add("Custom Head");

        if (skullMeta != null) {
            skullMeta.setLore(lore);
            skullMeta.setOwner(name);
            skullMeta.setDisplayName(name);
            skullItem.setItemMeta(skullMeta);
        }

        senderObject.getInventory().addItem(skullItem);
        senderObject.sendMessage(ChatColor.GOLD + "You now have " + ChatColor.GREEN + name + "'s " + ChatColor.GOLD + "head!");

        setCooldown(senderObject.getUniqueId(), System.currentTimeMillis());

        return true;
    }

    private void setCooldown(UUID uid, long time) {
        if (time >= 1) {
            activeCooldowns.put(uid, time);
        } else {
            activeCooldowns.remove(uid);
        }
    }

    private long getCooldown(UUID uid) {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - activeCooldowns.getOrDefault(uid, (long) 0));
    }

    private boolean IsOnCooldown(UUID uid) {
        return getCooldown(uid) < cooldown;
    }
}