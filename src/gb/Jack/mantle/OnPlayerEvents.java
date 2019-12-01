package gb.Jack.mantle;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class OnPlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { Mantle.users.registerUser(event.getPlayer()); }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Mantle.users.unregisterUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (Mantle.cfg.getBoolean("Chorus-Fruit.Enable-Effect-Chances")) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) {
                int random = new Random().nextInt(100) + 1;

                if (random > Mantle.cfg.getInt("Chorus-Fruit.Teleport-Chance")) {
                    event.setCancelled(true);

                    random = new Random().nextInt(100) + 1;
                    int chanceTotal = 0;

                    Player player = event.getPlayer();
                    PotionEffect effect = null;

                    ConfigurationSection cfgSec = Mantle.cfg.getConfigurationSection("Chorus-Fruit.Effects");

                    if (cfgSec != null) {
                        for (String eff : cfgSec.getKeys(false)) {
                            int effChance = Mantle.cfg.getInt("Chorus-Fruit.Effects." + eff + ".Chance");
                            int duration = Mantle.cfg.getInt("Chorus-Fruit.Effects." + eff + ".Duration");
                            int amp = Mantle.cfg.getInt("Chorus-Fruit.Effects." + eff + ".Amplifier");

                            if (random <= (effChance + chanceTotal)) {
                                PotionEffectType type = PotionEffectType.getByName(eff);

                                if (type != null) {
                                    effect = new PotionEffect(type, (duration * 20), amp);
                                } else {
                                    Mantle.log.info("An effect would have been applied to " + player.getName() + " but the effect " + eff + " could not be found");
                                }

                                break;
                            }

                            chanceTotal += effChance;
                        }
                    }

                    if (effect != null) {
                        effect.apply(player);
                    }

                    player.sendMessage(ChatColor.DARK_PURPLE + "This fruit tastes funny...");
                }
            }
        }
    }
}