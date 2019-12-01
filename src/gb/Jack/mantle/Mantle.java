package gb.Jack.mantle;

import gb.Jack.mantle.groups.Groups;
import gb.Jack.mantle.users.Users;
import gb.Jack.mantle.warps.Warps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Mantle extends JavaPlugin {

    public static Mantle object;
    public static Database db;

    public static final Logger log = Logger.getLogger(Mantle.class.getCanonicalName());
    public static final Warps warps = new Warps();
    public static final Users users = new Users();
    public static final Groups groups = new Groups();
    public static Scoreboard scoreboard = null;

    public static FileConfiguration cfg;

    @Override
    public void onEnable() {
        //noinspection ConstantConditions
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        this.saveDefaultConfig();
        cfg = this.getConfig();

        // There shouldn't be any teams at this stage...
        if (scoreboard.getTeams().size() > 0 && cfg.getBoolean("Groups.Use-Team-System")) {
            Groups.unregisterAllTeams();
        }

        try {
            db = new Database();
            db.initialDBSetup(db.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        object = this;

        warps.setupWarps(null);

        // Check to see if anyone is on the server currently, if they are we just call to get their info setup...
        if (Bukkit.getOnlinePlayers().size() > 0) {
            users.setupUsers();
        }

        Commands.setupCommands();

        getServer().getPluginManager().registerEvents(new OnPlayerEvents(), this);
    }

    @Override
    public void onDisable() {
        users.saveAllUsers();

        try {
            Connection conn = db.getConnection();

            if (db != null && !conn.isClosed()){
                conn.close();
            }

            db = null;
        } catch(Exception e) {
            e.printStackTrace();
        }

        Groups.unregisterAllTeams();
    }
}