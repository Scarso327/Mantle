package gb.Jack.mantle;

import gb.Jack.mantle.groups.Group;
import gb.Jack.mantle.users.User;
import gb.Jack.mantle.warps.Warp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {

    private Connection conn;

    public String prefix = Mantle.cfg.getString("Database.Prefix");

    public Connection getConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            return conn;
        }

        return connectToDatabase();
    }

    private Connection connectToDatabase() throws SQLException {
        try {
            if (!isJDBCAvailable()) {
                throw new SQLException("JDBC Unavailable");
            }

            FileConfiguration cfg = Mantle.cfg;

            return DriverManager.getConnection(
                    "jdbc:mysql://" + cfg.getString("Database.Address") + ":" + cfg.getString("Database.Port") + "/" + cfg.getString("Database.Database"),
                    cfg.getString("Database.Username"),
                    cfg.getString("Database.Password")
            );
        } catch (SQLException e) {
            throw e;
        }
    }

    private boolean isJDBCAvailable() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void initialDBSetup(Connection db) throws SQLException {
        PreparedStatement smt = null;

        try {
            // Create "Users" Table...
            smt = db.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `" + prefix + "users` (\n" +
                "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
                "\t`user` VARCHAR(40) NOT NULL,\n" +
                "\t`uuid` VARCHAR(36) NULL DEFAULT NULL,\n" +
                "\t`groupid` INT(11) NOT NULL DEFAULT '-1',\n" +
                "\t`balance` INT(100) NOT NULL DEFAULT '0',\n" +
                "\t`lastlogin` INT(32) UNSIGNED NOT NULL,\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `uuid` (`uuid`),\n" +
                "\tINDEX `user` (`user`(20))\n" +
                ")\n" +
                "COLLATE='latin1_swedish_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";"
            );

            smt.executeUpdate();
            smt.close();

            smt = db.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `" + prefix + "warps` (\n" +
                "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
                "\t`world` VARCHAR(50) NOT NULL,\n" +
                "\t`name` VARCHAR(32) NOT NULL,\n" +
                "\t`owner` INT(11) NOT NULL DEFAULT '-1',\n" +
                "\t`password` VARCHAR(8) NOT NULL,\n" +
                "\t`x` FLOAT NOT NULL DEFAULT '0',\n" +
                "\t`y` FLOAT NOT NULL DEFAULT '0',\n" +
                "\t`z` FLOAT NOT NULL DEFAULT '0',\n" +
                "\t`pitch` FLOAT NOT NULL DEFAULT '0',\n" +
                "\t`yaw` FLOAT NOT NULL DEFAULT '0',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `name` (`name`),\n" +
                "\tINDEX `owner` (`owner`),\n" +
                "\tINDEX `world` (`world`)\n" +
                ")\n" +
                "COLLATE='latin1_swedish_ci'\n" +
                "ENGINE=INNODB\n" +
                ";"
            );

            smt.executeUpdate();
            smt.close();

            smt = db.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `" + prefix + "groups` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "\t`userID` INT(11) NOT NULL,\n" +
                "\t`tag` VARCHAR(4) NOT NULL,\n" +
                "\t`name` VARCHAR(14) NOT NULL,\n" +
                "\t`password` VARCHAR(16) NOT NULL,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "ENGINE=InnoDB\n" +
                ";"
            );

            smt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public int getUserID(UUID uid) throws SQLException {
        PreparedStatement smt = null;

        try {
            Connection db = getConnection();

            if (uid == null) {
                throw new SQLException("No UUID Provided");
            }

            smt = db.prepareStatement("SELECT id FROM " + prefix + "users WHERE uuid = ? LIMIT 1");
            smt.setString(1, uid.toString());
            ResultSet result = smt.executeQuery();

            if (result.next()) {
                return result.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert smt != null;
            smt.close();
        }

        return -1;
    }

    // Inserts new player and returns the database ID...
    public int newUser(UUID uid, String name) throws SQLException {
        PreparedStatement smt = null;

        try {
            Connection db = getConnection();

            if (uid == null) {
                throw new SQLException("No UUID Provided");
            }

            if (getUserID(uid) != -1) {
                throw new SQLException("User Already Inserted");
            }

            smt = db.prepareStatement("INSERT into " + prefix + "users (user, uuid, balance, lastlogin) VALUES (?, ?, ?, UNIX_TIMESTAMP())", Statement.RETURN_GENERATED_KEYS);

            smt.setString(1, name);
            smt.setString(2, uid.toString());

            smt.setInt(3, Mantle.cfg.getInt("Banking.Default-Balance"));

            smt.executeUpdate();

            ResultSet keys = smt.getGeneratedKeys();

            if (!keys.next()) {
                Mantle.log.info("Unable to insert " + name + " into the database");
                return -1;
            }

            return keys.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public User getUserSettings(User u) throws SQLException {
        PreparedStatement smt = null;
        int id = u.getDbID();

        if (id == -1) {
            throw new SQLException("The user id was passed as -1 when gathering settings");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("SELECT balance, groupid FROM " + prefix + "users WHERE id = ? LIMIT 1");
            smt.setInt(1, id);
            ResultSet result = smt.executeQuery();

            if (result.next()) {
                u.setBalance(result.getInt("balance"));

                int groupid = result.getInt("groupid");

                if (groupid != -1) {
                    u.setGroupID(groupid);

                    Group g = Mantle.groups.getGroup(groupid);

                    if (g == null) {
                        g = getGroupFromDB(groupid);
                        Mantle.groups.registerGroup(g);
                    } else {
                        Mantle.log.info(u.getPlayerObject().getUniqueId() + " has been added to an already registered group");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert smt != null;
            smt.close();
        }

        return u;
    }

    public void saveUserSettings(User u) throws SQLException {
        PreparedStatement smt = null;
        int id = u.getDbID();

        if (id == -1) {
            throw new SQLException("The user id was passed as -1 when saving settings");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("UPDATE " + prefix + "users SET balance = ?, groupid = ? WHERE id = ? OR uuid = ? LIMIT 1");

            smt.setInt(1, u.getBalance());
            smt.setInt(2, u.getGroupID());
            smt.setInt(3, id);
            smt.setString(4, u.getPlayerObject().getUniqueId().toString());

            int effectedRows = smt.executeUpdate();

            if (effectedRows < 1) {
                throw new SQLException("[Settings] No Rows Altered for ID: " + id);
            }
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public Group getGroupFromDB(int gID) throws SQLException {
        PreparedStatement smt = null;
        Group g = null;

        if (gID == -1) {
            throw new SQLException("The group id was passed as -1 when gathering settings");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("SELECT userID, tag, name, password FROM " + prefix + "groups WHERE id = ? LIMIT 1");
            smt.setInt(1, gID);
            ResultSet result = smt.executeQuery();

            if (result.next()) {
                g = new Group();

                g.setDbID(gID);
                g.setName(result.getString("name"));
                g.setTag(result.getString("tag"));
                g.setOwnerID(result.getInt("userID"));

                String pw = result.getString("password");

                assert pw != null;

                if (!pw.equals("")) {
                    g.setPassword(pw);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert smt != null;
            smt.close();
        }

        return g;
    }

    public int createGroup(int ownerID, String tag, String name, String pw) throws SQLException {
        PreparedStatement smt = null;

        try {
            if (isGroupTagTaken(tag) || isGroupNameTaken(name)) {
                throw new SQLException("Group Name or Tag Already Inserted");
            }

            Connection db = getConnection();

            smt = db.prepareStatement("INSERT into " + prefix + "groups (userID, tag, name, password, creation) VALUES (?, ?, ?, ?, UNIX_TIMESTAMP())", Statement.RETURN_GENERATED_KEYS);

            smt.setInt(1, ownerID);
            smt.setString(2, tag);
            smt.setString(3, name);
            smt.setString(4, pw);

            smt.executeUpdate();

            ResultSet keys = smt.getGeneratedKeys();

            if (!keys.next()) {
                throw new SQLException("Unable to insert group " + name + " into the database");
            }

            return keys.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public void deleteGroup(int id) throws SQLException {
        PreparedStatement smt = null;

        if (id == -1) {
            throw new SQLException("The group id was passed as -1 when deleting the group");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("DELETE From " + prefix + "groups WHERE id = ?");
            smt.setInt(1, id);

            int effectedRows = smt.executeUpdate();

            if (effectedRows < 1) {
                throw new SQLException("[deleteGroup] No Rows Altered for ID: " + id);
            } else {
                wipeGroupIDFromMembers(id);
            }
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    private void wipeGroupIDFromMembers(int id) throws SQLException {
        PreparedStatement smt = null;

        if (id == -1) {
            throw new SQLException("The group id was passed as -1 when wiping group id from members");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("UPDATE " + prefix + "users SET groupid = -1 WHERE groupid = ?");
            smt.setInt(1, id);
            smt.executeUpdate();
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public boolean isGroupNameTaken(String name) {
        PreparedStatement smt = null;

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("SELECT `name` FROM " + prefix + "groups WHERE `name` = ?");
            smt.setString(1, name);

            ResultSet result = smt.executeQuery();

            if (result.next()) {
                return true;
            }

            smt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume it's taken, just in case.
        }

        return false;
    }

    public boolean isGroupTagTaken(String tag) {
        PreparedStatement smt = null;

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("SELECT tag FROM " + prefix + "groups WHERE tag = ?");
            smt.setString(1, tag);

            ResultSet result = smt.executeQuery();

            if (result.next()) {
                return true;
            }

            smt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume it's taken, just in case.
        }

        return false;
    }

    /*
     ** Sets the Last Login timestamp as well as updates the username to ensure they are updated.
     */
    public void setLastLogin(int id, Player object) throws SQLException {
        PreparedStatement smt = null;

        if (id == -1) {
            throw new SQLException("The user id was passed as -1 when updating last login");
        }

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("UPDATE " + prefix + "users SET user = ?, lastlogin = UNIX_TIMESTAMP() WHERE id = ? OR uuid = ? LIMIT 1");

            smt.setString(1, object.getName());
            smt.setInt(2, id);
            smt.setString(3, object.getUniqueId().toString());

            int effectedRows = smt.executeUpdate();

            if (effectedRows < 1) {
                throw new SQLException("[LastLogin] No Rows Altered for ID: " + id);
            }
        } finally {
            assert smt != null;
            smt.close();
        }
    }

    public Map<String, Warp> getWarps() throws SQLException {
        PreparedStatement smt = null;
        Map<String, Warp> warps = new HashMap<>();

        try {
            Connection db = getConnection();

            smt = db.prepareStatement("SELECT * FROM " + prefix + "warps ORDER By ID");
            ResultSet result = smt.executeQuery();

            while (result.next()) {
                Warp w = new Warp();

                w.setID(result.getInt("id"));
                w.setName(result.getString("name"));
                w.setWorld(result.getString("world"));
                w.setLocation(
                    result.getFloat("x"),
                    result.getFloat("y"),
                    result.getFloat("z"),
                    result.getFloat("pitch"),
                    result.getFloat("yaw")
                );
                w.setPassword(result.getString("password"));

                warps.put(w.getName(), w);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            assert smt != null;
            smt.close();
        }

        return warps;
    }
}