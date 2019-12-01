package gb.Jack.mantle.warps;

import gb.Jack.mantle.Mantle;
import gb.Jack.mantle.utils.Maths;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Warps {
    private Map<String, Warp> warps = new HashMap<>();

    public void setupWarps(Player user) {
        try {
            warps = Mantle.db.getWarps();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (user != null) {
            user.sendMessage(ChatColor.GREEN + "Warps Reloaded");
        }
    }

    public Map<String, Warp> getWarps() { return warps; }

    public Warp getWarp(String name) {
        return warps.getOrDefault(name, null);
    }

    public Integer getPages() {
        return Maths.calTotalPages(warps.size(), Mantle.cfg.getInt("Warps.Per-Page"));
    }
}