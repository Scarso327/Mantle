package gb.Jack.mantle.users;

import gb.Jack.mantle.Mantle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Users {
    private Map<UUID, User> users = new HashMap<>();

    public void registerUser(Player object) {
        if (!users.containsKey(object.getUniqueId())) {
            User user = new User();

            user.setPlayerObject(object);

            try {
                int id = Mantle.db.getUserID(object.getUniqueId());

                if (id == -1) {
                    id = Mantle.db.newUser(object.getUniqueId(), object.getName());
                } else {
                    Mantle.db.setLastLogin(id, object);
                }

                user.setDbID(id);
                user = Mantle.db.getUserSettings(user); // Updates settings like our balance...
            } catch (SQLException e) {
                e.printStackTrace();
                Mantle.log.info("Unable to handle DB for user: " + object.getUniqueId());
            } finally {
                int gID = user.getGroupID();

                if (gID != -1 && Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
                    Mantle.groups.getGroup(gID).getTeam().addEntry(object.getName());
                }

                users.put(object.getUniqueId(), user);
                Mantle.log.info("Registering User: " + object.getUniqueId());
            }
        }
    }

    public void unregisterUser(UUID uid) {
        User u = getUser(uid);

        if (u != null) {
            try {
                Mantle.db.saveUserSettings(u);
            } catch (SQLException e) {
                e.printStackTrace();

                Mantle.log.info("Unable to save User: " + uid);
            } finally {
                int groupid = u.getGroupID();

                users.remove(uid);

                Mantle.log.info("Unregistered User: " + uid);

                if (groupid != -1) {
                    if (Mantle.groups.getGroup(groupid).getOnlineUsers().size() <= 0) {
                        Mantle.groups.unregisterGroup(groupid);
                    }
                }
            }
        }
    }

    // Return a specific user...
    public User getUser(UUID uid) {
        return users.getOrDefault(uid, null);
    }

    public User getUser(String name) {
        User u = null;

        for (User user : new ArrayList<>(users.values())) {
            if (user.getPlayerObject().getName().toLowerCase().equals(name.toLowerCase())) {
                u = user;
                break;
            }
        }

        return u;
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    public void saveAllUsers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Mantle.db.saveUserSettings(getUser(player.getUniqueId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupUsers() {
        Mantle.log.info("=== Mass Registering Users ===");
        Mantle.log.info("== (Likely Caused By Reload) ==");
        for (Player p : Bukkit.getOnlinePlayers()) {
            registerUser(p);
        }
        Mantle.log.info("=== End of Mass Registration ===");
    }
}