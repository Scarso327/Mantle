package gb.Jack.mantle.warps;

import gb.Jack.mantle.Mantle;
import org.bukkit.Location;
import org.bukkit.World;

public class Warp {

    private Integer id;
    private String name;

    private String password;
    private boolean whitelisted;

    // Location Data
    private String world;
    private Float x;
    private Float y;
    private Float z;
    private Float pitch;
    private Float yaw;

    public void setID(Integer newID) { id = newID; }
    public Integer getID() { return id; }

    public void setName(String newName) { name = newName; }
    public String getName() { return name; }

    public void setWorld(String w) { world = w; }
    public World getWorld() { return Mantle.object.getServer().getWorld(world); }

    public void setLocation(float xI, float yI, float zI, float pitchI, float yawI) {
        x = xI;
        y = yI;
        z = zI;
        pitch = pitchI;
        yaw = yawI;
    }

    public Location getLocation() {
        World w = getWorld();

        if (w == null) {
            Mantle.log.info("Warp: " + getName() + ", returned a null world. Does the world exist?");
            return null;
        }

        return new Location(w, x, y, z, pitch, yaw);
    }

    public boolean isPassworded() { return !password.equals(""); }
    public boolean isWhitelisted() { return whitelisted; }

    public void setPassword(String pw) { password = pw; }
    public boolean isPassword(String pw) { return password.equals(pw); }

    public void setWhitelisted(boolean whitelist) { whitelisted = whitelist; }

    public String getAccessString() {
        String str = isPassworded() || isWhitelisted() ? " [PRIVATE]" : " [PUBLIC]";

        return str;
    }
}
