package gb.Jack.mantle.commands.groups;

import com.google.common.collect.ImmutableList;
import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.groups.actions.*;
import gb.Jack.mantle.users.User;
import gb.Jack.mantle.utils.PrettyPrint;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCommand implements CommandExecutor, TabCompleter {

    private GroupInfo INFO = new GroupInfo();
    private CreateGroup CREATE = new CreateGroup();
    private DisbandGroup DISBAND = new DisbandGroup();
    private KickGroup KICK = new KickGroup();
    private JoinGroup JOIN = new JoinGroup();
    private LeaveGroup LEAVE = new LeaveGroup();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof ConsoleCommandSender) {
            Mantle.log.info("Group commands are only available to players");
            return false;
        }

        Player player = (Player) commandSender;

        User u = Mantle.users.getUser(player.getUniqueId());

        if (u == null) {
            player.kickPlayer("No user profile found...");
            return false;
        }

        List<String> args = new ArrayList<>(Arrays.asList(strings));

        if (args.size() < 1) {
            commandSender.sendMessage(ChatColor.RED + "You must enter an action you wish to preform");
            printUsage(commandSender);
            return false;
        }

        GroupSubcommandType subCommand = GroupSubcommandType.getCommand(args.get(0));

        if (subCommand == null) {
            commandSender.sendMessage(ChatColor.RED + "The action you've entered does not exist");
            printUsage(commandSender);
            return false;
        }

        args.remove(0); // We don't need the "action" index for the commands...

        // These commands can always be used (They still have internal checks)
        switch (subCommand) {
            case CREATE:
                return CREATE.run(player, u, null, args);
            case JOIN:
                return JOIN.run(player, u, null, args);
        }

        if (!u.inGroup()) {
            player.sendMessage(ChatColor.RED + "You must be in a group to use this command");
            return false;
        }

        Group g = Mantle.groups.getGroup(u.getGroupID());

        if (g == null) {
            player.sendMessage(ChatColor.RED + "An error occurred while retrieving your group");
            return false;
        }

        // These command can only be used inside a group
        switch (subCommand) {
            case INFO:
                return INFO.run(player, u, g, args);
            case LEAVE:
                return LEAVE.run(player, u, g, args);
        }

        if (!g.isOwner(u.getDbID())) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        // These commands only the group leader can use
        switch (subCommand) {
            case DISBAND:
                return DISBAND.run(player, u, g, args);
            case KICK:
                return KICK.run(player, u, g, args);
        }

        return false;
    }

    // We don't have to check the command name here as it'll always be "Group"
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            switch (strings.length) {
                case 1:
                    return getGroupCommands(strings[0]);
                case 2:
                    if (strings[0].toLowerCase().equals("join")) {
                        return getActiveGroups(strings[1]);
                    }
                    break;
                case 3:
                    if (strings[0].toLowerCase().equals("join")) {
                        return ImmutableList.of();
                    }
            }
        }

        return null;
    }

    private List<String> getGroupCommands(String string) {
        GroupSubcommandType[] list = GroupSubcommandType.values();
        List<String> tabList = new ArrayList<>();

        for (GroupSubcommandType s : list) {
            String name = s.toString().toLowerCase();

            if (name.startsWith(string.toLowerCase())) {
                tabList.add(name);
            }
        }

        return tabList;
    }

    private List<String> getActiveGroups(String string) {
        List<Group> groupList = new ArrayList<>(Mantle.groups.getGroups().values());
        List<String> tabList = new ArrayList<>();

        for (Group g : groupList) {
            String name = g.getName();

            if (name.toLowerCase().startsWith(string.toLowerCase())) {
                tabList.add(name);
            }
        }

        return tabList;
    }

    private void printUsage(CommandSender s) {
        s.sendMessage(ChatColor.GREEN + "Available Actions: " + ChatColor.GOLD + PrettyPrint.printString(GroupSubcommandType.toList()));
    }
}
