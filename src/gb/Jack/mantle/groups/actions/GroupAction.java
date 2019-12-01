package gb.Jack.mantle.groups.actions;

import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import org.bukkit.entity.Player;

import java.util.List;

public interface GroupAction {
    public boolean run(Player player, User u, Group g, List<String> args);
    public void printUsage(Player player);
}