package gb.Jack.mantle.users;

import gb.Jack.mantle.Mantle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class User {

    private Player playerObject;
    private Integer dbID = -1;
    private Integer groupID = -1;

    private Integer balance = Mantle.cfg.getInt("Banking.Default-Balance");

    public void setPlayerObject(Player player) {
        playerObject = player;
    }

    public Player getPlayerObject() {
        return playerObject;
    }

    public void setBalance(int bal) {
        balance = bal;
    }
    public Integer getBalance() {
        return balance;
    }

    public void setDbID(int id) { dbID = id; }
    public Integer getDbID() { return  dbID; }

    public void setGroupID(int id) { groupID = id; }
    public Integer getGroupID() { return  groupID; }
    public boolean inGroup() { return (groupID != -1); }

    // Error handling for teleporting...
    public boolean warp(Location loc) {
        try {
            if (playerObject.isInsideVehicle()) {
                playerObject.leaveVehicle();
            }

            playerObject.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}