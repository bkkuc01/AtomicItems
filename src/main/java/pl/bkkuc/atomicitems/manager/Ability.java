package pl.bkkuc.atomicitems.manager;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.bkkuc.atomicitems.Plugin;
import pl.bkkuc.atomicitems.listeners.PlayerListener;
import pl.bkkuc.atomicitems.tools.ColorUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// cringe class
public class Ability {

    private static void sendNearBy(Player player, int radius, String msg){
        List<Player> players = new ArrayList<>(player.getLocation().getNearbyPlayers(radius));
        players.forEach(p -> p.sendMessage(ColorUtility.color(msg)));
    }

    public static void startAbility(Player player, String type) {

        int manos = Plugin.getInstance().getPlayerPoints().getAPI().look(player.getUniqueId());
        int damage = manos / 6;

        switch (type.toLowerCase()) {
            case "gojo_blue":
                sendNearBy(player, 20, "&7" + player.getName() + "&8: &9Проклятая техника: Синий!");
                runBlueAbility(player, damage);
                break;
            case "gojo_red":
                sendNearBy(player, 20, "&7" + player.getName() + "&8: &cПроклятая техника: Красный!");
                runRedAbility(player, damage);
                break;
            case "gojo_purple":
                sendNearBy(player, 20, "&7" + player.getName() + "&8: &dТехника пустоты: Пурпурный");
                runPurpleAbility(player, damage);
                break;
            case "gojo_hollow_purple":
                sendNearBy(player, 20, "&7" + player.getName() + "&8: &5Техника пустоты: Фиолетовый!");
                runHollowPurpleAbility(player, damage);
                break;
            default:
                break;
        }
    }

