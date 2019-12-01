package gb.Jack.mantle.groups;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.utils.Maths;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Groups {
    private Map<Integer, Group> groups = new HashMap<>();

    public void registerGroup(Group group) {
        int id = group.getDbID();
        String name = group.getName();

        if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
            Team team = Mantle.scoreboard.getTeam(name);

            if (team == null) {
                team = Mantle.scoreboard.registerNewTeam(name);
            }

            team.setPrefix("[" + group.getTag() + "] ");
            group.setTeam(team);
        }

        groups.put(id, group);
        Mantle.log.info("Registering Group: " + id);
    }

    public void unregisterGroup(int id) {
        if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
            Group g = Mantle.groups.getGroup(id);

            if (g != null) {
                g.getTeam().unregister();
            }
        }

        groups.remove(id);
        Mantle.log.info("Unregistered Group: " + id);
    }

    // Return a specific user...
    public Group getGroup(int id) {
        return groups.getOrDefault(id, null);
    }

    public Group getGroup(String name) {
        Group g = null;

        for (Group group : new ArrayList<>(groups.values())) {
            if (group.getName().toLowerCase().equals(name.toLowerCase())) {
                g = group;
                break;
            }
        }

        return g;
    }

    public Map<Integer, Group> getGroups() { return groups; }

    public Integer getPages() {
        return Maths.calTotalPages(groups.size(), Mantle.cfg.getInt("Groups.Per-Page"));
    }

    public static void unregisterAllTeams() {
        if (Mantle.cfg.getBoolean("Groups.Use-Team-System")) {
            for (Team team : Mantle.scoreboard.getTeams()) {
                team.unregister();
            }
        }
    }
}