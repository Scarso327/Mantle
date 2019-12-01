package gb.Jack.mantle.groups;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.users.User;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class Group {

    private Integer dbID;
    private String name;
    private String tag;
    private Integer ownerID;
    private String password = "";
    private Team team;

    public void setDbID(int id) { dbID = id; }
    public Integer getDbID() { return dbID; }

    public void setName(String n) { name = n; }
    public String getName() { return name; }

    public void setTag(String t) { tag = t; }
    public String getTag() { return tag; }

    public void setOwnerID(int id) { ownerID = id; }
    public Integer getOwnerID() { return ownerID; }
    public boolean isOwner(int uID) { return ownerID == uID; }

    public void setPassword(String pw) { password = pw; }
    public boolean isPassworded() { return !password.equals(""); }
    public boolean isPassword(String pw) { return password.equals(pw); }

    public void setTeam(Team t) {
        team = t;
    }
    public Team getTeam() {
        return team;
    }

    public ArrayList<User> getOnlineUsers() {
        ArrayList<User> users = Mantle.users.getAllUsers();

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);

            if (!u.getGroupID().equals(dbID)) {
                users.remove(i);
                i--;
            }
        }

        return users;
    }

    public void printInfo(Player player, String title) {
        User u = Mantle.users.getUser(player.getUniqueId());

        player.sendMessage(ChatColor.GOLD + "=== Group " + title + " ===");

        player.sendMessage(ChatColor.GOLD + "   Tag: " + ChatColor.GREEN + tag);
        player.sendMessage(ChatColor.GOLD + "   Name: " + ChatColor.GREEN + name);

        if (!password.equals("") && u.getDbID().equals(ownerID)) {
            player.sendMessage(ChatColor.GOLD + "   Password: " + ChatColor.GREEN + password);
        }

        player.sendMessage(ChatColor.GOLD + "=== End of Group ===");
    }
}