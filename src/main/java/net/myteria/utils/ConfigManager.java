package net.myteria.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import net.myteria.HousingAPI;
import net.myteria.PlayerHousing;

public class ConfigManager {
	private File configFile;
	
    public File get(String folder, String fileName) {
    	configFile = new File(folder, fileName);
        return configFile;
    	
    }
    public ConfigManager() {
    	File folder = new File("housing");
    	if (!folder.exists()) {
    		folder.mkdir();
    	}
    }
    
    public boolean hasWorld(UUID uuid) {
    	File file = new File("housing/" + uuid + "/" +  uuid + ".yml");
    	return file.exists();
    }
    
    public File getFile(UUID uuid) {
    	File file = new File("housing/" + uuid + "/" +  uuid + ".yml");
    	return file;
    }
    
    public void setDefaults(UUID uuid, String world) {
    	File file = get("housing/" + uuid, uuid + ".yml");
    	
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    	List<UUID> whitelist = new ArrayList<UUID>();
    	whitelist.add(uuid);
    	
    	config.set("default-world", world);
    	config.set(world + ".whitelist", whitelist);
    	config.set(world + ".banned", new ArrayList<String>());
    	config.set(world + ".settings.gamemode", "ADVENTURE");
    	config.set(world + ".settings.status", "PRIVATE");
    	config.set(world + ".settings.pvp", false);
    	config.set(world + ".settings.difficulty", "EASY");
    	
    	config.set(world + ".gamerules.blockExplosionDropDecay", false);
    	config.set(world + ".gamerules.doDaylightCycle", false);
    	config.set(world + ".gamerules.doEntityDrops", false);
    	config.set(world + ".gamerules.doFireTick", false);
    	config.set(world + ".gamerules.doInsomnia", false);
    	config.set(world + ".gamerules.doLimitedCrafting", false);
    	config.set(world + ".gamerules.doMobLoot", false);
    	config.set(world + ".gamerules.doMobSpawning", false);
    	config.set(world + ".gamerules.doPatrolSpawning", false);
    	config.set(world + ".gamerules.doTileDrops", false);
    	config.set(world + ".gamerules.doTraderSpawning", false);
    	config.set(world + ".gamerules.doVinesSpread", false);
    	config.set(world + ".gamerules.doWeatherCycle", false);
    	config.set(world + ".gamerules.doWardenSpawning", false);
    	config.set(world + ".gamerules.drowningDamage", false);
    	config.set(world + ".gamerules.enderPearlsVanishOnDeath", false);
    	config.set(world + ".gamerules.fallDamage", false);
    	config.set(world + ".gamerules.fireDamage", false);
    	config.set(world + ".gamerules.forgiveDeadPlayers", false);
    	config.set(world + ".gamerules.freezeDamage", false);
    	config.set(world + ".gamerules.keepInventory", false);
    	config.set(world + ".gamerules.lavaSourceConversion", false);
    	config.set(world + ".gamerules.logAdminCommands", false);
    	config.set(world + ".gamerules.mobExplosionDropDecay", false);
    	config.set(world + ".gamerules.mobGriefing", false);
    	config.set(world + ".gamerules.naturalRegeneration", false);
    	config.set(world + ".gamerules.randomTickSpeed", 3);
    	config.set(world + ".gamerules.reducedDebugInfo", false);
    	config.set(world + ".gamerules.tntExplosionDropDecay", false);
    	
    	config.set(world + ".ranks.default.members", new ArrayList<String>());
    	config.set(world + ".ranks.default.permissions", new ArrayList<String>());
    	config.set(world + ".ranks.trusted.members", whitelist);
    	config.set(world + ".ranks.trusted.permissions", new ArrayList<String>());
    	try {
			config.save(file);
			PlayerHousing.getAPI().addWorldConfig(uuid, config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    public void verifyConfig(UUID uuid, String world) {    	
    	if (PlayerHousing.getAPI().getWorldConfig(uuid) == null) {
    		setDefaults(uuid, world);
    		return;
    	}
    	// Implementation details omitted for brevity
    }

}
