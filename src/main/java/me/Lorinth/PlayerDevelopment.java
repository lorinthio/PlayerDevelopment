package me.Lorinth;

import me.fromgate.playeffect.PlayEffect;
import me.fromgate.playeffect.VisualEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class PlayerDevelopment extends JavaPlugin implements Listener {

    public static PlayerDevelopment instance = null;
    public ArrayList<String> dodgedPlayers = new ArrayList<String>();
    public ArrayList<String> invulPlayers = new ArrayList<String>();
    public ArrayList<String> sentinelPlayers = new ArrayList<String>();
    public ArrayList<String> jumpPlayers = new ArrayList<String>();
    public ArrayList<String> burningBlood = new ArrayList<String>();
    public ArrayList<String> meteorStrike = new ArrayList<String>();
    public ArrayList<String> vanishedPlayers = new ArrayList<String>();
    public ArrayList<String> specPlayers = new ArrayList<String>();
    public Plugin plugin = this;
    private File Players;
    private YamlConfiguration playersConfig;
    private File Skills;
    private YamlConfiguration skillsConfig;
    private Logger log = Logger.getLogger("Minecraft");
    private boolean bonustime = false;
    private Random random = new Random();
    private File Parties;
    private YamlConfiguration partyConfig;
    private File cooldown;
    private YamlConfiguration cooldownconfig;
    private File checkpoint;
    private YamlConfiguration checkpointConfig;
    private File graveyard;
    private YamlConfiguration graveyardConfig;
    private File CustomGear;
    private YamlConfiguration gearConfig;
    private boolean gearExists = true;
    private boolean partyExists = true;

    public static PlayerDevelopment getInstance() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerDevelopment");
        if (plugin == null || !(plugin instanceof PlayerDevelopment)) {
            throw new RuntimeException("'PlayerDevelopment' not found. 'PlayerDevelopment' plugin disabled?");
        }
        return ((PlayerDevelopment) plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rangedSkills(EntityDamageByEntityEvent event) {
        try {
            Entity damage = event.getDamager();
            String skillname = "";
            if (damage instanceof Arrow) {
                try {
                    skillname = damage.getMetadata("skillname").get(0).asString();
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
                Entity shooter = (Entity) ((Arrow) damage).getShooter();
                if (shooter instanceof Player) {
                    if (!(event.getEntity() instanceof LivingEntity)) {
                        return;
                    }
                    final LivingEntity target = (LivingEntity) event.getEntity();
                    final Player player = (Player) shooter;
                    if (skillname.contentEquals("power shot 1")) {
                        Integer dex = player.getMetadata("Dexterity").get(0).asInt();
                        Integer Damage = dex / 4;
                        if (Damage > 4) {
                            Damage = 4;
                        }
                        Integer result = DealDamage(damage, target, Damage, "ranged");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("num", "20");
                        params.put("speed", "0.5");
                        PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
                        if (target instanceof Player) {
                            player.sendMessage("Power Shot dealt " + result + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Power Shot dealt " + result + " damage to " + target.getType().getName());
                        }
                    }
                    if (skillname.contentEquals("poison shot")) {
                        Integer dex = player.getMetadata("Dexterity").get(0).asInt();
                        Integer PoisonDamagestart = dex / 25;
                        if (PoisonDamagestart > 2 && dex < 100) {
                            PoisonDamagestart = 2;
                        }
                        final Integer PoisonDamage = PoisonDamagestart;
                        Integer result = DealDamage(damage, target, 0, "ranged");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("param", "4");
                        PlayEffect.play(VisualEffect.POTION, target.getLocation(), params);
                        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            @Override
                            public void run() {
                                if (target instanceof Player) {
                                    ((Player) target).setHealth(((Player) target).getHealth() - PoisonDamage);
                                } else {
                                    ((Damageable) target).setHealth(((Damageable) target).getHealth() - PoisonDamage);
                                }
                            }

                        }, (1 * 20));

                        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            @Override
                            public void run() {
                                if (target instanceof Player) {
                                    ((Player) target).setHealth(((Player) target).getHealth() - PoisonDamage);
                                } else {
                                    ((Damageable) target).setHealth(((Damageable) target).getHealth() - PoisonDamage);
                                }
                            }

                        }, (2 * 20));

                        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            @Override
                            public void run() {
                                if (target instanceof Player) {
                                    ((Player) target).setHealth(((Player) target).getHealth() - PoisonDamage);
                                } else {
                                    ((Damageable) target).setHealth(((Damageable) target).getHealth() - PoisonDamage);
                                }
                            }

                        }, (3 * 20));

                        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            @Override
                            public void run() {
                                if (target instanceof Player) {
                                    ((Player) target).setHealth(((Player) target).getHealth() - PoisonDamage);
                                } else {
                                    ((Damageable) target).setHealth(((Damageable) target).getHealth() - PoisonDamage);
                                }
                            }

                        }, (4 * 20));

                        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                            @Override
                            public void run() {
                                if (target instanceof Player) {
                                    ((Player) target).setHealth(((Player) target).getHealth() - PoisonDamage);
                                } else {
                                    ((Damageable) target).setHealth(((Damageable) target).getHealth() - PoisonDamage);
                                }
                            }

                        }, (5 * 20));
                        if (target instanceof Player) {
                            player.sendMessage("Poison Shot dealt " + result + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Poison Shot dealt " + result + " damage to " + target.getType().getName());
                        }
                    }
                    if (skillname.contentEquals("cripple shot")) {
                        Integer dex = player.getMetadata("Dexterity").get(0).asInt();
                        Integer Damage = dex / 6;
                        if (Damage >= 6) {
                            Damage = 5;
                        }
                        PotionEffect cripple = new PotionEffect(PotionEffectType.SLOW, 6, 2);
                        Integer result = DealDamage(damage, target, Damage, "ranged");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("param", "2");
                        PlayEffect.play(VisualEffect.POTION, target.getLocation(), params);
                        target.addPotionEffect(cripple);
                        if (target instanceof Player) {
                            player.sendMessage("Cripple Shot dealt " + result + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Cripple Shot dealt " + result + " damage to " + target.getType().getName());
                        }
                    }
                    if (skillname.contentEquals("sniper shot")) {
                        Integer Damage = 2 * ((player.getMetadata("Dexterity").get(0).asInt() / 20) + 3);
                        Integer result = DealDamage(damage, target, Damage, "ranged");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("num", "30");
                        params.put("speed", "1.0");
                        PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
                        if (target instanceof Player) {
                            player.sendMessage("Sniper Shot dealt " + result + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Sniper Shot dealt " + result + " damage to " + target.getType().getName());
                        }
                    }
                    if (skillname.contentEquals("teleport shot")) {
                        Integer Damage = DealDamage(damage, target, 0, "ranged");
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("type", "ENDERMAN_TELEPORT");
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("wind", "all");
                        Map<String, String> params3 = new HashMap<String, String>();
                        params3.put("num", "20");
                        params3.put("speed", "0.5");
                        PlayEffect.play(VisualEffect.SOUND, player.getLocation(), params);
                        PlayEffect.play(VisualEffect.PORTAL, player.getLocation(), params3);
                        player.teleport(target);
                        PlayEffect.play(VisualEffect.SOUND, player.getLocation(), params);
                        PlayEffect.play(VisualEffect.SMOKE, target.getLocation(), params);
                        if (target instanceof Player) {
                            player.sendMessage("Teleport Shot teleported you to, and dealt " + Damage + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Teleport Shot teleported you to, and dealt " + Damage + " damage to " + target.getType().getName());
                        }
                    }
                    damage.remove();
                    event.setDamage(0.1);
                }
            }
            if (damage instanceof Fireball) {
                try {
                    skillname = damage.getMetadata("skillname").get(0).asString();
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
                Entity shooter = (Entity) ((Fireball) damage).getShooter();
                if (shooter instanceof Player) {
                    final Entity target = event.getEntity();
                    final Player player = (Player) shooter;
                    if (skillname.contentEquals("Fireball1")) {
                        Integer Int = player.getMetadata("Intelligence").get(0).asInt();
                        Integer Damage = (Int / 4) + 3;
                        if (Damage > 7) {
                            Damage = 7;
                        }
                        Damage += player.getMetadata("MagicBonus").get(0).asInt();
                        DealDamage(player, target, Damage, "magic");
                        if (target instanceof Player) {
                            player.sendMessage("Fireball I, dealt " + Damage + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Fireball I, dealt " + Damage + " damage to " + target.getType().getName());
                        }
                        event.setDamage(0.1);
                        damage.remove();
                    }
                    if (skillname.contentEquals("Fireball2")) {
                        Integer Int = player.getMetadata("Intelligence").get(0).asInt();
                        Integer Damage = (Int / 5) + 4;
                        if (Damage > 10) {
                            Damage = 10;
                        }
                        Damage += player.getMetadata("MagicBonus").get(0).asInt();
                        DealDamage(player, target, Damage, "magic");
                        if (target instanceof Player) {
                            player.sendMessage("Fireball II, dealt " + Damage + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Fireball II, dealt " + Damage + " damage to " + target.getType().getName());
                        }
                        event.setDamage(0.1);
                        damage.remove();
                    }
                }
            }
            if (damage instanceof LightningStrike) {
                String Caster = "";
                try {
                    skillname = damage.getMetadata("skillname").get(0).asString();
                    Caster = damage.getMetadata("caster").get(0).asString();
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
                Entity shooter = Bukkit.getPlayer(Caster);
                if (shooter instanceof Player) {
                    final Entity target = event.getEntity();
                    final Player player = (Player) shooter;
                    if (skillname.contentEquals("Lightning Bolt")) {
                        Integer Int = player.getMetadata("Intelligence").get(0).asInt();
                        Integer Damage = random.nextInt((Int / 4) + 4) + 1;
                        if (Damage > 7) {
                            Damage = 7;
                        }
                        Damage += player.getMetadata("MagicBonus").get(0).asInt();
                        Integer MagicAttack = (Int / 2) + 14;
                        if (target instanceof Player) {
                            Integer MagicDefense = target.getMetadata("Wisdom").get(0).asInt();
                            MagicDefense = (MagicDefense / 2) + 14;
                            float MagicMod = (float) MagicAttack / (float) MagicDefense;
                            Damage = (int) (Damage * MagicMod);
                        }
                        ((Damageable) target).damage(Damage);
                        if (target instanceof Player) {
                            player.sendMessage("Fireball dealt " + Damage + " damage to " + ((Player) target).getName());
                        } else if (target instanceof Creature) {
                            player.sendMessage("Fireball dealt " + Damage + " damage to " + target.getType().getName());
                        }
                        damage.remove();
                    }
                }
            }
        } catch (NullPointerException e) {
            //none
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void arrowSkillsOnBlock(ProjectileHitEvent event) {
        try {
            String skillname = "";
            Entity damage = event.getEntity();
            if (damage instanceof Arrow) {
                try {
                    skillname = damage.getMetadata("skillname").get(0).asString();
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
                Entity shooter = (Entity) ((Arrow) damage).getShooter();
                if (shooter instanceof Player) {
                    final Location target = event.getEntity().getLocation();
                    final Player player = (Player) shooter;
                    if (skillname.contentEquals("teleport shot")) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("type", "ENDERMAN_TELEPORT");
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("wind", "all");
                        Map<String, String> params3 = new HashMap<String, String>();
                        params3.put("num", "20");
                        params3.put("speed", "0.5");
                        PlayEffect.play(VisualEffect.SOUND, player.getLocation(), params);
                        PlayEffect.play(VisualEffect.PORTAL, player.getLocation(), params3);
                        player.teleport(target);
                        PlayEffect.play(VisualEffect.SOUND, player.getLocation(), params);
                        PlayEffect.play(VisualEffect.SMOKE, target, params);
                        player.sendMessage("Teleport Shot teleported you to its location");
                        damage.remove();
                    }
                    event.getEntity().remove();
                }
            }
        } catch (NullPointerException e) {
            //none
        }
    }

    public void onEnable() {
        LoadConfig();
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                checkBonus();
            }
        }, 0, 20);
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                selfRegenEvent();
            }
        }, 0, 60);
        if (partyExists) {
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                public void run() {
                    partyConfig = YamlConfiguration.loadConfiguration(Parties);
                }
            }, 0, 120);
        }
    }

    public void LoadConfig() {
        this.checkpoint = new File(getDataFolder(), "Checkpoints.yml");
        this.checkpointConfig = YamlConfiguration.loadConfiguration(this.checkpoint);
        this.graveyard = new File(getDataFolder(), "Graveyards.yml");
        this.graveyardConfig = YamlConfiguration.loadConfiguration(this.graveyard);
        this.cooldown = new File(getDataFolder(), "Cooldowns.yml");
        this.cooldownconfig = YamlConfiguration.loadConfiguration(this.cooldown);
        this.Players = new File(getDataFolder(), "Players.yml");
        this.playersConfig = YamlConfiguration.loadConfiguration(this.Players);
        this.Skills = new File(getDataFolder(), "SkillBinds.yml");
        this.skillsConfig = YamlConfiguration.loadConfiguration(this.Skills);
        try {
            Plugin Gear = Bukkit.getPluginManager().getPlugin("LorinthsGear");
            this.CustomGear = new File(Gear.getDataFolder(), "Gear.yml");
            this.gearConfig = YamlConfiguration.loadConfiguration(this.CustomGear);
        } catch (NullPointerException e) {
            this.gearExists = false;
        }
        try {
            Plugin Party = Bukkit.getPluginManager().getPlugin("RpgParty");
            this.Parties = new File(Party.getDataFolder(), "Parties.yml");
            this.partyConfig = YamlConfiguration.loadConfiguration(this.Parties);
        } catch (NullPointerException e) {
            this.partyExists = false;
        }

        ArrayList<String> emptyList = new ArrayList<String>();
        emptyList.add("None");
        if (!this.Players.exists()) {
            this.playersConfig.set("Players", emptyList);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ItemBreak(PlayerItemBreakEvent e) {
        ItemStack item = e.getBrokenItem();
        if (item.hasItemMeta() && (item.getType().toString().toLowerCase().contains("sword") || item.getType().toString().toLowerCase().contains("axe") || item.getType().toString().toLowerCase().contains("bow") || item.getType().toString().toLowerCase().contains("boots") || item.getType().toString().toLowerCase().contains("chest") || item.getType().toString().toLowerCase().contains("helm") || item.getType().toString().toLowerCase().contains("leg"))) {
            item.setDurability((short) 1);
            item.getItemMeta().setDisplayName(item.getItemMeta().getDisplayName() + " (BROKEN)");
            e.getPlayer().getInventory().addItem(item);

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Boolean teleportloc = false;
        Location loc = player.getLocation().getWorld().getSpawnLocation();
        try {
            Double x = (Double) this.checkpointConfig.get(player.getName() + ".X");
            Double y = (Double) this.checkpointConfig.get(player.getName() + ".Y");
            Double z = (Double) this.checkpointConfig.get(player.getName() + ".Z");
            World world = null;
            float yaw = this.checkpointConfig.getInt(player.getName() + ".yaw");
            float pitch = this.checkpointConfig.getInt(player.getName() + ".pitch");
            try {
                world = Bukkit.getWorld(this.checkpointConfig.getString(player.getName() + ".world"));
            } catch (IllegalArgumentException error2) {
                //
            }
            loc = new Location(world, x, y, z, yaw, pitch);
            e.setRespawnLocation(loc);
            teleportloc = true;
        } catch (NullPointerException error) {
            try {
                Set<String> graveyards = this.graveyardConfig.getKeys(true);
                List<String> graves = new ArrayList<String>();
                for (String grave : graveyards) {
                    if (grave.toLowerCase().contains("x") || grave.toLowerCase().contains("y") || grave.toLowerCase().contains("z") || grave.toLowerCase().contains("yaw") || grave.toLowerCase().contains("pitch") || grave.toLowerCase().contains("world")) {
                        //pass
                    } else {
                        graves.add(grave);
                    }
                }
                double Distance = 0;
                String Name = "";
                Location playerloc = player.getLocation();
                for (String grave : graves) {
                    Double x = (Double) this.graveyardConfig.get(grave + ".X");
                    Double y = (Double) this.graveyardConfig.get(grave + ".Y");
                    Double z = (Double) this.graveyardConfig.get(grave + ".Z");
                    World world = Bukkit.getWorld((String) this.graveyardConfig.get(grave + ".world"));
                    Location testloc = new Location(world, x, y, z);
                    Double testDistance = playerloc.distanceSquared(testloc);
                    if (testDistance <= Distance | Distance == 0.0) {
                        Distance = testDistance;
                        Name = grave;
                    }
                }
                Double x = (Double) this.graveyardConfig.get(Name + ".X");
                Double y = (Double) this.graveyardConfig.get(Name + ".Y");
                Double z = (Double) this.graveyardConfig.get(Name + ".Z");
                World world = Bukkit.getWorld((String) this.graveyardConfig.get(Name + ".world"));
                float yaw = this.graveyardConfig.getInt(Name + ".yaw");
                float pitch = this.graveyardConfig.getInt(Name + ".pitch");
                loc = new Location(world, x, y, z, yaw, pitch);
                e.setRespawnLocation(loc);
                teleportloc = true;
            } catch (NullPointerException error2) {
                e.setRespawnLocation(player.getLocation().getWorld().getSpawnLocation());
            }
        }
    }

    public Integer DealDamage(Entity attacker, Entity target, Integer damage, String type) {
        boolean rangedAttack = false;
        boolean magicAttack = false;
        boolean sentinelDefense = false;
        float FullAttack = 0;
        float DamageReduction = 0;
        Integer resultdamage = damage;
        if (target instanceof Player) {
            Player targeted = (Player) target;
            if (this.dodgedPlayers.contains(targeted.getName())) {
                return 0;
            }
            if (this.sentinelPlayers.contains(((Player) target).getName())) {
                sentinelDefense = true;
            }
        }
        if (type == "ranged") {
            attacker = (Entity) ((Arrow) attacker).getShooter();
            rangedAttack = true;
        }
        if (type == "magic") {
            magicAttack = true;
        }
        if (rangedAttack && target instanceof Player) {
            if (target.getMetadata("Agility").get(0).asInt() >= 60) {
                Integer dodgearrow = target.getMetadata("Dodge").get(0).asInt();
                Integer dodgearrowchance = (Integer) random.nextInt(100);
                if (dodgearrowchance <= dodgearrow) {
                    if (attacker instanceof Player) {
                        ((Player) attacker).sendMessage(((Player) target).getName() + " evaded your arrow!");
                    }
                    ((Player) target).sendMessage("You evaded the arrow!");
                    return 0;
                }
            }
        }

        //Check attacker as player

        if (attacker instanceof Player && rangedAttack) {
            resultdamage = GetPlayerRangedAttackDamage((Player) attacker);
            FullAttack = ((attacker.getMetadata("Dexterity").get(0).asInt()) / 2) + 10;
        } else if (attacker instanceof Player && !rangedAttack && !magicAttack) {
            try {
                resultdamage = GetPlayerAttackDamage((Player) attacker);
                FullAttack = ((attacker.getMetadata("Strength").get(0).asInt()) / 2) + 10;
            } catch (NullPointerException e) {
                Integer minDam = 1;
                Integer maxDam = 4;
                FullAttack = ((attacker.getMetadata("Strength").get(0).asInt()) / 2) + 10;
                resultdamage = (Integer) (random.nextInt(maxDam - minDam) + minDam + (Integer) ((Player) attacker).getMetadata("MeleeBonus").get(0).value());
            }
            //Check Players Strength for the Knockback Passive "SMACK"
            if (attacker.getMetadata("Strength").get(0).asInt() >= 40) {
                Integer smack = random.nextInt(100);
                if (smack <= 3) {
                    Vector directionVector = target.getLocation().getDirection().normalize();
                    Vector playerVelocity = attacker.getVelocity();
                    playerVelocity.add(new Vector(0, 0.5, 0));
                    playerVelocity.add(directionVector.multiply(1.5));
                    target.setVelocity(playerVelocity);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("num", "60");
                    params.put("speed", "0.7");
                    PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
                    ((Player) attacker).sendMessage("SMACK");
                }
            }
        }
        //If Magic Attack!!! (Skills only possible)
        else if (attacker instanceof Player && !rangedAttack && magicAttack) {
            FullAttack = ((attacker.getMetadata("Intelligence").get(0).asInt()) / 2) + 10;
        }

        //Check attacker as monster
        else if (attacker instanceof Monster) {
            Integer mobLevel = getLevel(attacker);
            FullAttack = (mobLevel / 6) + 10;
        }
        //Check target

        if (target instanceof Player && !magicAttack) {
            DamageReduction = getPlayerDefense((Player) target);
            Integer dodge = target.getMetadata("Dodge").get(0).asInt();
            dodge = dodge / 2;
            Integer dodgechance = (Integer) random.nextInt(99);
            if (dodgechance <= dodge) {
                if (attacker instanceof Player) {
                    ((Player) attacker).sendMessage(((Player) target).getName() + " dodged your attack!");
                }
                ((Player) target).sendMessage("You dodged the attack!");
                ArrayList<String> DodgedPlayers = this.dodgedPlayers;
                this.dodgedPlayers.add(((Player) target).getName());
                this.dodgedPlayers = DodgedPlayers;
                final String targetnameis = ((Player) target).getName();
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    public void run() {
                        getInstance().dodgedPlayers.remove(targetnameis);
                    }
                }, 20);
                return 0;
            }
            float DamageMod = FullAttack / DamageReduction;
            if (DamageMod >= 1.5) {
                DamageMod = (float) 1.5;
            }
            if (DamageMod <= 0.25) {
                DamageMod = (float) 0.25;
            }
            resultdamage = (int) (DamageMod * resultdamage);
            if (target.getMetadata("Constitution").get(0).asInt() >= 80) {
                if (((Integer) (random.nextInt(100)) <= 10)) {
                    ArrayList<String> DodgedPlayers = this.invulPlayers;
                    this.invulPlayers.add(((Player) target).getName());
                    this.invulPlayers = DodgedPlayers;
                    final String targetnameis = ((Player) target).getName();
                    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                        public void run() {
                            getInstance().invulPlayers.remove(targetnameis);
                        }
                    }, 20);
                    ((Player) target).sendMessage("You blocked the attack!");
                    return 0;
                }
            }
        }
        if (target instanceof Player && magicAttack) {
            Integer dodge = target.getMetadata("Dodge").get(0).asInt();
            dodge = dodge / 3;
            Integer dodgechance = (Integer) random.nextInt(100);
            if (dodgechance <= dodge) {
                ((Player) target).sendMessage("You dodged " + ((Player) attacker).getName() + "'s spell!");
                ((Player) attacker).sendMessage(((Player) target).getName() + " has dodged your spell!");
                ArrayList<String> DodgedPlayers = this.dodgedPlayers;
                this.dodgedPlayers.add(((Player) target).getName());
                this.dodgedPlayers = DodgedPlayers;
                final String targetnameis = ((Player) target).getName();
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    public void run() {
                        getInstance().dodgedPlayers.remove(targetnameis);
                    }
                }, 20);
                return 0;
            }
            DamageReduction = target.getMetadata("MagicDefense").get(0).asInt();
            float DamageMod = FullAttack / DamageReduction;
            if (DamageMod >= 1.5) {
                DamageMod = (float) 1.5;
            }
            resultdamage = (int) (DamageMod * resultdamage);
        } else if (target instanceof Creature) {
            if (target instanceof Monster) {
                Integer mobLevel = getLevel(target);
                DamageReduction = (float) (mobLevel / 6) + 10;
            } else {
                Integer mobLevel = getLevel(target);
                DamageReduction = (float) (mobLevel / 10) + 10;
            }
            float DamageMod = FullAttack / DamageReduction;
            if (DamageMod >= 2.0) {
                DamageMod = (float) 2.0;
            }
            resultdamage = (int) (DamageMod * resultdamage);
        }
        if (sentinelDefense) {
            resultdamage = resultdamage / 2;
        }
        ((Damageable) target).damage(resultdamage);
        return resultdamage;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnHit(EntityDamageByEntityEvent event) {
        boolean rangedAttack = false;
        boolean sentinelDefense = false;
        float FullAttack = 0;
        float DamageReduction = 0;
        if (event.getCause().equals(DamageCause.FALL)) {
            if (event.getEntity() instanceof Player) {
                Player entity = (Player) event.getEntity();
                if (this.jumpPlayers.contains(entity.getName())) {
                    this.jumpPlayers.remove(entity.getName());
                    event.setCancelled(true);
                    return;
                }
            }
        }
        Integer resultdamage = (int) event.getDamage();
        Entity target = event.getEntity();
        Entity attacker = event.getDamager();
        if (target instanceof Player) {
            Player targeted = (Player) target;
            if (this.dodgedPlayers.contains(targeted.getName())) {
                event.setCancelled(true);
                return;
            }
            if (this.sentinelPlayers.contains(((Player) target).getName())) {
                sentinelDefense = true;
            }
        }
        if (attacker instanceof Arrow) {
            attacker = (Entity) ((Arrow) attacker).getShooter();
            rangedAttack = true;
        }
        if (rangedAttack && target instanceof Player) {
            try {
                if (target.getMetadata("Agility").get(0).asInt() >= 40) {
                    Integer dodgearrow = target.getMetadata("Dodge").get(0).asInt();
                    Integer dodgearrowchance = (Integer) random.nextInt(100);
                    if (dodgearrowchance <= dodgearrow) {
                        if (attacker instanceof Player) {
                            ((Player) attacker).sendMessage(((Player) target).getName() + " evaded your arrow!");
                        }
                        ((Player) target).sendMessage("You evaded the arrow!");
                        event.setCancelled(true);
                        return;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                // I'm an NPC
                return;
            }

        }

        //Check attacker as player

        if (attacker instanceof Player && rangedAttack) {
            resultdamage = GetPlayerRangedAttackDamage((Player) attacker);
            FullAttack = ((attacker.getMetadata("Dexterity").get(0).asInt()) / 2) + 10;
        } else if (attacker instanceof Player && !rangedAttack) {
            resultdamage = GetPlayerAttackDamage((Player) attacker);
            FullAttack = ((attacker.getMetadata("Strength").get(0).asInt()) / 2) + 10;
            //Check Players Strength for the Knockback Passive "SMACK"
            if (attacker.getMetadata("Strength").get(0).asInt() >= 30) {
                Integer smack = random.nextInt(100);
                if (smack <= 3) {
                    Vector directionVector = target.getLocation().getDirection().normalize();
                    Vector playerVelocity = attacker.getVelocity();
                    playerVelocity.add(new Vector(0, 0.5, 0));
                    playerVelocity.add(directionVector.multiply(1.5));
                    target.setVelocity(playerVelocity);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("num", "60");
                    params.put("speed", "0.7");
                    PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
                    ((Player) attacker).sendMessage("SMACK");
                }
            }
        }

        //Player Crit Check
        if (attacker instanceof Player) {
            Integer Critchance = random.nextInt(99) + 1;
            try {
                if (Critchance <= attacker.getMetadata("Critical").get(0).asInt()) {
                    if (attacker.getMetadata("Dexterity").get(0).asInt() >= 15) {
                        resultdamage = (int) (resultdamage * 1.6);
                    } else {
                        resultdamage = (int) (resultdamage * 1.5);
                    }
                    ((Player) attacker).sendMessage("Critical Hit!");
                }
            } catch (IndexOutOfBoundsException e) {
                //NPC catch?
            }
        }

        //Check attacker as monster
        else if (attacker instanceof Monster) {
            Integer mobLevel = getLevel(attacker);
            FullAttack = (mobLevel / 4) + 10;
        }
        //Check target

        if (target instanceof Player) {
            DamageReduction = getPlayerDefense((Player) target);
            Integer dodge = target.getMetadata("Dodge").get(0).asInt();
            Integer dodgechance = (Integer) random.nextInt(100);
            float DamageMod = FullAttack / DamageReduction;
            if (DamageMod >= 1.5) {
                DamageMod = (float) 1.5;
            }
            resultdamage = (int) (DamageMod * resultdamage);
            if (dodgechance <= dodge) {
                if (attacker instanceof Player) {
                    ((Player) attacker).sendMessage(((Player) target).getName() + " dodged your attack!");
                }
                ((Player) target).sendMessage("You dodged the attack!");
                ArrayList<String> DodgedPlayers = this.dodgedPlayers;
                this.dodgedPlayers.add(((Player) target).getName());
                this.dodgedPlayers = DodgedPlayers;
                final String targetnameis = ((Player) target).getName();
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    public void run() {
                        getInstance().dodgedPlayers.remove(targetnameis);
                    }
                }, 20);
                event.setCancelled(true);
                return;
            }
            if (target.getMetadata("Constitution").get(0).asInt() >= 50) {
                if (((Integer) (random.nextInt(100)) <= 10)) {
                    ArrayList<String> DodgedPlayers = this.invulPlayers;
                    this.invulPlayers.add(((Player) target).getName());
                    this.invulPlayers = DodgedPlayers;
                    final String targetnameis = ((Player) target).getName();
                    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                        public void run() {
                            getInstance().invulPlayers.remove(targetnameis);
                        }
                    }, 20);
                    ((Player) target).sendMessage("You blocked the attack!");
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (target instanceof Creature) {
            if (target instanceof Monster) {
                Integer mobLevel = getLevel(target);
                DamageReduction = (float) (mobLevel / 6) + 10;
            } else {
                Integer mobLevel = getLevel(target);
                DamageReduction = (float) (mobLevel / 10) + 10;
            }
            float DamageMod = FullAttack / DamageReduction;
            if (DamageMod >= 1.5) {
                DamageMod = (float) 1.5;
            }
            resultdamage = (int) (DamageMod * resultdamage);
        }
        if (sentinelDefense) {
            resultdamage = resultdamage / 2;
        }
        if (target instanceof Player && !rangedAttack) {
            event.setDamage(resultdamage);
        } else if (!(target instanceof Player) && !rangedAttack) {
            event.setDamage(resultdamage);
        } else if (target instanceof Player && rangedAttack) {
            ((Damageable) target).damage(resultdamage);
            event.setDamage(0);
            Arrow arrow = (Arrow) event.getDamager();
            arrow.remove();
        } else if (target instanceof Creature && rangedAttack) {
            ((Damageable) target).damage(resultdamage);
            event.setDamage(0);
            Arrow arrow = (Arrow) event.getDamager();
            arrow.remove();
        }
    }

    private float getPlayerDefense(Player target) {
        float Totaldefense = 0;
        ItemStack chest = target.getEquipment().getChestplate();
        ItemStack legs = target.getEquipment().getLeggings();
        ItemStack boots = target.getEquipment().getBoots();
        ItemStack helm = target.getEquipment().getHelmet();
        Integer CDef = 0;
        Integer LDef = 0;
        Integer BDef = 0;
        Integer HDef = 0;
        if (chest == null) {
            CDef = 0;
        } else {
            if (chest.hasItemMeta()) {
                String Cname = ChatColor.stripColor(chest.getItemMeta().getDisplayName());
                try {
                    CDef = (Integer) this.gearConfig.get("Armor." + Cname + ".Armor");
                } catch (NullPointerException error) {
                    target.sendMessage("Your chest piece is invalidly named, it is worthless unless you remove the custom name");
                }
            } else {
                if (chest.getType() == Material.LEATHER_CHESTPLATE) {
                    CDef = 1;
                }
                if (chest.getType() == Material.GOLD_CHESTPLATE) {
                    CDef = 2;
                }
                if (chest.getType() == Material.IRON_CHESTPLATE) {
                    CDef = 3;
                }
                if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
                    CDef = 5;
                }
            }
        }
        if (legs == null) {
            LDef = 0;
        } else {
            if (legs.hasItemMeta()) {
                String Lname = ChatColor.stripColor(legs.getItemMeta().getDisplayName());
                try {
                    LDef = (Integer) this.gearConfig.get("Armor." + Lname + ".Armor");
                } catch (NullPointerException error) {
                    ((Player) target).sendMessage("Your Leg piece is invalidly named, it is worthless unless you remove the custom name");
                }
            } else {
                if (legs.getType() == Material.LEATHER_LEGGINGS) {
                    LDef = 1;
                }
                if (legs.getType() == Material.GOLD_LEGGINGS) {
                    LDef = 2;
                }
                if (legs.getType() == Material.IRON_LEGGINGS) {
                    LDef = 2;
                }
                if (legs.getType() == Material.DIAMOND_LEGGINGS) {
                    LDef = 3;
                }
            }
        }
        if (boots == null) {
            BDef = 0;
        } else {
            if (boots.hasItemMeta()) {
                String Bname = ChatColor.stripColor(boots.getItemMeta().getDisplayName());
                try {
                    BDef = (Integer) this.gearConfig.get("Armor." + Bname + ".Armor");
                } catch (NullPointerException error) {
                    ((Player) target).sendMessage("Your Boots are invalidly named, it is worthless unless you remove the custom name");
                }
            } else {
                if (boots.getType() == Material.LEATHER_BOOTS) {
                    BDef = 1;
                }
                if (boots.getType() == Material.GOLD_BOOTS) {
                    BDef = 1;
                }
                if (boots.getType() == Material.IRON_BOOTS) {
                    BDef = 2;
                }
                if (boots.getType() == Material.DIAMOND_BOOTS) {
                    BDef = 3;
                }
            }
        }
        if (helm == null) {
            HDef = 0;
        } else {
            if (helm.hasItemMeta()) {
                String Hname = ChatColor.stripColor(helm.getItemMeta().getDisplayName());
                try {
                    HDef = (Integer) this.gearConfig.get("Armor." + Hname + ".Armor");
                } catch (NullPointerException error) {
                    ((Player) target).sendMessage("Your head piece is invalidly named, it is worthless unless you remove the custom name");
                }
            } else {
                if (helm.getType() == Material.LEATHER_HELMET) {
                    HDef = 1;
                } else if (helm.getType() == Material.GOLD_HELMET) {
                    HDef = 1;
                } else if (helm.getType() == Material.IRON_HELMET) {
                    HDef = 2;
                } else if (helm.getType() == Material.DIAMOND_HELMET) {
                    HDef = 2;
                }
            }
        }
        if (HDef == null) {
            HDef = 0;
        }
        if (CDef == null) {
            CDef = 0;
        }
        if (LDef == null) {
            LDef = 0;
        }
        if (BDef == null) {
            BDef = 0;
        }
        float Fulldefense = (float) (HDef + CDef + LDef + BDef);
        try {
            Totaldefense = ((target.getMetadata("Constitution").get(0).asInt()) / 2) + ((target.getMetadata("Constitution").get(0).asInt()) / 5) + Fulldefense + 10;
        } catch (IndexOutOfBoundsException e) {
            //I'm an NPC error catch
        }
        return Totaldefense;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        // check if the event has been cancelled by another plugin
        if (!e.isCancelled()) {
            HumanEntity ent = e.getWhoClicked();

            // not really necessary
            if (ent instanceof Player) {
                Player player = (Player) ent;
                Inventory inv = e.getInventory();

                // see if the event is about an anvil
                if (inv instanceof AnvilInventory) {
                    InventoryView view = e.getView();
                    int rawSlot = e.getRawSlot();

                    // compare the raw slot with the inventory view to make sure we are talking about the upper inventory
                    if (rawSlot == view.convertSlot(rawSlot)) {
                        /*
                slot 0 = left item slot
				slot 1 = right item slot
				slot 2 = result item slot

				see if the player clicked in the result item slot of the anvil inventory
						 */
                        if (rawSlot == 2) {
                            /*
                    get the current item in the result slot
					I think inv.getItem(rawSlot) would be possible too
							 */
                            ItemStack item = e.getCurrentItem();

                            // check if there is an item in the result slot
                            if (item != null) {
                                ItemMeta meta = item.getItemMeta();

                                // it is possible that the item does not have meta data
                                if (meta != null) {
                                    // see whether the item is being renamed
                                    if (meta.hasDisplayName()) {
                                        if (!player.hasPermission("PlayerDevelopment.Rename"))
                                            meta.setDisplayName("");
                                        player.sendMessage("You cannot rename items on this server");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRegenEvent(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    public void selfRegenEvent() {
        Player[] players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            Integer con = player.getMetadata("Constitution").get(0).asInt();
            Integer health = (int) player.getHealth();
            Integer maxhealth = (int) player.getMaxHealth();
            Integer wis = player.getMetadata("Wisdom").get(0).asInt();
            Integer intel = player.getMetadata("Intelligence").get(0).asInt();
            Integer mana = player.getMetadata("Mana").get(0).asInt();
            Integer maxmana = player.getMetadata("MaxMana").get(0).asInt();
            if (health < maxhealth) {
                Integer regen = 1;
                if (con >= 30) {
                    regen += 1;
                }
                if (this.burningBlood.contains(player.getName())) {
                    regen += 3;
                    if (con >= 40) {
                        regen += 2;
                    }
                }
                health += regen;
                if (health >= maxhealth) {
                    health = maxhealth;
                }
                player.setHealth(health);
            }
            if (!(mana >= maxmana)) {
                Integer regen = 1;
                if (wis >= 40) {
                    regen += 2;
                }
                if (intel >= 20) {
                    regen += 1;
                }
                mana += regen;
                if (mana >= maxmana) {
                    mana = maxmana;
                }
                player.setMetadata("Mana", new FixedMetadataValue(this, mana));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnItemUse(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            String type = event.getPlayer().getItemInHand().getType().toString();
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (this.skillsConfig.getConfigurationSection(player.getName() + "").getKeys(true).contains(type)) {
                    String skill = this.skillsConfig.getString(player.getName() + "." + type);
                    player.chat("/cast " + skill);
                }
            }
        } catch (NullPointerException e) {
            //Pass
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void CancelStarving(EntityDamageEvent event) {
        if (event.getCause().toString() == "STARVATION") {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings({"deprecation"})
    public void onPlayerCast(final Player player, String skill) {
        if (skill.toLowerCase().contentEquals("bash i")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".bash").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 5);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use Bash I on");
                return;
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer str = player.getMetadata("Strength").get(0).asInt();
            Integer Damage = (int) (str / 5);
            if (Damage > 3) {
                Damage = 3;
            }
            Integer result = DealDamage(player, target, Damage, "melee");
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "12");
            params.put("speed", "0.5");
            PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName());
            setSkillCooldown(player, "bash", 10);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "Bash I");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("bash ii")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".bash_2").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 5);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use Bash II on");
                return;
            }
            String confirmed = subMana(player, 6);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer str = player.getMetadata("Strength").get(0).asInt();
            Integer Damage = str / 6;
            if (Damage > 5) {
                Damage = 5;
            }
            Integer result = DealDamage(player, target, Damage, "melee");
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
            if (target instanceof Player) {
                player.sendMessage("You dealt " + result + " damage to " + target.getType().getName());
            }
            setSkillCooldown(player, "bash_2", 12);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "Bash II");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("lunge")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".lunge").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 12);
            if (target == null || target instanceof ExperienceOrb) {
                player.sendMessage("No target to use lunge on");
                return;
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            float playeryaw = player.getLocation().getYaw();
            float playerpitch = player.getLocation().getPitch();
            Location endpoint = new Location(player.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), playeryaw, playerpitch);

            //makes player immune to damage on teleport
            ArrayList<String> InvulnerablePlayers = this.invulPlayers;
            final String playername = player.getName();
            InvulnerablePlayers.add(player.getName());
            this.invulPlayers = InvulnerablePlayers;
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    getInstance().invulPlayers.remove(playername);
                }

            }, (1 * 20));
            Map<String, String> params = new HashMap<String, String>();
            params.put("wind", "calm");
            params.put("speed", "0.5");
            Map<String, String> params2 = new HashMap<String, String>();
            params.put("num", "12");
            params.put("speed", "0.5");
            PlayEffect.play(VisualEffect.SMOKE, player.getLocation(), params);
            Location loc2 = player.getLocation().add(0, 1, 0);
            PlayEffect.play(VisualEffect.SMOKE, loc2, params);
            player.teleport(endpoint);
            PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params2);
            player.getLocation().setPitch(playerpitch);
            player.getLocation().setYaw(playeryaw);
            Integer result = DealDamage(player, target, 0, "melee");
            setSkillCooldown(player, "lunge", 16);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "Lunge");
                }
            }
        }

        if (skill.toLowerCase().contentEquals("hard bash")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".hard_bash").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Integer str = player.getMetadata("Strength").get(0).asInt();
            Integer Damage = str / 5;
            if (Damage > 4) {
                Damage = 4;
            }
            Entity target = getTarget(player, 5);
            if (target == null || target instanceof ExperienceOrb) {
                player.sendMessage("No target to use hard bash on");
                return;
            }
            Integer result = DealDamage(player, target, Damage, "melee");
            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.8");
            PlayEffect.play(VisualEffect.CRIT, target.getLocation(), params);
            ((Damageable) target).damage(Damage, player);
            Vector directionVector = player.getLocation().getDirection().normalize();
            Vector playerVelocity = player.getVelocity();
            playerVelocity.add(new Vector(0, 0.6, 0));
            playerVelocity.add(directionVector.multiply(2.0));
            target.setVelocity(playerVelocity);
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName());
            setSkillCooldown(player, "hard_bash", 16);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "Hard Bash");
                }
            }
        }

        if (skill.toLowerCase().contentEquals("meteor strike")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".meteor_strike").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Entity target = getTarget(player, 30);
            if (target == null || target instanceof ExperienceOrb) {
                player.sendMessage("Invalid target for meteor strike");
                return;
            }
            String confirmed = subMana(player, 12);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Vector directionVector = player.getLocation().getDirection().normalize();
            Vector playerVelocity = player.getVelocity();
            playerVelocity.add(new Vector(0, 6.0, 0));
            playerVelocity.add(directionVector.multiply(2.0));
            player.setVelocity(playerVelocity);
            double locX = target.getLocation().getX();
            double locY = target.getLocation().getY() + 20;
            double locZ = target.getLocation().getZ();
            float yaw = target.getLocation().getYaw();
            float pitch = 90;
            final Location targetloc = new Location(player.getWorld(), locX, locY, locZ, yaw, pitch);
            this.jumpPlayers.add(player.getName());
            setSkillCooldown(player, "meteor_strike", 30);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "meteor strike");
                }
            }
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    player.teleport(targetloc);

                }

            }, 20);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(8, 4, 8);
                    Integer Damage = GetPlayerAttackDamage(player) + (player.getMetadata("Strength").get(0).asInt() / 15);
                    for (Entity e : Entities) {
                        ((Damageable) e).damage(Damage, player);
                    }
                }
            }, 50);
        }

        if (skill.toLowerCase().contentEquals("whirlwind")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".whirlwind").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            List<Entity> Entities = player.getNearbyEntities(6, 4, 6);
            if (Entities.isEmpty()) {
                player.sendMessage("No targets nearby");
                return;
            }
            String confirmed = subMana(player, 10);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "30");
            params.put("speed", "0.5");
            PlayEffect.play(VisualEffect.EXPLODE, player.getLocation(), params);
            for (Entity e : Entities) {
                try {
                    Map<String, String> params2 = new HashMap<String, String>();
                    params.put("num", "8");
                    params.put("speed", "0.5");
                    PlayEffect.play(VisualEffect.CRIT, e.getLocation(), params2);
                    Integer result = DealDamage(player, e, 0, "melee");
                    Vector directionVector = player.getLocation().getDirection().normalize();
                    Vector playerVelocity = player.getVelocity();
                    playerVelocity.add(new Vector(0, 0.1, 0));
                    playerVelocity.add(directionVector.multiply(1.0));
                    e.setVelocity(playerVelocity);
                } catch (ClassCastException except) {
                    //
                }
            }
            player.sendMessage("You cast whirlwind!");
            player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 5);
            setSkillCooldown(player, "whirlwind", 24);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.RED + "whirlwind");
                }
            }
        }

        if (skill.toLowerCase().contentEquals("taunt")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".taunt").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
            if (Entities.isEmpty()) {
                player.sendMessage("No targets to use taunt on");
                return;
            }
            String confirmed = subMana(player, 3);
            if (confirmed.contentEquals("no")) {
                return;
            }
            player.sendMessage("You cast Taunt!");
            for (Entity entity : Entities) {
                try {
                    if (entity instanceof Monster) {
                        ((Monster) entity).setTarget(player);
                    }
                    ((Monster) entity).setLeashHolder(player);
                    entity.setMetadata("taunt", new FixedMetadataValue(this, player.getName()));
                } catch (ClassCastException e) {
                    //pass
                }
            }
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
                    if (Entities.isEmpty()) {
                        player.sendMessage("No targets to use taunt on");
                        return;
                    }
                    for (Entity entity : Entities) {
                        try {
                            ((Monster) entity).setTarget((LivingEntity) player);
                        } catch (ClassCastException e) {
                            //pass
                        }
                    }
                }

            }, (2 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
                    if (Entities.isEmpty()) {
                        player.sendMessage("No targets to use taunt on");
                        return;
                    }
                    for (Entity entity : Entities) {
                        try {
                            ((Monster) entity).setTarget((LivingEntity) player);
                        } catch (ClassCastException e) {
                            //pass
                        }
                    }
                }

            }, (4 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
                    if (Entities.isEmpty()) {
                        player.sendMessage("No targets to use taunt on");
                        return;
                    }
                    for (Entity entity : Entities) {
                        try {
                            ((Monster) entity).setTarget((LivingEntity) player);
                        } catch (ClassCastException e) {
                            //pass
                        }
                    }
                }

            }, (6 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
                    if (Entities.isEmpty()) {
                        player.sendMessage("No targets to use taunt on");
                        return;
                    }
                    for (Entity entity : Entities) {
                        try {
                            ((Monster) entity).setTarget((LivingEntity) player);
                        } catch (ClassCastException e) {
                            //pass
                        }
                    }
                }

            }, (8 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    List<Entity> Entities = player.getNearbyEntities(16, 8, 16);
                    if (Entities.isEmpty()) {
                        player.sendMessage("No targets to use taunt on");
                        return;
                    }
                    for (Entity entity : Entities) {
                        try {
                            ((Monster) entity).setTarget((LivingEntity) player);
                            ((Monster) entity).setLeashHolder(null);
                        } catch (ClassCastException e) {
                            //pass
                        }
                    }
                }

            }, (10 * 20));

            setSkillCooldown(player, "taunt", 16);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GOLD + "taunt");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("burning blood")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".burningblood").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            this.burningBlood.add(player.getName());
            Map<String, String> params = new HashMap<String, String>();
            params.put("param", "9");
            PlayEffect.play(VisualEffect.SPLASH, player.getLocation(), params);
            player.sendMessage("You are now effected by Burning Blood");
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    getInstance().burningBlood.remove(player.getName());
                }
            }, (20 * 20));
            setSkillCooldown(player, "burningblood", 40);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GOLD + "Burning Blood");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("sentinel")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".sentinel").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }

            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }

            this.sentinelPlayers.add(player.getName());
            setSkillCooldown(player, "sentinel", 60);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    getInstance().sentinelPlayers.remove(player.getName());
                    player.sendMessage(ChatColor.GOLD + "Sentinel" + ChatColor.RESET + " has wore off!");
                }

            }, (15 * 20));
            player.sendMessage("You have used " + ChatColor.GOLD + "Sentinel");
            Map<String, String> params = new HashMap<String, String>();
            params.put("color", "silver");
            params.put("type", "star");
            PlayEffect.play(VisualEffect.FIREWORK, player.getLocation().add(0, 1, 0), params);
            setSkillCooldown(player, "sentinel", 60);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GOLD + "Sentinel");
                }
            }
        }

        if (skill.toLowerCase().contentEquals("stone defense")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".stonedefense").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }

            String confirmed = subMana(player, 24);
            if (confirmed.contentEquals("no")) {
                return;
            }
            for (final Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    this.sentinelPlayers.add(((Player) targets).getName());

                    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                        @Override
                        public void run() {
                            getInstance().sentinelPlayers.remove(((Player) targets).getName());
                        }

                    }, (20 * 20));
                }
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("param", "3");
            PlayEffect.play(VisualEffect.POTION, player.getLocation(), params);
            setSkillCooldown(player, "stonedefense", 120);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GOLD + "Stone defense");
                }
            }
        }

        if (skill.toLowerCase().contentEquals("backstep")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".backstep").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 2);
            if (confirmed.contentEquals("no")) {
                return;
            }
            setSkillCooldown(player, "backstep", 6);
            double locX = player.getLocation().getX();
            double locY = player.getLocation().getY();
            double locZ = player.getLocation().getZ();
            float yaw = player.getLocation().getYaw();
            float pitch = 0;
            final Location targetloc = new Location(player.getWorld(), locX, locY, locZ, yaw, pitch);
            player.teleport(targetloc);
            Vector directionVector = player.getLocation().getDirection().normalize();
            Vector playerVelocity = new Vector(0, 0, 0);
            playerVelocity.add(new Vector(0, 0.3, 0));
            playerVelocity.add(directionVector.multiply(-1.5));
            player.setVelocity(playerVelocity);
        }
        if (skill.toLowerCase().contentEquals("stab")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".stab").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 5);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use stab on");
                return;
            }
            String confirmed = subMana(player, 5);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer agi = player.getMetadata("Agility").get(0).asInt();
            Integer Damage = agi / 4;
            if (Damage > 4) {
                Damage = 4;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation(), params);
            Integer result = DealDamage(player, target, Damage, "melee");
            ((Damageable) target).damage(Damage, player);
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName());
            setSkillCooldown(player, "stab", 8);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GREEN + "stab");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("jump")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".jump").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            double locX = player.getLocation().getX();
            double locY = player.getLocation().getY();
            double locZ = player.getLocation().getZ();
            float yaw = player.getLocation().getYaw();
            float pitch = 0;
            final Location targetloc = new Location(player.getWorld(), locX, locY, locZ, yaw, pitch);
            player.teleport(targetloc);
            this.jumpPlayers.add(player.getName());
            Vector directionVector = player.getLocation().getDirection().normalize();
            Vector playerVelocity = new Vector(0, 0, 0);
            playerVelocity.add(new Vector(0, 1.2, 0));
            playerVelocity.add(directionVector.multiply(0.5));
            player.setVelocity(playerVelocity);
            setSkillCooldown(player, "jump", 8);
        }

        if (skill.toLowerCase().contentEquals("backstab")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".backstab").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 30);
            if (target == null || target instanceof ExperienceOrb) {
                player.sendMessage("No target to use backstab on");
                return;
            }
            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }

            //teleport backup portion
            Location playerloc = player.getLocation();
            double locX = target.getLocation().getX();
            double locY = target.getLocation().getY();
            double locZ = target.getLocation().getZ();
            float yaw = target.getLocation().getYaw();
            float pitch = 0;
            final Location targetloc = new Location(player.getWorld(), locX, locY, locZ, yaw, pitch);
            player.teleport(targetloc);
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.MAGICCRIT, targetloc, params);
            Vector directionVector = player.getLocation().getDirection().normalize();
            Vector playerVelocity = new Vector(0, 0, 0);
            playerVelocity.add(new Vector(0, 0.1, 0));
            playerVelocity.add(directionVector.multiply(-1.0));
            player.setVelocity(playerVelocity);
            //immune to creature damage on teleport
            ArrayList<String> InvulnerablePlayers = this.invulPlayers;
            final String playername = player.getName();
            InvulnerablePlayers.add(player.getName());
            this.invulPlayers = InvulnerablePlayers;
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                public void run() {
                    getInstance().invulPlayers.remove(playername);
                }
            }, 20);
            //damage portion
            Integer agi = player.getMetadata("Agility").get(0).asInt();
            Integer Damage = agi / 6;
            if (Damage > 8) {
                Damage = 8;
            }
            Integer result = DealDamage(player, target, Damage, "melee");
            ((Damageable) target).damage(Damage, player);
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName());

            setSkillCooldown(player, "backstab", 12);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GREEN + "Backstab");
                }
            }

        }

        if (skill.toLowerCase().contentEquals("life steal")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".life_steal").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 5);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use stab on");
                return;
            }
            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer agi = player.getMetadata("Agility").get(0).asInt();
            Integer Damage = agi / 10;
            if (Damage > 10) {
                Damage = 10;
            }
            Integer result = DealDamage(player, target, Damage, "melee");
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation(), params);
            Map<String, String> params2 = new HashMap<String, String>();
            PlayEffect.play(VisualEffect.HEART, player.getLocation().add(0, 1, 0), params2);
            ((Damageable) target).damage(Damage, player);
            Integer healamt = (Damage / 2) + 1;
            Integer health = (int) (player.getHealth() + healamt);
            if (health >= player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            } else {
                player.setHealth(health);
            }
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName() + ", and healed yourself for " + healamt);
            setSkillCooldown(player, "life_steal", 16);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GREEN + "Life Steal I");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("serrated blade")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".serrated_blade").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Entity target = getTarget(player, 5);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use serrated blade on");
                return;
            }
            String confirmed = subMana(player, 12);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer agi = player.getMetadata("Agility").get(0).asInt();
            Integer startBleed = agi / 60;
            if (startBleed == 0) {
                startBleed = 1;
            }
            final Integer Bleed = startBleed;
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "15");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation(), params);
            Integer result = DealDamage(player, target, 0, "melee");
            player.sendMessage("You dealt " + result + " damage to " + target.getType().getName() + " and inflicted bleed");
            setSkillCooldown(player, "serrated_blade", 32);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Bleed);
                    } else {
                        ((Damageable) target).damage(Bleed);
                    }
                }

            }, (1 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).setHealth(((Player) target).getHealth() - Bleed);
                    } else {
                        ((Damageable) target).damage(Bleed, player);
                    }
                }

            }, (2 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).setHealth(((Player) target).getHealth() - Bleed);
                    } else {
                        ((Damageable) target).damage(Bleed, player);
                    }
                }

            }, (3 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).setHealth(((Player) target).getHealth() - Bleed);
                    } else {
                        ((Damageable) target).damage(Bleed, player);
                    }
                }

            }, (4 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).setHealth(((Player) target).getHealth() - Bleed);
                    } else {
                        ((Damageable) target).damage(Bleed, player);
                    }
                }

            }, (5 * 20));

            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.GREEN + "Serrated Blade");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("power shot i")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".power_shot_1").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "power shot 1"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(3);
            arrow.setVelocity(vector2);
            setSkillCooldown(player, "power_shot_1", 6);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Power Shot I");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("power shot ii")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".power_shot_2").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 6);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "power shot 2"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(3);
            arrow.setVelocity(vector2);
            setSkillCooldown(player, "power_shot_2", 8);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Power Shot II");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("invisible i")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".invis_1").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Player[] players = Bukkit.getServer().getOnlinePlayers();
            String confirmed = subMana(player, 5);
            if (confirmed.contentEquals("no")) {
                return;
            }
            setSkillCooldown(player, "invis_1", 30);
            for (Player p : players) {
                p.hidePlayer(player);
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "30");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.PORTAL, player.getLocation(), params);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10, 1));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    for (Player p : players) {
                        p.showPlayer(player);
                    }
                }

            }, (20 * 10));
        }
        if (skill.toLowerCase().contentEquals("invisible ii")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".invis_2").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Player[] players = Bukkit.getServer().getOnlinePlayers();
            String confirmed = subMana(player, 6);
            if (confirmed.contentEquals("no")) {
                return;
            }
            setSkillCooldown(player, "invis_2", 35);
            for (Player p : players) {
                p.hidePlayer(player);
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "30");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.PORTAL, player.getLocation(), params);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 15, 1));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    for (Player p : players) {
                        p.showPlayer(player);
                    }
                }

            }, (20 * 15));
        }
        if (skill.toLowerCase().contentEquals("invisible iii")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".invis_3").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Player[] players = Bukkit.getServer().getOnlinePlayers();
            String confirmed = subMana(player, 7);
            if (confirmed.contentEquals("no")) {
                return;
            }
            setSkillCooldown(player, "invis_3", 45);
            for (Player p : players) {
                p.hidePlayer(player);
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("num", "30");
            params.put("speed", "0.6");
            PlayEffect.play(VisualEffect.PORTAL, player.getLocation(), params);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 25, 1));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    for (Player p : players) {
                        p.showPlayer(player);
                    }
                }

            }, (20 * 20));
        }
        if (skill.toLowerCase().contentEquals("poison shot")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".poison_shot").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 50);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use Poison Shot on");
                return;
            }
            String confirmed = subMana(player, 6);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "poison shot"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(3);
            arrow.setVelocity(vector2);
            setSkillCooldown(player, "poison_shot", 16);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Poison Shot");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("cripple shot")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".cripple_shot").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 50);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use Cripple Shot on");
                return;
            }
            String confirmed = subMana(player, 7);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "cripple shot"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(3);
            arrow.setVelocity(vector2);
            setSkillCooldown(player, "cripple_shot", 20);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Cripple Shot");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("sniper shot")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".sniper_shot").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Entity target = getTarget(player, 100);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use Sniper Shot on");
                return;
            }
            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "sniper shot"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(100);
            arrow.setVelocity(vector2);
            setSkillCooldown(player, "sniper_shot", 32);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Sniper Shot");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("teleport shot")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".teleport_shot").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 12);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata("skillname", new FixedMetadataValue(this, "teleport shot"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(3);
            arrow.setVelocity(vector2);
            arrow.setBounce(false);
            setSkillCooldown(player, "teleport_shot", 48);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_GREEN + "Teleport Shot");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("arrow rain")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".arrow_rain").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Entity target = getTarget(player, 50);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use arrow rain on");
                return;
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            List<Entity> nearby = target.getNearbyEntities(12, 6, 12);
            Location tele2 = getTopLocation(target.getLocation(), 30);
            Vector from2 = new Vector(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
            Vector to2 = new Vector(tele2.getX(), tele2.getY(), tele2.getZ());
            Vector vector2 = from2.subtract(to2);
            Arrow arrow2 = player.launchProjectile(Arrow.class);
            Vector darrow2 = vector2;
            arrow2.setVelocity(darrow2);
            arrow2.teleport(tele2);
            for (Entity e : nearby) {
                if (e instanceof Creature || e instanceof Player) {
                    Location tele = getTopLocation(e.getLocation(), 30);
                    Vector from = new Vector(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ());
                    Vector to = new Vector(tele.getX(), tele.getY(), tele.getZ());
                    Vector vector = from.subtract(to);
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    Vector darrow = vector;
                    arrow.setVelocity(darrow);
                    arrow.teleport(tele);
                }
            }
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Location tele2 = getTopLocation(target.getLocation(), 30);
                        Vector from2 = new Vector(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
                        Vector to2 = new Vector(tele2.getX(), tele2.getY(), tele2.getZ());
                        Vector vector2 = from2.subtract(to2);
                        Arrow arrow2 = player.launchProjectile(Arrow.class);
                        Vector darrow2 = vector2;
                        arrow2.setVelocity(darrow2);
                        arrow2.teleport(tele2);
                        List<Entity> nearby = target.getNearbyEntities(12, 6, 12);
                        for (Entity e : nearby) {
                            if (e instanceof Creature || e instanceof Player) {
                                Location tele = getTopLocation(e.getLocation(), 30);
                                Vector from = new Vector(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ());
                                Vector to = new Vector(tele.getX(), tele.getY(), tele.getZ());
                                Vector vector = from.subtract(to);
                                Arrow arrow = player.launchProjectile(Arrow.class);
                                Vector darrow = vector;
                                arrow.setVelocity(darrow);
                                arrow.teleport(tele);
                            }
                        }
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (2 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Location tele2 = getTopLocation(target.getLocation(), 30);
                        Vector from2 = new Vector(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
                        Vector to2 = new Vector(tele2.getX(), tele2.getY(), tele2.getZ());
                        Vector vector2 = from2.subtract(to2);
                        Arrow arrow2 = player.launchProjectile(Arrow.class);
                        Vector darrow2 = vector2;
                        arrow2.setVelocity(darrow2);
                        arrow2.teleport(tele2);
                        List<Entity> nearby = target.getNearbyEntities(12, 6, 12);
                        for (Entity e : nearby) {
                            if (e instanceof Creature || e instanceof Player) {
                                Location tele = getTopLocation(e.getLocation(), 30);
                                Vector from = new Vector(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ());
                                Vector to = new Vector(tele.getX(), tele.getY(), tele.getZ());
                                Vector vector = from.subtract(to);
                                Arrow arrow = player.launchProjectile(Arrow.class);
                                Vector darrow = vector;
                                arrow.setVelocity(darrow);
                                arrow.teleport(tele);
                            }
                        }
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (4 * 20));
            setSkillCooldown(player, "arrow_rain", 120);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used Arrow Rain");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("heal i") || skill.toLowerCase().contentEquals("heal 1")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".heal_1").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Player target = getTargetPlayer(player, 20);
            String targName = "";
            if (target == null || !(target instanceof Player)) {
                target = player;
                targName = "yourself";
            } else {
                targName = target.getName();
            }
            String confirmed = subMana(player, 5);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer wisdom = player.getMetadata("Wisdom").get(0).asInt();
            Integer healamt = (wisdom / 3) + 5;
            if (healamt > 15) {
                healamt = 15;
            }
            healamt += player.getMetadata("MagicBonus").get(0).asInt();
            Integer health = (int) (((Player) target).getHealth() + healamt);
            Map<String, String> params2 = new HashMap<String, String>();
            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
            if (health >= ((Player) target).getMaxHealth()) {
                ((Player) target).setHealth(((Player) target).getMaxHealth());
            } else {
                ((Player) target).setHealth(health);
            }
            player.sendMessage(ChatColor.GREEN + "You have healed " + targName + " for " + healamt + " :: " + +((Player) target).getHealth() + "/" + ((Player) target).getMaxHealth());
            setSkillCooldown(player, "heal_1", 2);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.BLUE + "Heal I");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("regeneration")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".regeneration").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Player targets = getTargetPlayer(player, 20);
            String targName = "";
            if (targets == null || !(targets instanceof Player)) {
                targets = player;
                targName = "yourself";
            } else {
                targName = targets.getName();
            }
            final Player target = targets;
            String confirmed = subMana(player, 5);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer wisdom = player.getMetadata("Wisdom").get(0).asInt();
            Integer healamt = (wisdom / 20);
            if (healamt > 3) {
                healamt = 3;
            }
            final Integer regen = healamt;
            setSkillCooldown(player, "regeneration", 12);
            Integer health = (int) (((Player) target).getHealth() + regen);
            if (health >= ((Player) target).getMaxHealth()) {
                ((Player) target).setHealth(((Player) target).getMaxHealth());
            } else {
                ((Player) target).setHealth(health);
            }
            player.sendMessage("You cast Regeneration on " + targName + " giving them " + healamt + " regen/second");
            setSkillCooldown(player, "regeneration", 12);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (1 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (2 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (3 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (4 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (5 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        Integer health = (int) (((Player) target).getHealth() + regen);
                        if (health >= ((Player) target).getMaxHealth()) {
                            ((Player) target).setHealth(((Player) target).getMaxHealth());
                        } else {
                            ((Player) target).setHealth(health);
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, (6 * 20));
            if (wisdom >= 100) {
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Integer health = (int) (((Player) target).getHealth() + regen);
                            if (health >= ((Player) target).getMaxHealth()) {
                                ((Player) target).setHealth(((Player) target).getMaxHealth());
                            } else {
                                ((Player) target).setHealth(health);
                            }
                            Map<String, String> params2 = new HashMap<String, String>();
                            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                        } catch (NullPointerException e) {
                            return;
                        }
                    }
                }, (7 * 20));
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Integer health = (int) (((Player) target).getHealth() + regen);
                            if (health >= ((Player) target).getMaxHealth()) {
                                ((Player) target).setHealth(((Player) target).getMaxHealth());
                            } else {
                                ((Player) target).setHealth(health);
                            }
                            Map<String, String> params2 = new HashMap<String, String>();
                            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                        } catch (NullPointerException e) {
                            return;
                        }
                    }
                }, (8 * 20));
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Integer health = (int) (((Player) target).getHealth() + regen);
                            if (health >= ((Player) target).getMaxHealth()) {
                                ((Player) target).setHealth(((Player) target).getMaxHealth());
                            } else {
                                ((Player) target).setHealth(health);
                            }
                            Map<String, String> params2 = new HashMap<String, String>();
                            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                        } catch (NullPointerException e) {
                            return;
                        }
                    }
                }, (9 * 20));
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Integer health = (int) (((Player) target).getHealth() + regen);
                            if (health >= ((Player) target).getMaxHealth()) {
                                ((Player) target).setHealth(((Player) target).getMaxHealth());
                            } else {
                                ((Player) target).setHealth(health);
                            }
                            Map<String, String> params2 = new HashMap<String, String>();
                            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
                        } catch (NullPointerException e) {
                            return;
                        }
                    }
                }, (10 * 20));
            }
            for (Entity targetss : player.getNearbyEntities(30, 20, 30)) {
                if (targetss instanceof Player) {
                    ((Player) targetss).sendMessage(player.getName() + " has used Regenerate on " + targName);
                }
            }
        }
        if (skill.toLowerCase().contentEquals("heal ii") || skill.toLowerCase().contentEquals("heal 2")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".heal_2").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Player target = getTargetPlayer(player, 20);
            String targName = "";
            if (target == null || !(target instanceof Player)) {
                target = player;
                targName = "yourself";
            } else {
                targName = target.getName();
            }
            String confirmed = subMana(player, 7);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer wisdom = player.getMetadata("Wisdom").get(0).asInt();
            Integer healamt = (wisdom / 4) + 5;
            if (healamt > 25) {
                healamt = 25;
            }
            healamt += player.getMetadata("MagicBonus").get(0).asInt();
            Integer health = (int) (((Player) target).getHealth() + healamt);
            if (health >= ((Player) target).getMaxHealth()) {
                ((Player) target).setHealth(((Player) target).getMaxHealth());
            } else {
                ((Player) target).setHealth(health);
            }
            player.sendMessage(ChatColor.GREEN + "You have healed " + targName + " for " + healamt + " :: " + ((Player) target).getHealth() + "/" + ((Player) target).getMaxHealth());
            if (!(target == player)) {
                target.sendMessage(ChatColor.GREEN + "You have been healed by" + player.getName() + " for " + healamt + " :: " + ((Player) target).getHealth() + "/" + ((Player) target).getMaxHealth());
            }
            setSkillCooldown(player, "heal_2", 4);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.BLUE + "Heal II");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("group heal")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".group_heal").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 5);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer wisdom = player.getMetadata("Wisdom").get(0).asInt();
            Integer healamt = (wisdom / 10) + 5 + player.getMetadata("MagicBonus").get(0).asInt();
            Integer health = (int) (((Player) player).getHealth() + healamt);
            if (health >= ((Player) player).getMaxHealth()) {
                ((Player) player).setHealth(((Player) player).getMaxHealth());
            } else {
                ((Player) player).setHealth(health);
            }
            List<Entity> nearby = player.getNearbyEntities(10, 10, 10);
            String playershealed = "Players healed by Group Heal were";
            for (Entity e : nearby) {
                if (e instanceof Player) {
                    Integer health2 = (int) (((Player) e).getHealth() + healamt);
                    if (health2 >= ((Player) e).getMaxHealth()) {
                        ((Player) e).setHealth(((Player) e).getMaxHealth());
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, e.getLocation().add(0, 1, 0), params2);
                        playershealed += " ," + ((Player) e).getName();
                    } else {
                        ((Player) e).setHealth(health);
                        playershealed += " ," + ((Player) e).getName();
                        Map<String, String> params2 = new HashMap<String, String>();
                        PlayEffect.play(VisualEffect.HEART, e.getLocation().add(0, 1, 0), params2);
                    }
                    if (!(e == player)) {
                        ((Player) e).sendMessage(ChatColor.GREEN + "You have been healed by" + player.getName() + " for " + healamt + " :: " + ((Player) e).getHealth() + "/" + ((Player) e).getMaxHealth());
                    }
                    player.sendMessage(ChatColor.GREEN + "You have healed " + ((Player) e).getName() + " for " + healamt + " :: " + ((Player) e).getHealth() + "/" + ((Player) e).getMaxHealth());
                }
            }
            player.sendMessage(playershealed);
            setSkillCooldown(player, "group_heal", 30);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.BLUE + "Group Heal");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("heal iii") || skill.toLowerCase().contentEquals("heal 3")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".heal_3").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            Player target = getTargetPlayer(player, 20);
            String targName = "";
            if (target == null || !(target instanceof Player)) {
                target = player;
                targName = "yourself";
            } else {
                targName = target.getName();
            }
            String confirmed = subMana(player, 10);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer wisdom = player.getMetadata("Wisdom").get(0).asInt();
            Integer healamt = (wisdom / 4) + 5;
            if (healamt > 35) {
                healamt = 35;
            }
            healamt += player.getMetadata("MagicBonus").get(0).asInt();
            Integer health = (int) (((Player) target).getHealth() + healamt);
            if (health >= ((Player) target).getMaxHealth()) {
                ((Player) target).setHealth(((Player) target).getMaxHealth());
            } else {
                ((Player) target).setHealth(health);
            }
            if (!(target == player)) {
                target.sendMessage(ChatColor.GREEN + "You have been healed by" + player.getName() + " for " + healamt + " :: " + ((Player) target).getHealth() + "/" + ((Player) target).getMaxHealth());
            }
            Map<String, String> params2 = new HashMap<String, String>();
            PlayEffect.play(VisualEffect.HEART, target.getLocation().add(0, 1, 0), params2);
            player.sendMessage(ChatColor.GREEN + "You have healed " + targName + " for " + healamt + " :: " + ((Player) target).getHealth() + "/" + ((Player) target).getMaxHealth());
            setSkillCooldown(player, "heal_3", 8);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.BLUE + "Heal III");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("fireball i") || skill.toLowerCase().contentEquals("fireball 1")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".fireball_1").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 4);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setMetadata("skillname", new FixedMetadataValue(this, "Fireball1"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(2);
            fireball.setVelocity(vector2);
            setSkillCooldown(player, "fireball_1", 2);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_PURPLE + "Fireball I");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("poison")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".poison").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Entity target = getTarget(player, 30);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use poison on");
                return;
            }
            String confirmed = subMana(player, 8);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer Int = player.getMetadata("Intelligence").get(0).asInt();
            Integer startPoison = Int / 50;
            Map<String, String> params = new HashMap<String, String>();
            params.put("param", "4");
            PlayEffect.play(VisualEffect.POTION, target.getLocation(), params);
            if (startPoison == 0) {
                startPoison = 1;
            }
            final Integer Poison = startPoison;
            player.sendMessage("You poisoned " + target.getType().getName());
            setSkillCooldown(player, "poison", 10);
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Poison);
                    } else {
                        ((Damageable) target).damage(Poison);
                        ;
                    }
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("num", "30");
                    params2.put("speed", "0.3");
                    PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                }

            }, (1 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Poison);
                    } else {
                        ((Damageable) target).damage(Poison);
                        ;
                    }
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("num", "30");
                    params2.put("speed", "0.3");
                    PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                }

            }, (2 * 20));

            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Poison);
                    } else {
                        ((Damageable) target).damage(Poison);
                        ;
                    }
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("num", "30");
                    params2.put("speed", "0.3");
                    PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                }

            }, (3 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Poison);
                    } else {
                        ((Damageable) target).damage(Poison);
                        ;
                    }
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("num", "30");
                    params2.put("speed", "0.3");
                    PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                }

            }, (4 * 20));
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        ((Player) target).damage(Poison);
                    } else {
                        ((Damageable) target).damage(Poison);
                        ;
                    }
                    Map<String, String> params2 = new HashMap<String, String>();
                    params2.put("num", "30");
                    params2.put("speed", "0.3");
                    PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                }

            }, (5 * 20));
            if (Int >= 100) {
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        if (target instanceof Player) {
                            ((Player) target).damage(Poison);
                        } else {
                            ((Damageable) target).damage(Poison);
                            ;
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("num", "30");
                        params2.put("speed", "0.3");
                        PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                    }

                }, (6 * 20));
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        if (target instanceof Player) {
                            ((Player) target).damage(Poison);
                        } else {
                            ((Damageable) target).damage(Poison);
                            ;
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("num", "30");
                        params2.put("speed", "0.3");
                        PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                    }

                }, (7 * 20));
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        if (target instanceof Player) {
                            ((Player) target).damage(Poison);
                        } else {
                            ((Damageable) target).damage(Poison);
                            ;
                        }
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("num", "30");
                        params2.put("speed", "0.3");
                        PlayEffect.play(VisualEffect.MAGICCRIT, target.getLocation().add(0, 1, 0), params2);
                    }

                }, (8 * 20));
            }
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_PURPLE + "Poison");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("slow")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".slow").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            final Entity target = getTarget(player, 30);
            if (!(target instanceof Creature || target instanceof Player)) {
                player.sendMessage("No target to use slow on");
                return;
            }
            String confirmed = subMana(player, 6);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Integer Int = player.getMetadata("MagicBonus").get(0).asInt();
            ((LivingEntity) target).setHealth((((LivingEntity) target).getHealth() - Int));
            ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 1), true);
            player.sendMessage("You slowed " + target.getType().getName());
            setSkillCooldown(player, "slow", 12);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used Slow");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("fireball ii") || skill.toLowerCase().contentEquals("fireball 2")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".fireball_2").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            String confirmed = subMana(player, 7);
            if (confirmed.contentEquals("no")) {
                return;
            }
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setMetadata("skillname", new FixedMetadataValue(this, "Fireball2"));
            Vector vector2 = player.getLocation().getDirection();
            vector2.multiply(2);
            fireball.setVelocity(vector2);
            setSkillCooldown(player, "fireball_2", 4);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_PURPLE + "Fireball II");
                }
            }
        }
        if (skill.toLowerCase().contentEquals("teleport")) {
            try {
                if (this.cooldownconfig.get(player.getName() + ".teleport").toString().equals("true")) {
                    player.sendMessage(skill + " has not cooldown yet");
                    return;
                }
            } catch (NullPointerException e) {
                //pass
            }
            try {
                String partyid = player.getMetadata("Partyid").get(0).asString();
                List<String> partymembers = this.partyConfig.getStringList("PartyPlayers." + partyid + ".Members");
                for (String member : partymembers) {
                    Player realmemb = Bukkit.getPlayer(member);
                    if (realmemb.equals(player)) {
                        //Pass cause its the caster
                    } else {
                        realmemb.teleport(player);
                        realmemb.sendMessage(ChatColor.DARK_PURPLE + "You were teleported by " + player.getName());
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                //pass
            }
            String confirmed = subMana(player, 50);
            if (confirmed.contentEquals("no")) {
                return;
            }
            setSkillCooldown(player, "teleport", 18);
            for (Entity targets : player.getNearbyEntities(30, 20, 30)) {
                if (targets.getType().toString() == "PLAYER") {
                    ((Player) targets).sendMessage(player.getName() + " has used " + ChatColor.DARK_PURPLE + "Teleport");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageFall(EntityDamageEvent e) {
        String cause = e.getCause().toString();
        if (cause.toLowerCase().equals("fall")) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (this.jumpPlayers.contains(player.getName())) {
                    e.setCancelled(true);
                    this.jumpPlayers.remove(player.getName());
                }
            }
        }
    }

    public Integer GetPlayerAttackDamage(Player player) {
        Integer resultdamage = 0;
        Integer minDamage = 1;
        Integer maxDamage = 2;
        Player Damager = player;
        Integer count = 0;
        Inventory item = Damager.getInventory();
        ItemStack item2 = item.getItem(count);
        while (count <= 8) {
            if (item2.getType().toString().toLowerCase().contains("sword") || item2.getType().toString().toLowerCase().contains("axe")) {
                break;
            } else {
                count += 1;
                item2 = item.getItem(count);
            }
        }
        if (item2.getType() == Material.WOOD_SWORD) {
            minDamage = 1;
            maxDamage = 4;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.STONE_SWORD)) {
            minDamage = 2;
            maxDamage = 5;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.GOLD_SWORD)) {
            minDamage = 3;
            maxDamage = 6;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.IRON_SWORD)) {
            minDamage = 4;
            maxDamage = 7;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.DIAMOND_SWORD)) {
            minDamage = 5;
            maxDamage = 8;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.WOOD_AXE)) {
            minDamage = 2;
            maxDamage = 6;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.STONE_AXE)) {
            minDamage = 3;
            maxDamage = 7;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.GOLD_AXE)) {
            minDamage = 4;
            maxDamage = 8;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.IRON_AXE)) {
            minDamage = 4;
            maxDamage = 9;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        } else if ((item2.getType() == Material.DIAMOND_AXE)) {
            minDamage = 4;
            maxDamage = 10;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.DAMAGE_ALL);
                minDamage += enchant;
                maxDamage += enchant;
            }
        }
        if ((item2.hasItemMeta())) {
            String name = ChatColor.stripColor(item2.getItemMeta().getDisplayName());
            minDamage = (Integer) this.gearConfig.getInt("Weapons." + name + ".min");
            maxDamage = (Integer) this.gearConfig.getInt("Weapons." + name + ".max");
            if (item2.getEnchantments().size() != 0) {
                maxDamage += (Integer) Damager.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            }
            if (item2.getItemMeta().getDisplayName().contains("(BROKEN)")) {
                maxDamage = 1;
                minDamage = 0;
            }
        }
        resultdamage = (Integer) (random.nextInt(maxDamage - minDamage) + minDamage + (Integer) ((Player) Damager).getMetadata("MeleeBonus").get(0).value());
        return resultdamage;
    }

    public Integer GetPlayerRangedAttackDamage(Player player) {
        Integer resultdamage = 0;
        Integer minDamage = 1;
        Integer maxDamage = 3;
        Player Damager = player;
        Integer count = 0;
        Inventory item = Damager.getInventory();
        ItemStack item2 = item.getItem(count);
        while (count <= 8) {
            if (item2.getType() == Material.BOW) {
                break;
            } else {
                count += 1;
                item2 = item.getItem(count);
            }
        }
        if ((item2.hasItemMeta())) {
            String name = ChatColor.stripColor(item2.getItemMeta().getDisplayName());
            minDamage = (Integer) this.gearConfig.getInt("Weapons." + name + ".min");
            maxDamage = (Integer) this.gearConfig.getInt("Weapons." + name + ".max");
            if (item2.getEnchantments().size() != 0) {
                maxDamage += (Integer) Damager.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
            }
        } else if ((item2.getType() == Material.BOW)) {
            minDamage = 3;
            maxDamage = 8;
            if (item2.getEnchantments().size() != 0) {
                Integer enchant = item2.getEnchantments().get(Enchantment.ARROW_DAMAGE);
                minDamage += enchant;
                maxDamage += enchant;
            }
        }
        try {
            try {
                if (item2.getItemMeta().getDisplayName().contains("(BROKEN)")) {
                    maxDamage = 1;
                    minDamage = 0;
                }
                resultdamage = (Integer) (random.nextInt(maxDamage - minDamage) + minDamage + (Integer) ((Player) Damager).getMetadata("RangedBonus").get(0).value());
            } catch (IndexOutOfBoundsException e) {
                //NPC
                return 0;
            }
        } catch (IllegalArgumentException e) {
            resultdamage = 4;
        }
        return resultdamage;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        try {
            this.playersConfig.set("Players." + name + ".Level", player.getMetadata("Level").get(0).asInt());
            this.playersConfig.set("Players." + name + ".Experience", player.getMetadata("Exp").get(0).asInt());
            this.playersConfig.set("Players." + name + ".EXPTnl", player.getMetadata("ExpTnl").get(0).asInt());
            this.playersConfig.set("Players." + name + ".SkillPoints", player.getMetadata("SkillP").get(0).asInt());
            Integer Str = (Integer) player.getMetadata("Strength").get(0).value();
            Integer Con = (Integer) player.getMetadata("Constitution").get(0).value();
            Integer Dex = (Integer) player.getMetadata("Dexterity").get(0).value();
            Integer Agi = (Integer) player.getMetadata("Agility").get(0).value();
            Integer Wis = (Integer) player.getMetadata("Wisdom").get(0).value();
            Integer Int = (Integer) player.getMetadata("Intelligence").get(0).value();
            Integer TotalSkillP = Str + Con + Dex + Agi + Wis + Int + player.getMetadata("SkillP").get(0).asInt() - 6;
            this.playersConfig.set("Players." + name + ".TotalSkillPoints", TotalSkillP);
            this.playersConfig.set("Players." + name + ".TotalSkillPoints", player.getMetadata("TotalSkillP").get(0).asInt());
            this.playersConfig.set("Players." + name + ".Strength", Str);
            this.playersConfig.set("Players." + name + ".Constitution", Con);
            this.playersConfig.set("Players." + name + ".Dexterity", Dex);
            this.playersConfig.set("Players." + name + ".Agility", Agi);
            this.playersConfig.set("Players." + name + ".Wisdom", Wis);
            this.playersConfig.set("Players." + name + ".Intelligence", Int);
            try {
                this.playersConfig.save(this.Players);
                log.info(name + " was saved!");
            } catch (IOException e) {
                log.severe("Problem while saving " + name);
                e.printStackTrace();
            }
        } finally {

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (!this.Players.exists()) {
            this.playersConfig.set("Players." + name + ".Level", 1);
            this.playersConfig.set("Players." + name + ".Experience", 0);
            this.playersConfig.set("Players." + name + ".EXPTnl", 1000);
            this.playersConfig.set("Players." + name + ".SkillPoints", 5);
            this.playersConfig.set("Players." + name + ".TotalSkillPoints", 5);
            this.playersConfig.set("Players." + name + ".Strength", 1);
            this.playersConfig.set("Players." + name + ".Constitution", 1);
            this.playersConfig.set("Players." + name + ".Dexterity", 1);
            this.playersConfig.set("Players." + name + ".Agility", 1);
            this.playersConfig.set("Players." + name + ".Wisdom", 1);
            this.playersConfig.set("Players." + name + ".Intelligence", 1);
            player.setMetadata("Level", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Level")));
            player.setMetadata("Exp", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Experience")));
            player.setMetadata("ExpTnl", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".EXPTnl")));
            player.setMetadata("SkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".SkillPoints")));
            player.setMetadata("TotalSkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".TotalSkillPoints")));
            player.setMetadata("Strength", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Strength")));
            player.setMetadata("Constitution", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Constitution")));
            player.setMetadata("Dexterity", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Dexterity")));
            player.setMetadata("Agility", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Agility")));
            player.setMetadata("Wisdom", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Wisdom")));
            player.setMetadata("Intelligence", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Intelligence")));
            try {
                this.playersConfig.save(this.Players);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            if (!this.playersConfig.getConfigurationSection("Players").getKeys(true).contains(name)) {
                this.playersConfig.set("Players." + name + ".Level", 1);
                this.playersConfig.set("Players." + name + ".Experience", 0);
                this.playersConfig.set("Players." + name + ".EXPTnl", 500);
                this.playersConfig.set("Players." + name + ".SkillPoints", 5);
                this.playersConfig.set("Players." + name + ".TotalSkillPoints", 5);
                this.playersConfig.set("Players." + name + ".Strength", 1);
                this.playersConfig.set("Players." + name + ".Constitution", 1);
                this.playersConfig.set("Players." + name + ".Dexterity", 1);
                this.playersConfig.set("Players." + name + ".Agility", 1);
                this.playersConfig.set("Players." + name + ".Wisdom", 1);
                this.playersConfig.set("Players." + name + ".Intelligence", 1);
                player.setMetadata("Level", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Level")));
                player.setMetadata("Exp", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Experience")));
                player.setMetadata("ExpTnl", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".EXPTnl")));
                player.setMetadata("SkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".SkillPoints")));
                player.setMetadata("TotalSkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".TotalSkillPoints")));
                player.setMetadata("Strength", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Strength")));
                player.setMetadata("Constitution", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Constitution")));
                player.setMetadata("Dexterity", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Dexterity")));
                player.setMetadata("Agility", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Agility")));
                player.setMetadata("Wisdom", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Wisdom")));
                player.setMetadata("Intelligence", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Intelligence")));
                try {
                    this.playersConfig.save(this.Players);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                player.setMetadata("Level", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Level")));
                player.setMetadata("Exp", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Experience")));
                player.setMetadata("ExpTnl", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".EXPTnl")));
                player.setMetadata("SkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".SkillPoints")));
                player.setMetadata("TotalSkillP", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".TotalSkillPoints")));
                player.setMetadata("Strength", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Strength")));
                player.setMetadata("Constitution", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Constitution")));
                player.setMetadata("Dexterity", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Dexterity")));
                player.setMetadata("Agility", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Agility")));
                player.setMetadata("Wisdom", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Wisdom")));
                player.setMetadata("Intelligence", new FixedMetadataValue(this, this.playersConfig.get("Players." + name + ".Intelligence")));
                String wisdom = player.getMetadata("Wisdom").get(0).asString();
            }
        }
        UpdatePlayer(player);
        ArrayList<String> vanished = this.vanishedPlayers;
        for (String players : vanished) {
            player.hidePlayer(Bukkit.getPlayer(players));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnEntityDeath(EntityDeathEvent event) {
        Entity target = event.getEntity();
        if (target instanceof Creature) {
            if (((Creature) target).isLeashed()) {
                ((Creature) target).setLeashHolder(null);
            }
        }
        EntityDamageEvent damageEvent = target.getLastDamageCause();
        Entity killerevent = event.getEntity().getKiller();
        if ((damageEvent != null) && (damageEvent instanceof EntityDamageByEntityEvent)) {
            Entity killer = ((EntityDamageByEntityEvent) damageEvent).getDamager();
            if ((killer instanceof Arrow && !(target instanceof Player)) || (killer instanceof Fireball && !(target instanceof Player))) {
                Entity shooter = null;
                try {
                    shooter = (Entity) ((Arrow) killer).getShooter();
                } catch (ClassCastException e) {
                    shooter = (Entity) ((Fireball) killer).getShooter();
                }
                if (shooter instanceof Player) {
                    Integer level = 0;
                    try {
                        level = getLevel(event.getEntity());
                    } catch (NullPointerException e) {
                        level = 1;
                    }
                    Integer Level = level;
                    GiveExp(Level, (Player) shooter);
                }
            }
            if (killer instanceof Player && target instanceof Player) {
                Integer DeathLevel = Integer.parseInt(target.getMetadata("Level").get(0).toString());
                GiveExp(DeathLevel, (Player) killer);
            }
            if (killer instanceof Player && !(target instanceof Player)) {
                Integer Level = getLevel(target);
                GiveExp(Level, (Player) killer);
            }
        }
    }

    public void GiveExp(float level, Player player) {
        try {
            String partyid = player.getMetadata("Partyid").get(0).asString();
            List<String> partymembers = this.partyConfig.getStringList("PartyPlayers." + partyid + ".Members");
            float partyamt = 0;
            for (String member : partymembers) {
                Player realmemb = Bukkit.getPlayer(member);
                partyamt += 1;
            }
            for (String member : partymembers) {
                Player realmemb = Bukkit.getPlayer(member);
                try {
                    GivePartyExp(level, realmemb, partyamt);
                } catch (NullPointerException e) {
                    //PASS
                }
            }
            return;
        } catch (IndexOutOfBoundsException e) {
            //pass
        }
        Integer Str = (Integer) player.getMetadata("Strength").get(0).value();
        Integer Con = (Integer) player.getMetadata("Constitution").get(0).value();
        Integer Dex = (Integer) player.getMetadata("Dexterity").get(0).value();
        Integer Agi = (Integer) player.getMetadata("Agility").get(0).value();
        Integer Wis = (Integer) player.getMetadata("Wisdom").get(0).value();
        Integer Int = (Integer) player.getMetadata("Intelligence").get(0).value();
        Integer powerlevel = (Str + Con + Dex + Agi + Wis + Int) / 6;
        float levelmod = (float) (level / powerlevel);
        levelmod = (float) (levelmod * 150.00);
        Integer expgive = (int) levelmod;
        if (expgive >= 200) {
            expgive = 200;
        }
        if (bonustime) {
            expgive = (int) (expgive * 1.50);
        }
        Integer myexp = (Integer) player.getMetadata("Exp").get(0).value();
        Integer mytnl = (Integer) player.getMetadata("ExpTnl").get(0).value();
        Integer mylevel = (Integer) player.getMetadata("Level").get(0).value();
        myexp += expgive;
        mytnl -= expgive;
        if (mytnl <= 0) {
            mylevel += 1;
            player.sendMessage(ChatColor.GREEN + "You have gained skill points!");
            Integer mysp = (Integer) player.getMetadata("SkillP").get(0).value();
            Integer Tsp = (Integer) player.getMetadata("TotalSkillP").get(0).value();
            ArrayList<Integer> bonuslevels = new ArrayList<Integer>();
            ArrayList<Integer> megabonuslevels = new ArrayList<Integer>();
            bonuslevels.add(5);
            megabonuslevels.add(10);
            bonuslevels.add(15);
            megabonuslevels.add(20);
            bonuslevels.add(25);
            megabonuslevels.add(30);
            bonuslevels.add(35);
            megabonuslevels.add(40);
            bonuslevels.add(45);
            megabonuslevels.add(50);
            bonuslevels.add(55);
            megabonuslevels.add(60);
            bonuslevels.add(65);
            megabonuslevels.add(70);
            bonuslevels.add(75);
            megabonuslevels.add(80);
            bonuslevels.add(85);
            megabonuslevels.add(90);
            bonuslevels.add(95);
            megabonuslevels.add(100);
            bonuslevels.add(105);
            megabonuslevels.add(110);
            bonuslevels.add(115);
            megabonuslevels.add(120);
            bonuslevels.add(125);
            megabonuslevels.add(130);
            bonuslevels.add(135);
            megabonuslevels.add(140);
            bonuslevels.add(145);
            megabonuslevels.add(150);
            bonuslevels.add(155);
            megabonuslevels.add(160);
            bonuslevels.add(165);
            megabonuslevels.add(170);
            bonuslevels.add(175);
            megabonuslevels.add(180);
            bonuslevels.add(185);
            megabonuslevels.add(190);
            bonuslevels.add(195);
            megabonuslevels.add(200);
            if (bonuslevels.contains(mylevel)) {
                mysp += 3;
                Tsp += 3;
            } else if (megabonuslevels.contains(mylevel)) {
                mysp += 5;
                Tsp += 5;
            } else {
                mysp += 2;
                Tsp += 2;
            }
            mytnl += (1000 + (150 * (mylevel - 1)));
            player.setMetadata("SkillP", new FixedMetadataValue(this, mysp));
            player.setMetadata("TotalSkillP", new FixedMetadataValue(this, Tsp));
            player.setHealth(player.getMaxHealth());
            player.setMetadata("Level", new FixedMetadataValue(this, mylevel));
        }
        player.setMetadata("Exp", new FixedMetadataValue(this, myexp));
        player.setMetadata("ExpTnl", new FixedMetadataValue(this, mytnl));
    }

    public void GivePartyExp(float level, Player player, float amt) {
        Integer Str = (Integer) player.getMetadata("Strength").get(0).value();
        Integer Con = (Integer) player.getMetadata("Constitution").get(0).value();
        Integer Dex = (Integer) player.getMetadata("Dexterity").get(0).value();
        Integer Agi = (Integer) player.getMetadata("Agility").get(0).value();
        Integer Wis = (Integer) player.getMetadata("Wisdom").get(0).value();
        Integer Int = (Integer) player.getMetadata("Intelligence").get(0).value();
        Integer powerlevel = (Str + Con + Dex + Agi + Wis + Int) / 6;
        float levelmod = (float) (level / powerlevel);
        levelmod = (float) (levelmod * 150.00);
        Integer expgive = (int) levelmod;
        if (expgive >= 200) {
            expgive = 200;
        }
        if (amt == 2) {
            expgive = (int) (expgive * 1.10) / 2;
        }
        if (amt == 3) {
            expgive = (int) (expgive * 1.20) / 3;
        }
        if (amt == 4) {
            expgive = (int) (expgive * 1.35) / 4;
        }
        if (amt == 5) {
            expgive = (int) (expgive * 1.50) / 5;
        }
        if (amt == 6) {
            expgive = (int) (expgive * 1.75) / 6;
        }
        if (bonustime) {
            expgive = (int) (expgive * 1.50);
        }
        Integer myexp = (Integer) player.getMetadata("Exp").get(0).value();
        Integer mytnl = (Integer) player.getMetadata("ExpTnl").get(0).value();
        Integer mylevel = (Integer) player.getMetadata("Level").get(0).value();
        myexp += expgive;
        mytnl -= expgive;
        if (mytnl <= 0) {
            mylevel += 1;
            player.sendMessage(ChatColor.GREEN + "You have gained skill points!");
            Integer mysp = (Integer) player.getMetadata("SkillP").get(0).value();
            Integer Tsp = (Integer) player.getMetadata("TotalSkillP").get(0).value();
            ArrayList<Integer> bonuslevels = new ArrayList<Integer>();
            ArrayList<Integer> megabonuslevels = new ArrayList<Integer>();
            bonuslevels.add(5);
            megabonuslevels.add(10);
            bonuslevels.add(15);
            megabonuslevels.add(20);
            bonuslevels.add(25);
            megabonuslevels.add(30);
            bonuslevels.add(35);
            megabonuslevels.add(40);
            bonuslevels.add(45);
            megabonuslevels.add(50);
            bonuslevels.add(55);
            megabonuslevels.add(60);
            bonuslevels.add(65);
            megabonuslevels.add(70);
            bonuslevels.add(75);
            megabonuslevels.add(80);
            bonuslevels.add(85);
            megabonuslevels.add(90);
            bonuslevels.add(95);
            megabonuslevels.add(100);
            bonuslevels.add(105);
            megabonuslevels.add(110);
            bonuslevels.add(115);
            megabonuslevels.add(120);
            bonuslevels.add(125);
            megabonuslevels.add(130);
            bonuslevels.add(135);
            megabonuslevels.add(140);
            bonuslevels.add(145);
            megabonuslevels.add(150);
            bonuslevels.add(155);
            megabonuslevels.add(160);
            bonuslevels.add(165);
            megabonuslevels.add(170);
            bonuslevels.add(175);
            megabonuslevels.add(180);
            bonuslevels.add(185);
            megabonuslevels.add(190);
            bonuslevels.add(195);
            megabonuslevels.add(200);
            if (bonuslevels.contains(mylevel)) {
                mysp += 3;
                Tsp += 3;
            } else if (megabonuslevels.contains(mylevel)) {
                mysp += 5;
                Tsp += 5;
            } else {
                mysp += 2;
                Tsp += 2;
            }
            mytnl += (1000 + (150 * (mylevel - 1)));
            player.setMetadata("SkillP", new FixedMetadataValue(this, mysp));
            player.setMetadata("TotalSkillP", new FixedMetadataValue(this, Tsp));
            player.setHealth(player.getMaxHealth());
            player.setMetadata("Level", new FixedMetadataValue(this, mylevel));
        }
        player.setMetadata("Exp", new FixedMetadataValue(this, myexp));
        player.setMetadata("ExpTnl", new FixedMetadataValue(this, mytnl));
    }

    public ArrayList<String> GiveDayofWeek() {
        Date now = new Date();
        SimpleDateFormat simpleday = new SimpleDateFormat("EEEE");
        SimpleDateFormat simplehour = new SimpleDateFormat("H");
        String Day = simpleday.format(now);
        String Hour = simplehour.format(now);
        ArrayList<String> time;
        time = new ArrayList<String>();
        time.add(Day);
        time.add(Hour);
        return time;
    }

    public void checkBonus() {
        ArrayList<String> Date = GiveDayofWeek();
        if (Integer.parseInt(Date.get(1)) == 11) {
            if (this.bonustime) {
                return;
            }
            this.bonustime = true;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[RPG] " + ChatColor.RESET + "Bonus Experience Hour has begun! (150% Exp Bonus)");
        }
        if (Integer.parseInt(Date.get(1)) == 12) {
            if (!this.bonustime) {
                return;
            }
            this.bonustime = false;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[RPG] " + ChatColor.RESET + "Bonus hour has ended");
        }
        if (Integer.parseInt(Date.get(1)) == 20) {
            if (this.bonustime) {
                return;
            }
            this.bonustime = true;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[RPG] " + ChatColor.RESET + "Bonus Experience Hour! (150% Exp Bonus)");
        }
        if (Integer.parseInt(Date.get(1)) == 21) {
            if (!this.bonustime) {
                return;
            }
            this.bonustime = false;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[RPG] " + ChatColor.RESET + "Bonus hour has ended");
        }
    }

    public void clearChat(Player player) {
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("");
    }

    public void UpdatePlayer(Player player) {
        //Strength && Constitution
        Integer Strength = player.getMetadata("Strength").get(0).asInt();
        Integer MeleeBonus = Strength / 10;
        player.setMetadata("MeleeBonus", new FixedMetadataValue(this, MeleeBonus));
        Integer Con = player.getMetadata("Constitution").get(0).asInt();
        Integer PhysicalDefense = (Con / 2) + (Con / 5) + 10;
        player.setMetadata("PhysicalDefense", new FixedMetadataValue(this, PhysicalDefense));
        Integer MaxHealth = (Strength / 10) + (Con / 4) + 12;
        player.setMaxHealth(MaxHealth);

        //Dexterity & Agility
        Integer Dex = player.getMetadata("Dexterity").get(0).asInt();
        Integer RangedBonus = Dex / 10;
        Integer Agi = player.getMetadata("Agility").get(0).asInt();
        Integer Dodge = (Agi / 10) + 1;
        if (Agi >= 25) {
            Dodge += 5;
        }
        player.setMetadata("Dodge", new FixedMetadataValue(this, Dodge));
        Integer Crit = (Dex / 8) + (Agi / 12) + 1;
        player.setMetadata("RangedBonus", new FixedMetadataValue(this, RangedBonus));
        player.setMetadata("Critical", new FixedMetadataValue(this, Crit));

        //Wisdom & Intelligence
        Integer Wis = player.getMetadata("Wisdom").get(0).asInt();
        Integer Int = player.getMetadata("Intelligence").get(0).asInt();
        Integer MagicBonus = Int / 10;
        Integer MagicDefense = (Wis / 2) + (Wis / 5) + 14;
        if (Wis >= 15) {
            MagicDefense += 5;
        }
        player.setMetadata("MagicBonus", new FixedMetadataValue(this, MagicBonus));
        player.setMetadata("MagicDefense", new FixedMetadataValue(this, MagicDefense));
        Integer Mana = Wis + Int + 20;
        if (Wis >= 10) {
            Mana += 10;
        }
        if (Int >= 40) {
            Mana += 20;
        }
        player.setMetadata("Mana", new FixedMetadataValue(this, Mana));
        player.setMetadata("MaxMana", new FixedMetadataValue(this, Mana));
    }

    public Location getTopLocation(Location loc, Integer Height) {
        Location endLocation = loc;
        Integer count = 1;
        while (count <= Height) {
            if ((loc.add(0, 1, 0)).getBlock().getType() == Material.AIR) {
                count += 1;
            } else {
                loc.subtract(0, 2, 0);
                break;
            }
        }
        endLocation = loc;
        return endLocation;
    }

    public Player getTargetPlayer(Player player, Integer distance) {
        List<Entity> nearbyE = player.getNearbyEntities(distance, distance, distance);
        ArrayList<Player> nearPlayers = new ArrayList<Player>();
        for (Entity e : nearbyE) {
            if (e instanceof Player) {
                nearPlayers.add((Player) e);
            }
        }
        Player target = null;
        BlockIterator bItr = new BlockIterator(player, distance);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez;
        while (bItr.hasNext()) {

            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();
            for (Player e : nearPlayers) {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
                    target = e;
                    break;

                }
            }

        }
        return target;

    }

    public Entity getTarget(Player player, Integer distance) {
        List<Entity> nearbyE = player.getNearbyEntities(distance, distance, distance);
        Entity target = null;
        BlockIterator bItr = new BlockIterator(player, distance);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez;
        while (bItr.hasNext()) {

            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();
            for (Entity e : nearbyE) {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
                    if (e instanceof Player || e instanceof Creature) {
                        target = e;
                        break;
                    }
                }
            }

        }
        return target;

    }

    public String subMana(Player player, Integer amt) {
        String confirmed = "yes";
        Integer mana = player.getMetadata("Mana").get(0).asInt();
        if ((mana - amt) < 0) {
            confirmed = "no";
            player.sendMessage("Not enough mana");
        } else {
            mana = mana - amt;
            player.setMetadata("Mana", new FixedMetadataValue(this, mana));
        }
        return confirmed;
    }

    public void setSkillCooldown(final Player player, String skil, Integer time) {
        final String skill = skil.toLowerCase();
        this.cooldownconfig.set(player.getName() + "." + skill, "true");
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                cooldownconfig.set(player.getName() + "." + skill, "false");
            }
        }, (time * 20));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.toLowerCase().contentEquals("passives")) {
            try {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.UNDERLINE + "Passives");
                passiveList(player);
            } catch (ClassCastException error) {

            }
        }
        if (commandLabel.toLowerCase().contentEquals("givenamed")) {
            String playername = args[0];
            Integer itemid = Integer.parseInt(args[1]);
            Player receiver = Bukkit.getPlayer(playername);
            String itemname = "";
            for (String word : args) {
                if ((word == args[0] || word == args[1])) {
                } else {
                    if (word.contains("&1")) {
                        word = word.replaceAll("&1", ChatColor.DARK_BLUE + "");
                    }
                    if (word.contains("&2")) {
                        word = word.replaceAll("&2", ChatColor.DARK_GREEN + "");
                    }
                    if (word.contains("&3")) {
                        word = word.replaceAll("&3", ChatColor.DARK_AQUA + "");
                    }
                    if (word.contains("&4")) {
                        word = word.replaceAll("&4", ChatColor.DARK_RED + "");
                    }
                    if (word.contains("&5")) {
                        word = word.replaceAll("&5", ChatColor.DARK_PURPLE + "");
                    }
                    if (word.contains("&6")) {
                        word = word.replaceAll("&6", ChatColor.GOLD + "");
                    }
                    if (word.contains("&7")) {
                        word = word.replaceAll("&7", ChatColor.GRAY + "");
                    }
                    if (word.contains("&8")) {
                        word = word.replaceAll("&8", ChatColor.DARK_GRAY + "");
                    }
                    if (word.contains("&9")) {
                        word = word.replaceAll("&9", ChatColor.BLUE + "");
                    }
                    if (word.contains("&a")) {
                        word = word.replace("&a", ChatColor.GREEN + "");
                    }
                    if (word.contains("&b")) {
                        word = word.replace("&b", ChatColor.AQUA + "");
                    }
                    if (word.contains("&c")) {
                        word = word.replace("&c", ChatColor.RED + "");
                    }
                    if (word.contains("&d")) {
                        word = word.replace("&d", ChatColor.LIGHT_PURPLE + "");
                    }
                    if (word.contains("&e")) {
                        word = word.replace("&e", ChatColor.YELLOW + "");
                    }
                    if (word.contains("&f")) {
                        word = word.replace("&f", ChatColor.WHITE + "");
                    }
                    if (word.contains("&l")) {
                        word = word.replace("&l", ChatColor.BOLD + "");
                    }
                    if (word.contains("&r")) {
                        word = word.replace("&r", ChatColor.RESET + "");
                    }
                    itemname += word + " ";
                }
            }
            itemname = itemname.trim();
            ItemStack item = new ItemStack(itemid);
            ItemMeta newitem = item.getItemMeta();
            newitem.setDisplayName(itemname);
            if (item == null) {
                return true;
            }
            String name = "";
            for (String string : args) {
                name += string + " ";
            }
            name = name.trim();
            item.setItemMeta(newitem);
            receiver.getInventory().addItem(item);
            return false;
        }
        if (commandLabel.toLowerCase().contentEquals("giveexp")) {
            String name = ChatColor.stripColor(args[0]);
            Player target = Bukkit.getPlayer(name);
            Integer expgive = Integer.parseInt(args[1]);
            Integer myexp = (Integer) target.getMetadata("Exp").get(0).value();
            Integer mytnl = (Integer) target.getMetadata("ExpTnl").get(0).value();
            Integer mylevel = (Integer) target.getMetadata("Level").get(0).value();
            myexp += expgive;
            mytnl -= expgive;
            target.sendMessage(ChatColor.GREEN + "You've gained " + expgive + " experience!");
            if (mytnl <= 0) {
                mylevel += 1;
                target.sendMessage(ChatColor.GREEN + "You have gained skill points!");
                Integer mysp = (Integer) target.getMetadata("SkillP").get(0).value();
                Integer Tsp = (Integer) target.getMetadata("TotalSkillP").get(0).value();
                ArrayList<Integer> bonuslevels = new ArrayList<Integer>();
                ArrayList<Integer> megabonuslevels = new ArrayList<Integer>();
                bonuslevels.add(5);
                megabonuslevels.add(10);
                bonuslevels.add(15);
                megabonuslevels.add(20);
                bonuslevels.add(25);
                megabonuslevels.add(30);
                bonuslevels.add(35);
                megabonuslevels.add(40);
                bonuslevels.add(45);
                megabonuslevels.add(50);
                bonuslevels.add(55);
                megabonuslevels.add(60);
                bonuslevels.add(65);
                megabonuslevels.add(70);
                bonuslevels.add(75);
                megabonuslevels.add(80);
                bonuslevels.add(85);
                megabonuslevels.add(90);
                bonuslevels.add(95);
                megabonuslevels.add(100);
                bonuslevels.add(105);
                megabonuslevels.add(110);
                bonuslevels.add(115);
                megabonuslevels.add(120);
                bonuslevels.add(125);
                megabonuslevels.add(130);
                bonuslevels.add(135);
                megabonuslevels.add(140);
                bonuslevels.add(145);
                megabonuslevels.add(150);
                bonuslevels.add(155);
                megabonuslevels.add(160);
                bonuslevels.add(165);
                megabonuslevels.add(170);
                bonuslevels.add(175);
                megabonuslevels.add(180);
                bonuslevels.add(185);
                megabonuslevels.add(190);
                bonuslevels.add(195);
                megabonuslevels.add(200);
                if (bonuslevels.contains(mylevel)) {
                    mysp += 3;
                    Tsp += 3;
                } else if (megabonuslevels.contains(mylevel)) {
                    mysp += 5;
                    Tsp += 5;
                } else {
                    mysp += 2;
                    Tsp += 2;
                }
                mytnl += (1000 + (150 * (mylevel - 1)));
                target.setMetadata("SkillP", new FixedMetadataValue(this, mysp));
                target.setMetadata("TotalSkillP", new FixedMetadataValue(this, Tsp));
                target.setHealth(target.getMaxHealth());
                target.setMetadata("Level", new FixedMetadataValue(this, mylevel));
            }
            target.setMetadata("Exp", new FixedMetadataValue(this, myexp));
            target.setMetadata("ExpTnl", new FixedMetadataValue(this, mytnl));
        }
        if (commandLabel.toLowerCase().contentEquals("sendchat")) {
            Player target = Bukkit.getPlayer(args[0]);
            String Message = "";
            Integer count = 0;
            for (String word : args) {
                if (count == 0) {
                    count += 1;
                } else {
                    try {
                        if (word.contains("&1")) {
                            word = word.replaceAll("&1", ChatColor.DARK_BLUE + "");
                        }
                        if (word.contains("&2")) {
                            word = word.replaceAll("&2", ChatColor.DARK_GREEN + "");
                        }
                        if (word.contains("&3")) {
                            word = word.replaceAll("&3", ChatColor.DARK_AQUA + "");
                        }
                        if (word.contains("&4")) {
                            word = word.replaceAll("&4", ChatColor.DARK_RED + "");
                        }
                        if (word.contains("&5")) {
                            word = word.replaceAll("&5", ChatColor.DARK_PURPLE + "");
                        }
                        if (word.contains("&6")) {
                            word = word.replaceAll("&6", ChatColor.GOLD + "");
                        }
                        if (word.contains("&7")) {
                            word = word.replaceAll("&7", ChatColor.GRAY + "");
                        }
                        if (word.contains("&8")) {
                            word = word.replaceAll("&8", ChatColor.DARK_GRAY + "");
                        }
                        if (word.contains("&9")) {
                            word = word.replaceAll("&9", ChatColor.BLUE + "");
                        }
                        if (word.contains("&a")) {
                            word = word.replace("&a", ChatColor.GREEN + "");
                        }
                        if (word.contains("&b")) {
                            word = word.replace("&b", ChatColor.AQUA + "");
                        }
                        if (word.contains("&c")) {
                            word = word.replace("&c", ChatColor.RED + "");
                        }
                        if (word.contains("&d")) {
                            word = word.replace("&d", ChatColor.LIGHT_PURPLE + "");
                        }
                        if (word.contains("&e")) {
                            word = word.replace("&e", ChatColor.YELLOW + "");
                        }
                        if (word.contains("&f")) {
                            word = word.replace("&f", ChatColor.WHITE + "");
                        }
                        if (word.contains("&l")) {
                            word = word.replace("&l", ChatColor.BOLD + "");
                        }
                        if (word.contains("&r")) {
                            word = word.replace("&r", ChatColor.RESET + "");
                        }
                    } catch (ClassCastException e) {
                        //
                    }
                    Message += word + " ";
                }
            }
            target.sendMessage(Message);
            return false;
        }
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (commandLabel.toLowerCase().contentEquals("spec")) {
                if (!this.specPlayers.contains(player.getName())) {
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.RED + "Need a target to spectate");
                        return true;
                    }
                    this.specPlayers.add(player.getName());
                    this.vanishedPlayers.add(player.getName());
                    Player[] onlineplayers = Bukkit.getOnlinePlayers();
                    for (Player players : onlineplayers) {
                        players.hidePlayer(player);
                    }
                    player.chat("/setcheckpoint");
                    final Player target = Bukkit.getPlayer(args[0]);
                    player.sendMessage(ChatColor.GREEN + "You are spectating " + target.getName() + "!");
                    player.teleport(target);
                    player.hidePlayer(target);
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    player.setMetadata("Spectate", new FixedMetadataValue(this, "True"));
                    Integer task = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                        public void run() {
                            if (player.getMetadata("Spectate").get(0).toString() == "False") {
                                Bukkit.getScheduler().cancelTask((Integer) player.getMetadata("Spectatetaskid").get(0).asInt());
                            }
                            player.teleport(target);
                        }
                    }, 0, 1);
                    player.setMetadata("Spectatetaskid", new FixedMetadataValue(this, task));
                } else {
                    this.vanishedPlayers.remove(player.getName());
                    player.setMetadata("Spectate", new FixedMetadataValue(this, "False"));
                    player.sendMessage(ChatColor.GREEN + "You are done spectating");
                    Double x = (Double) this.checkpointConfig.get(player.getName() + ".X");
                    Double y = (Double) this.checkpointConfig.get(player.getName() + ".Y");
                    Double z = (Double) this.checkpointConfig.get(player.getName() + ".Z");
                    World world = null;
                    float yaw = this.checkpointConfig.getInt(player.getName() + ".yaw");
                    float pitch = this.checkpointConfig.getInt(player.getName() + ".pitch");
                    try {
                        world = Bukkit.getWorld(this.checkpointConfig.getString(player.getName() + ".world"));
                    } catch (IllegalArgumentException error2) {
                        //
                    }
                    Location loc = new Location(world, x, y, z, yaw, pitch);
                    player.teleport(loc);
                    Player[] onlineplayers = Bukkit.getOnlinePlayers();
                    for (Player players : onlineplayers) {
                        players.showPlayer(player);
                    }
                    this.specPlayers.remove(player.getName());
                }
            }
            if (commandLabel.toLowerCase().contentEquals("vanish")) {
                if (this.vanishedPlayers.contains(player.getName())) {
                    this.vanishedPlayers.remove(player.getName());
                    player.sendMessage(ChatColor.GREEN + "You are now visible");
                    Player[] onlineplayers = Bukkit.getOnlinePlayers();
                    for (Player players : onlineplayers) {
                        players.showPlayer(player);
                    }
                } else {
                    player.sendMessage(ChatColor.GREEN + "You are invisible!");
                    this.vanishedPlayers.add(player.getName());
                    Player[] onlineplayers = Bukkit.getOnlinePlayers();
                    for (Player players : onlineplayers) {
                        players.hidePlayer(player);
                    }
                }
            }
            if (commandLabel.toLowerCase().contentEquals("listgraveyards")) {
                Set<String> graveyards = this.graveyardConfig.getKeys(true);
                List<String> graves = new ArrayList<String>();
                for (String grave : graveyards) {
                    if (grave.toLowerCase().contains("x") || grave.toLowerCase().contains("y") || grave.toLowerCase().contains("z") || grave.toLowerCase().contains("yaw") || grave.toLowerCase().contains("pitch") || grave.toLowerCase().contains("world")) {
                        //pass
                    } else {
                        graves.add(grave);
                    }
                }
                player.sendMessage(ChatColor.BLUE + "Graves:");
                for (String grave : graves) {
                    player.sendMessage(grave);
                }
            }
            if (commandLabel.toLowerCase().contentEquals("setgraveyard")) {
                String name = "";
                for (String word : args) {
                    name += word + " ";
                }
                name = name.trim();
                Double locX = player.getLocation().getX();
                Double locY = player.getLocation().getY();
                Double locZ = player.getLocation().getZ();
                float pitch = player.getLocation().getPitch();
                float yaw = player.getLocation().getYaw();
                this.graveyardConfig.set(name + ".X", locX);
                this.graveyardConfig.set(name + ".Y", locY);
                this.graveyardConfig.set(name + ".Z", locZ);
                this.graveyardConfig.set(name + ".pitch", pitch);
                this.graveyardConfig.set(name + ".yaw", yaw);
                this.graveyardConfig.set(name + ".world", player.getLocation().getWorld().getName().toString());
                player.sendMessage(ChatColor.BLUE + "Graveyard set with name ");
                try {
                    this.graveyardConfig.save(this.graveyard);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //Checkpoint
            if (commandLabel.toLowerCase().contentEquals("setcheckpoint")) {
                Double locX = player.getLocation().getX();
                Double locY = player.getLocation().getY();
                Double locZ = player.getLocation().getZ();
                float pitch = player.getLocation().getPitch();
                float yaw = player.getLocation().getYaw();
                this.checkpointConfig.set(player.getName() + ".X", locX);
                this.checkpointConfig.set(player.getName() + ".Y", locY);
                this.checkpointConfig.set(player.getName() + ".Z", locZ);
                this.checkpointConfig.set(player.getName() + ".pitch", pitch);
                this.checkpointConfig.set(player.getName() + ".yaw", yaw);
                this.checkpointConfig.set(player.getName() + ".world", player.getLocation().getWorld().getName().toString());
                player.sendMessage(ChatColor.YELLOW + "Checkpoint set");
                try {
                    this.checkpointConfig.save(this.checkpoint);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (commandLabel.toLowerCase().contentEquals("clearcheckpoint")) {
                this.checkpointConfig.set(player.getName(), null);
            }
            if (commandLabel.equalsIgnoreCase("stats")) {
                Integer Exp = (Integer) player.getMetadata("Exp").get(0).value();
                Integer Exptnl = (Integer) player.getMetadata("ExpTnl").get(0).value();
                Integer SkillP = (Integer) player.getMetadata("SkillP").get(0).value();
                Integer Str = (Integer) player.getMetadata("Strength").get(0).value();
                Integer Con = (Integer) player.getMetadata("Constitution").get(0).value();
                Integer Dex = (Integer) player.getMetadata("Dexterity").get(0).value();
                Integer Agi = (Integer) player.getMetadata("Agility").get(0).value();
                Integer Wis = (Integer) player.getMetadata("Wisdom").get(0).value();
                Integer Int = (Integer) player.getMetadata("Intelligence").get(0).value();
                Integer powerlevel = (Str + Con + Dex + Agi + Wis + Int) / 6;
                Integer TotalSkillP = Str + Con + Dex + Agi + Wis + Int + SkillP - 6;
                player.sendMessage("PowerLevel : " + powerlevel);
                player.sendMessage("Experience : " + Exp);
                player.sendMessage("Exptnl : " + Exptnl);
                player.sendMessage(ChatColor.RED + "Str : " + Str + "   " + ChatColor.GOLD + "Con : " + Con);
                player.sendMessage(ChatColor.DARK_GREEN + "Dex : " + Dex + "   " + ChatColor.GREEN + "Agi : " + Agi);
                player.sendMessage(ChatColor.BLUE + "Wis : " + Wis + "   " + ChatColor.DARK_PURPLE + "Int : " + Int);
                player.sendMessage("Available Skill Points : " + SkillP + " / (" + TotalSkillP + " total)");
            }
            if (commandLabel.equalsIgnoreCase("resetpoints")) {
                Integer Str = (Integer) player.getMetadata("Strength").get(0).value();
                Integer Con = (Integer) player.getMetadata("Constitution").get(0).value();
                Integer Dex = (Integer) player.getMetadata("Dexterity").get(0).value();
                Integer Agi = (Integer) player.getMetadata("Agility").get(0).value();
                Integer Wis = (Integer) player.getMetadata("Wisdom").get(0).value();
                Integer Int = (Integer) player.getMetadata("Intelligence").get(0).value();
                Integer TotalSkillP = Str + Con + Dex + Agi + Wis + Int + player.getMetadata("SkillP").get(0).asInt() - 6;
                player.setMetadata("Strength", new FixedMetadataValue(this, 1));
                player.setMetadata("Dexterity", new FixedMetadataValue(this, 1));
                player.setMetadata("Agility", new FixedMetadataValue(this, 1));
                player.setMetadata("Constitution", new FixedMetadataValue(this, 1));
                player.setMetadata("Wisdom", new FixedMetadataValue(this, 1));
                player.setMetadata("Intelligence", new FixedMetadataValue(this, 1));
                player.setMetadata("SkillP", new FixedMetadataValue(this, TotalSkillP));
                this.skillsConfig.set(player.getName(), null);
                player.sendMessage(ChatColor.BLUE + "Your stats have been reset");
            }
            if (commandLabel.equalsIgnoreCase("addstat")) {
                String stat = args[0].toLowerCase();
                if ("strength".contains(stat)) {
                    stat = "Strength";
                } else if ("constitution".contains(stat)) {
                    stat = "Constitution";
                } else if ("dexterity".contains(stat)) {
                    stat = "Dexterity";
                } else if ("agility".contains(stat)) {
                    stat = "Agility";
                } else if ("wisdom".contains(stat)) {
                    stat = "Wisdom";
                } else if ("intelligence".contains(stat)) {
                    stat = "Intelligence";
                } else {
                    player.sendMessage("Invalid stat type, '" + stat + "'");
                }
                if (!((Integer) player.getMetadata("SkillP").get(0).asInt() >= 1)) {
                    player.sendMessage("Not enough skill points to spend");
                    return false;
                }
                Integer result = (Integer) player.getMetadata(stat).get(0).value();
                ;
                if (result > 50) {
                    player.sendMessage("This stat is maxed out!");
                    return false;
                }
                result += 1;
                player.setMetadata(stat, new FixedMetadataValue(this, result));
                // lower skill points by 1
                Integer skillp = (Integer) player.getMetadata("SkillP").get(0).value();
                ;
                skillp -= 1;
                player.setMetadata("SkillP", new FixedMetadataValue(this, skillp));
                this.playersConfig.set("Players." + player.getName() + "." + stat, result);
                this.playersConfig.set("Players." + player.getName() + ".SkillPoints", result);
                player.sendMessage("You have upgraded " + stat);

                //update players extended stats
                UpdatePlayer(player);
            }
            if (commandLabel.equalsIgnoreCase("bindskill")) {
                String skill = "";
                for (String string : args) {
                    skill += string + " ";
                }
                skill = skill.trim();
                if (skillsCheck(player, skill)) {
                    player.sendMessage("You bound, " + skill + " to the item, " + player.getItemInHand().getType());
                    this.skillsConfig.set(player.getName() + "." + player.getItemInHand().getType().toString(), skill);
                    try {
                        this.skillsConfig.save(this.Skills);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    if (skill == "") {
                        player.sendMessage("You bound, " + skill + " to the item, " + player.getItemInHand().getType());
                        this.skillsConfig.set(player.getName() + "." + player.getItemInHand().getType().toString(), skill);
                        try {
                            this.skillsConfig.save(this.Skills);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "That skill is not available to bind");
                    }
                }
            }
            if (commandLabel.equalsIgnoreCase("cast")) {
                String Skill = "";
                for (String string : args) {
                    Skill += string + " ";
                }
                Skill = Skill.trim();
                onPlayerCast(player, Skill);
            }
            if (commandLabel.equalsIgnoreCase("rename")) {
                ItemMeta item = player.getItemInHand().getItemMeta();
                if (item == null) {
                    player.sendMessage(ChatColor.RED + "You need an item in your hand to rename it");
                    return true;
                }
                String name = "";
                for (String string : args) {
                    name += string + " ";
                }
                name = name.trim();
                player.sendMessage("Setting your item to name, " + name);
                ItemStack weapontype = new ItemStack(player.getItemInHand().getType());
                if (this.gearConfig.getConfigurationSection("Weapons").contains(name)) {
                    weapontype = new ItemStack(this.gearConfig.getInt("Weapons." + name + ".material"));
                    String rarity = this.gearConfig.get("Weapons." + name + ".rarity").toString();
                    if (rarity.contentEquals("common")) {
                        item.setDisplayName(ChatColor.GRAY + name);
                    }
                    if (rarity.contentEquals("uncommon")) {
                        item.setDisplayName(ChatColor.GREEN + name);
                    }
                    if (rarity.contentEquals("rare")) {
                        item.setDisplayName(ChatColor.AQUA + name);
                    }
                    if (rarity.contentEquals("very rare")) {
                        item.setDisplayName(ChatColor.DARK_PURPLE + name);
                    }
                    if (rarity.contentEquals("epic")) {
                        item.setDisplayName(ChatColor.RED + name);
                    }
                    if (rarity.contentEquals("godly")) {
                        item.setDisplayName(ChatColor.GOLD + name);
                    }
                    List<String> Lore = this.gearConfig.getStringList("Weapons." + name + ".description");
                    item.setLore(Lore);
                } else if (this.gearConfig.getConfigurationSection("Armor").contains(name)) {
                    weapontype = new ItemStack(this.gearConfig.getInt("Armor." + name + ".material"));
                    String rarity = this.gearConfig.getString("Armor." + name + ".rarity").trim();
                    if (rarity.contentEquals("common")) {
                        item.setDisplayName(ChatColor.GRAY + name);
                    }
                    if (rarity.contentEquals("uncommon")) {
                        item.setDisplayName(ChatColor.GREEN + name);
                    }
                    if (rarity.contentEquals("rare")) {
                        item.setDisplayName(ChatColor.AQUA + name);
                    }
                    if (rarity.contentEquals("very rare")) {
                        item.setDisplayName(ChatColor.DARK_PURPLE + name);
                    }
                    if (rarity.contentEquals("epic")) {
                        item.setDisplayName(ChatColor.RED + name);
                    }
                    if (rarity.contentEquals("godly")) {
                        item.setDisplayName(ChatColor.GOLD + name);
                    }
                    List<String> Lore = this.gearConfig.getStringList("Armor." + name + ".description");
                    item.setLore(Lore);
                }
                player.setItemInHand(weapontype);
                player.getItemInHand().setItemMeta(item);
                return false;
            }
            if (commandLabel.equalsIgnoreCase("battlestats")) {
                Integer MeB = (Integer) player.getMetadata("MeleeBonus").get(0).value();
                Integer RaB = (Integer) player.getMetadata("RangedBonus").get(0).value();
                Integer MaB = (Integer) player.getMetadata("MagicBonus").get(0).value();
                Integer PAtk = (Integer) (player.getMetadata("Strength").get(0).asInt() / 2) + 10;
                Integer RAtk = (Integer) (player.getMetadata("Dexterity").get(0).asInt() / 2) + 10;
                Integer PhyDef = (int) getPlayerDefense(player);
                Integer MAtk = (Integer) (player.getMetadata("Intelligence").get(0).asInt() / 2) + 14;
                Integer MagDef = (Integer) player.getMetadata("MagicDefense").get(0).value();
                Integer MaxH = (int) player.getMaxHealth();
                Integer MaxM = (Integer) player.getMetadata("MaxMana").get(0).value();
                Integer CurH = (int) player.getHealth();
                Integer CurM = (Integer) player.getMetadata("Mana").get(0).value();
                Integer Crit = (Integer) player.getMetadata("Critical").get(0).value();
                Integer Dodge = (Integer) player.getMetadata("Dodge").get(0).value();
                clearChat(player);
                player.sendMessage(ChatColor.GREEN + "Health" + ChatColor.RESET + " : " + CurH + "/" + MaxH);
                player.sendMessage(ChatColor.BLUE + "Mana" + ChatColor.RESET + " : " + CurM + "/" + MaxM);
                player.sendMessage(ChatColor.UNDERLINE + "Damage Bonuses");
                player.sendMessage(ChatColor.RED + "Melee :  " + MeB);
                player.sendMessage(ChatColor.DARK_GREEN + "Ranged : " + RaB);
                player.sendMessage(ChatColor.DARK_PURPLE + "Magic :  " + MaB);
                player.sendMessage(ChatColor.UNDERLINE + "Combat Ratings");
                player.sendMessage(ChatColor.RED + "Melee-Atk : " + PAtk);
                player.sendMessage(ChatColor.DARK_GREEN + "Ranged-Atk : " + RAtk);
                player.sendMessage(ChatColor.GOLD + "Physical-Def : " + PhyDef);
                player.sendMessage(ChatColor.DARK_PURPLE + "Magic-Atk : " + MAtk);
                player.sendMessage(ChatColor.BLUE + "Magic-Def : " + MagDef);
                player.sendMessage(ChatColor.UNDERLINE + "Misc stats");
                player.sendMessage("Critical : " + Crit + "%");
                player.sendMessage("Dodge : " + Dodge + "%");
            }
            if (commandLabel.toLowerCase().contains("spells")) {
                ArrayList<String> skills = skillList(player);
                String Skilllist = "Available Spells :";
                for (String skill : skills) {
                    Skilllist += " " + skill + ",";
                }
                player.sendMessage(Skilllist);
            }
        }
        return true;
    }

    private boolean skillsCheck(Player p, String skill) {
        ArrayList<String> skills = skillList(p);
        ArrayList<String> check = new ArrayList<String>();
        for (String skilllist : skills) {
            check.add(ChatColor.stripColor(skilllist.toLowerCase()));
        }
        if (check.contains(skill.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    private void passiveList(Player p) {
        Integer Str = p.getMetadata("Strength").get(0).asInt();
        if (Str >= 30) {
            p.sendMessage(ChatColor.RED + "Smack");
            p.sendMessage(" - This passive skill gives you a 4% chance to knockback an enemy");
        }
        Integer Con = p.getMetadata("Constitution").get(0).asInt();
        if (Con >= 30) {
            p.sendMessage(ChatColor.GOLD + "HP regeneration I");
            p.sendMessage(" - This increases your hp regeneration by 1");
        }
        if (Con >= 40) {
            p.sendMessage(ChatColor.GOLD + "Burning Blood Upgrade");
            p.sendMessage(" - Increases the strength of Burning Blood");
        }
        if (Con >= 50) {
            p.sendMessage(ChatColor.GOLD + "Block Chance I");
            p.sendMessage(" - Gives you a 10% chance to block incoming damage");
        }
        Integer Dex = p.getMetadata("Dexterity").get(0).asInt();
        if (Dex >= 15) {
            p.sendMessage(ChatColor.DARK_GREEN + "Improved Critical I");
            p.sendMessage(" - Gives a 10% bonus to Critical Damage");
        }
        if (Dex >= 25) {
            p.sendMessage(ChatColor.DARK_GREEN + "Arrow Retrieval I     (coming soon)");
            p.sendMessage(" - Gives a 20% chance to gain your arrow back");
        }
        Integer Agi = p.getMetadata("Agility").get(0).asInt();
        if (Agi >= 25) {
            p.sendMessage(ChatColor.GREEN + "Increased Evasion I");
            p.sendMessage(" - Increases your chance to dodge by 5%");
        }
        if (Agi >= 40) {
            p.sendMessage(ChatColor.GREEN + "Projectile Evasion I");
            p.sendMessage(" - Doubles your chance to dodge arrows");
        }
        Integer Wis = p.getMetadata("Wisdom").get(0).asInt();
        if (Wis >= 10) {
            p.sendMessage(ChatColor.BLUE + "Bonus Mana I");
            p.sendMessage(" - Increases your mana pool by 10");
        }
        if (Wis >= 15) {
            p.sendMessage(ChatColor.BLUE + "Magic Defense I");
            p.sendMessage(" - Increases your magic defense by 5");
        }
        if (Wis >= 40) {
            p.sendMessage(ChatColor.BLUE + "Mana Regen I");
            p.sendMessage(" - Increases your mana regeneration by 2");
        }
        Integer Int = p.getMetadata("Intelligence").get(0).asInt();
        if (Int >= 20) {
            p.sendMessage(ChatColor.DARK_PURPLE + "Mana Regen I");
            p.sendMessage(" - Increases your mana regeneration by 1");
        }
        if (Int >= 30) {
            p.sendMessage(ChatColor.DARK_PURPLE + "Bonus Mana I");
            p.sendMessage(" - Increases your mana pool by 20");
        }
    }

    private ArrayList<String> skillList(Player p) {
        ArrayList<String> skills = new ArrayList<String>();
        Integer Str = p.getMetadata("Strength").get(0).asInt();
        if (Str >= 5) {
            skills.add(ChatColor.RED + "Bash I");
        }
        if (Str >= 10) {
            skills.add(ChatColor.RED + "Lunge");
        }
        if (Str >= 20) {
            skills.add(ChatColor.RED + "Hard Bash");
        }
        //if(Str >= 30){
        //	skills.add("Smack");
        //}
        if (Str >= 40) {
            skills.add(ChatColor.RED + "Whirlwind");
        }
        if (Str >= 50) {
            skills.add(ChatColor.RED + "Bash II");
        }
        //if(Str >= 60){
        //	skills.add(ChatColor.RED + "Rage");
        //}
        Integer Con = p.getMetadata("Constitution").get(0).asInt();
        if (Con >= 5) {
            skills.add(ChatColor.GOLD + "Taunt");
        }
        if (Con >= 10) {
            skills.add(ChatColor.GOLD + "Burning Blood");
        }
        if (Con >= 20) {
            skills.add(ChatColor.GOLD + "Sentinel");
        }
        //if(Con >= 30){
        //	skills.add("HP regen I");
        //}
        //if(Con >= 40){
        //	skills.add("Burning Blood Upgrade");
        //}
        //if(Con >= 50){
        //	skills.add("Block Chance I");
        //}
        if (Con >= 60) {
            skills.add(ChatColor.GOLD + "Stone defense");
        }
        Integer Dex = p.getMetadata("Dexterity").get(0).asInt();
        if (Dex >= 5) {
            skills.add(ChatColor.DARK_GREEN + "Power Shot I");
        }
        if (Dex >= 10) {
            skills.add(ChatColor.DARK_GREEN + "Invisible I");
        }
        if (Dex >= 20) {
            skills.add(ChatColor.DARK_GREEN + "Poison Shot");
        }
        if (Dex >= 30) {
            skills.add(ChatColor.DARK_GREEN + "Cripple Shot");
        }
        if (Dex >= 40) {
            skills.add(ChatColor.DARK_GREEN + "Power Shot II");
        }
        if (Dex >= 50) {
            skills.add(ChatColor.DARK_GREEN + "Teleport Shot");
        }
        if (Dex >= 60) {
            skills.add(ChatColor.DARK_GREEN + "Arrow Rain");
        }
        if (Dex >= 70) {
            skills.add(ChatColor.DARK_GREEN + "Invisible II");
        }
        if (Dex >= 150) {
            skills.add(ChatColor.DARK_GREEN + "Invisible III");
        }
        Integer Agi = p.getMetadata("Agility").get(0).asInt();
        if (Agi >= 5) {
            skills.add(ChatColor.GREEN + "Backstep");
        }
        if (Agi >= 10) {
            skills.add(ChatColor.GREEN + "Stab");
        }
        if (Agi >= 20) {
            skills.add(ChatColor.GREEN + "Jump");
        }
        if (Agi >= 30) {
            skills.add(ChatColor.GREEN + "Backstab");
        }
        //if(Agi >= 40){
        //	skills.add("Projectile Evasion");
        //}
        if (Agi >= 50) {
            skills.add(ChatColor.GREEN + "Life Steal");
        }
        if (Agi >= 60) {
            skills.add(ChatColor.GREEN + "Serrated Blade");
        }
        Integer Wis = p.getMetadata("Wisdom").get(0).asInt();
        if (Wis >= 5) {
            skills.add(ChatColor.BLUE + "Heal I");
        }
        //if(Wis >= 10){
        //	skills.add("Bonus Mana I");
        //}
        if (Wis >= 20) {
            skills.add(ChatColor.BLUE + "Heal II");
        }
        if (Wis >= 30) {
            skills.add(ChatColor.BLUE + "Regeneration");
        }
        //if(Wis >= 40){
        //	skills.add("Mana Regen I");
        //}
        if (Wis >= 50) {
            skills.add(ChatColor.BLUE + "Group Heal");
        }
        if (Wis >= 60) {
            skills.add(ChatColor.BLUE + "Heal III");
        }
        Integer Int = p.getMetadata("Intelligence").get(0).asInt();
        if (Int >= 5) {
            skills.add(ChatColor.DARK_PURPLE + "Fireball I");
        }
        if (Int >= 10) {
            skills.add(ChatColor.DARK_PURPLE + "Poison");
        }
        //if(Int >= 20){
        //	skills.add("Mana Regen I");
        //}
        //if(Int >= 30){
        //	skills.add("Bonus Mana I");
        //}
        if (Int >= 40) {
            skills.add(ChatColor.DARK_PURPLE + "Slow");
        }
        if (Int >= 50) {
            skills.add(ChatColor.DARK_PURPLE + "Fireball II");
        }
        if (Int >= 60) {
            skills.add(ChatColor.DARK_PURPLE + "Teleport");
        }
        if (Int >= 70) {
            skills.add(ChatColor.DARK_PURPLE + "Lightning Storm");
        }
        //Super Skills
        if (Str >= 100 && Dex >= 100) {
            skills.add("Sniper Shot");
        }
        if (Str >= 100 && Int >= 100) {
            skills.add("Meteor Strike");
        }
        return skills;
    }

    private Integer getLevel(Entity entity) {
        Integer Level = 1;
        if (entity instanceof Creature) {

        }
        return Level;
    }
}