package gb.Jack.mantle;

import gb.Jack.mantle.commands.AutoComplete;
import gb.Jack.mantle.commands.BankCommand;
import gb.Jack.mantle.commands.GiveHeadCommand;
import gb.Jack.mantle.commands.groups.GroupCommand;
import gb.Jack.mantle.commands.groups.GroupsCommand;
import gb.Jack.mantle.commands.warps.ReloadWarpsCommand;
import gb.Jack.mantle.commands.warps.WarpCommand;
import gb.Jack.mantle.commands.warps.WarpsCommand;
import org.bukkit.command.PluginCommand;

public final class Commands {
    private static void registerBankCommand() {
        PluginCommand command = Mantle.object.getCommand("bank");
        assert command != null;
        command.setTabCompleter(new AutoComplete());
        command.setExecutor(new BankCommand());
    }

    private static void registerGiveHeadCommand() {
        PluginCommand command = Mantle.object.getCommand("givehead");
        assert command != null;
        command.setExecutor(new GiveHeadCommand());
    }

    private static void registerWarpsCommand() {
        PluginCommand command = Mantle.object.getCommand("warps");
        assert command != null;
        command.setExecutor(new WarpsCommand());
    }

    private static void registerReloadWarpsCommand() {
        PluginCommand command = Mantle.object.getCommand("reloadwarps");
        assert command != null;
        command.setExecutor(new ReloadWarpsCommand());
    }

    private static void registerWarpCommand() {
        PluginCommand command = Mantle.object.getCommand("warp");
        assert command != null;
        command.setTabCompleter(new AutoComplete());
        command.setExecutor(new WarpCommand());
    }

    private static void registerGroupsCommand() {
        PluginCommand command = Mantle.object.getCommand("groups");
        assert command != null;
        command.setExecutor(new GroupsCommand());
    }

    private static void registerGroupCommand() {
        PluginCommand command = Mantle.object.getCommand("group");
        assert command != null;
        command.setTabCompleter(new GroupCommand());
        command.setExecutor(new GroupCommand());
    }

    static void setupCommands() {
        registerBankCommand();
        registerGiveHeadCommand();
        registerWarpsCommand();
        registerReloadWarpsCommand();
        registerWarpCommand();
        registerGroupsCommand();
        registerGroupCommand();
    }
}