    private static void runBlueAbility(Player player, int damage) {
        player.getWorld().playSound(eye(player), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        new BukkitRunnable() {
            int ticks = 0;
            final int duration = 170;

            @Override
            public void run() {
                try {
                    Player actuallyPlayer = Bukkit.getPlayer(player.getName());
                    if (actuallyPlayer == null || ticks >= duration) {
                        cancel();
                        return;
                    }

                    spawnSphere(player, Color.BLUE);

                    List<Entity> nearby = eye(player).getNearbyEntities(10, 10, 10).stream().filter(s -> !s.equals(player)).collect(Collectors.toList());

                    if (!nearby.isEmpty()) {
                        for (Entity entity : nearby) {
                            Vector direction = eye(player).toVector().subtract(entity.getLocation().toVector()).normalize();
                            entity.setVelocity(direction.multiply(0.5));
                            if(entity instanceof LivingEntity) {
                                LivingEntity livingEntity = (LivingEntity) entity;
                                livingEntity.damage(Math.max(damage, 2.4));
                            }
                        }
                    }
                    ticks++;
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 2L);
    }

    private static void runRedAbility(Player player, int damage) {
        PlayerListener.push.add(player);

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(eye(player), EntityType.ARMOR_STAND);

        armorStand.setVisible(false);
        armorStand.setGravity(false);

        new BukkitRunnable() {
            boolean isPushed = false;

            @Override
            public void run() {
                if (Bukkit.getPlayer(player.getName()) == null || armorStand.isDead()) {
                    if (!armorStand.isDead()) armorStand.remove();
                    cancel();
                    return;
                }
                if (!PlayerListener.push.contains(player)) {
                    spawnSphere(armorStand.getLocation(), Color.RED);
                    if (!isPushed) {
                        isPushed = true;
                        armorStand.setGravity(true);
                        armorStand.setVelocity(eye(player).getDirection().multiply(10));
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        List<Entity> nearby = armorStand.getNearbyEntities(7, 7, 7).stream()
                                .filter(s -> !s.equals(player) && s instanceof LivingEntity)
                                .collect(Collectors.toList());

                        if (!nearby.isEmpty()) {
                            for (Entity entity : nearby) {
                                LivingEntity livingEntity = (LivingEntity) entity;
                                livingEntity.damage(Math.max(damage, 20.3));
                            }
                        }
                        new BukkitRunnable() {
                            int duration = 20;

                            @Override
                            public void run() {
                                if (duration <= 0) {
                                    if (!armorStand.isDead()) armorStand.remove();
                                    cancel();
                                    return;
                                }
                                List<Entity> nearby = armorStand.getNearbyEntities(7, 7, 7).stream()
                                        .filter(s -> !s.equals(player) && s instanceof LivingEntity)
                                        .collect(Collectors.toList());

                                if (!nearby.isEmpty()) {
                                    for (Entity entity : nearby) {
                                        LivingEntity livingEntity = (LivingEntity) entity;
                                        livingEntity.damage(Math.max(damage, 20.3));
                                    }
                                }
                                duration--;
                            }
                        }.runTaskTimer(Plugin.getInstance(), 0L, 1L);

                    }
                } else {
                    spawnSphere(player, Color.RED);
                    armorStand.teleport(eye(player));
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 2L);
    }

    private static void runPurpleAbility(Player player, int damage) {
        runSphereAbility(player, damage);
    }

    private static void runHollowPurpleAbility(Player player, int damage) {
        runSphereAbility(player, damage);
    }

    private static void runSphereAbility(Player player, int damage) {

        Location playerLocation = player.getEyeLocation();
        Vector direction = playerLocation.getDirection();

        ArmorStand sphere = createBulletStand(playerLocation, direction);
        sphere.addScoreboardTag("gojo_purple");

        new BukkitRunnable() {
            @Override
            public void run() {
                handleBulletTick(sphere, null, damage, this);
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 1L);

    }

    private static void handleBulletTick(ArmorStand bullet, ArmorStand collision, int damage, BukkitRunnable task) {
        if (bullet.isDead()) {
            task.cancel();
            return;
        }

        List<Entity> nearby = bullet.getNearbyEntities(2, 2, 2).stream()
                .filter(e -> e instanceof LivingEntity)
                .collect(Collectors.toList());

        if (!nearby.isEmpty()) {
            for (Entity entity : nearby) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.damage(Math.max(damage, 2.4));
            }
            bullet.remove();
            task.cancel();
        }

        if (collision != null && !collision.isOnGround()) {
            bullet.remove();
            task.cancel();
        } else if (collision != null) {
            handleCollisionGround(collision, bullet.getLocation().getBlock());
        }
    }

    private static void handleCollisionGround(ArmorStand collision, Block block) {
        if (Math.random() < 0.3 && !block.getType().equals(Material.BEDROCK)) {
            block.breakNaturally();
        }
    }

    private static ArmorStand createBulletStand(Location location, Vector velocity) {
        ArmorStand bullet = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        bullet.setMetadata("atomicitems", new FixedMetadataValue(Plugin.getInstance(), "value"));
        bullet.setVisible(false);
        bullet.setGravity(true);
        bullet.setAI(false);
        bullet.setCollidable(false);
        bullet.setInvulnerable(true);
        bullet.setVelocity(velocity.multiply(1.5));
        bullet.setGlowing(true);
        return bullet;
    }

    private static void particle(Location location, Color color) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        new ParticleBuilder(Particle.REDSTONE).count(1).data(dustOptions).location(location).spawn();
    }

    private static void spawnSphere(Player player, Color color) {


        Location spawnLocation = eye(player);

        spawnSphere(spawnLocation, color);
    }

    private static void spawnSphere(Location location, Color color){
        double radius = 1.0;
        int points = 20;
        double increment = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            for (int j = 0; j < points; j++) {
                double angleX = i * increment;
                double angleY = j * increment;

                double x = radius * Math.sin(angleX) * Math.cos(angleY);
                double y = radius * Math.sin(angleX) * Math.sin(angleY);
                double z = radius * Math.cos(angleX);

                Location particleLocation = location.clone().add(x, y, z);
                particle(particleLocation, color);
            }
        }
    }

    private static Location eye(Player player){
        Vector direction = player.getEyeLocation().getDirection().normalize();
        return player.getEyeLocation().add(direction.multiply(8));
    }
}