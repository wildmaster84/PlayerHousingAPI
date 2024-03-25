/**
 * The HousingAPI class provides various methods for managing player housing in a Minecraft server.
 * This API interacts with LuckPerms for permission management and allows for creating, managing, and joining custom worlds for players.
 */
package net.myteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.util.TriState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.validation.ContentValidationException;
import net.myteria.menus.BannedMenu;
import net.myteria.menus.GameRulesMenu;
import net.myteria.menus.HousingMenu;
import net.myteria.menus.OnlinePlayersMenu;
import net.myteria.menus.OptionsMenu;
import net.myteria.menus.PermissionsMenu;
import net.myteria.menus.PlayerManagerMenu;
import net.myteria.menus.SettingsMenu;
import net.myteria.menus.WhitelistMenu;
import net.myteria.menus.WorldsMenu;
import net.myteria.utils.ConfigManager;
import net.myteria.utils.WorldUtils;
import net.myteria.utils.voidWorld;

import net.luckperms.api.*;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.node.Node;

public final class HousingAPI {
    // LuckPerms instance
    private LuckPerms lp = LuckPermsProvider.get();
    // Configuration manager
    private ConfigManager configManager = new ConfigManager();
    // World utility methods
    private WorldUtils worldUtils = new WorldUtils();
    // Instance of PlayerHousing
    private static PlayerHousing instance = PlayerHousing.getInstance();
    
    // Hash maps for various purposes
    public HashMap<Player, Integer> gameRulesPage = new HashMap<>();
    public HashMap<Player, Player> inventoryTarget = new HashMap<>();
    public HashMap<Player, Inventory> gameRulesInv = new HashMap<>();
    public HashMap<Player, Integer> worldsMenuPage = new HashMap<>();
    public HashMap<Player, Inventory> worldsMenuInv = new HashMap<>();
    public HashMap<OfflinePlayer, World> worlds = new HashMap<>();
    public HashMap<UUID, YamlConfiguration> worldConfigs = new HashMap<>();
    public HashMap<Player, Integer> playersPage = new HashMap<>();
    public HashMap<Player, Inventory> playersInv = new HashMap<>();
    public HashMap<Player, Integer> permissionsPage = new HashMap<>();
    public HashMap<Player, Inventory> permissionsInv = new HashMap<>();
    public List<ItemStack> presets = new ArrayList<>();
    public List<String> permissionNodes = new ArrayList<>();

    /**
     * Enumeration representing various actions that can be performed.
     */
    public enum Action {
        Manage,
        Whitelist,
        Banned,
        addWhitelist,
        removeWhitelist,
        kick,
        ban,
        unban, 
        addGroupPermission,
        removeGroupPermission
    }
    
    /**
     * Retrieves the GameRulesMenu instance.
     * @return The GameRulesMenu instance.
     */
    public GameRulesMenu getGameRulesMenu() {
        return null;
    }
    
    /**
     * Retrieves the SettingsMenu instance.
     * @return The SettingsMenu instance.
     */
    public SettingsMenu getSettingsMenu() {
        return null;
    }
    
    /**
     * Retrieves the HousingMenu instance.
     * @return The HousingMenu instance.
     */
    public HousingMenu getHousingMenu() {
        return instance.housingMenu;
    }
    
    /**
     * Retrieves the OnlinePlayersMenu instance.
     * @return The OnlinePlayersMenu instance.
     */
    public OnlinePlayersMenu getOnlinePlayersMenu() {
        return null;
    }
    
    /**
     * Retrieves the PlayerManagerMenu instance.
     * @return The PlayerManagerMenu instance.
     */
    public PlayerManagerMenu getPlayerManagerMenu() {
        return null;
    }
    
    /**
     * Retrieves the WhitelistMenu instance.
     * @return The WhitelistMenu instance.
     */
    public WhitelistMenu getWhitelistMenu() {
        return null;
    }
    
    /**
     * Retrieves the OptionsMenu instance.
     * @return The OptionsMenu instance.
     */
    public OptionsMenu getOptionsMenu() {
        return null;
    }
    
    /**
     * Retrieves the BannedMenu instance.
     * @return The BannedMenu instance.
     */
    public BannedMenu getBannedMenu() {
        return instance.bannedMenu;
    }
    
    /**
     * Retrieves the WorldsMenu instance.
     * @return The WorldsMenu instance.
     */
    public WorldsMenu getWorldsMenu() {
        return null;
    }
    
    /**
     * Retrieves the PermissionsMenu instance.
     * @return The PermissionsMenu instance.
     */
    public PermissionsMenu getPermissionsMenu() {
        return null;
    }

    /**
     * Gets the configuration manager.
     * @return The ConfigManager instance.
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Splits a list into sublists of specified size.
     * Can be used for anything, we use it for GUI pages.
     * @param list The list to be split.
     * @param size The size of each sublist.
     * @return A list of sublists.
     */
    public <T> List<List<T>> listToPages(List<T> list, int size) {
        List<List<T>> sublists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            int endIndex = Math.min(i + size, list.size());
            sublists.add(list.subList(i, endIndex));
        }
        return sublists;
    }
    
    /**
     * Gets the raw world configuration.
     * Note: Call this method only when necessary.
     * @param uuid The UUID of the world.
     * @return The YamlConfiguration of the world.
     */
    public YamlConfiguration getWorldConfig(UUID uuid) {
        return worldConfigs.get(uuid);
    }
    
    /**
     * Gets the name of the selected world.
     * @param uuid The UUID of the world.
     * @return The name of the selected world.
     */
    public String getSelectedWorldName(UUID uuid) {
        return getWorldConfig(uuid).getString("default-world");
    }

    /**
     * Adds a world configuration to the map.
     * @param uuid The UUID of the world.
     * @param config The configuration to be added.
     * @return The previous configuration associated with the UUID, if any.
     */
    public YamlConfiguration addWorldConfig(UUID uuid, YamlConfiguration config) {
        return worldConfigs.put(uuid, config);
	}
	
	/**
	 * Used to get the worlds owner.
	 * @param world The world to check.
     * @return The UUID of the world owner.
     */
	public OfflinePlayer getWorldOwner(World world) {
		return Bukkit.getOfflinePlayer(UUID.fromString(world.getName().split("/")[1]));
	}
	
	/**
	 * Used to create custom worlds,
	 * Anything else prints "UnsupportedOperationException"
	 * @param uuid The UUID of the world.
	 * @param worldName The name of the world.
     */
	public void CreateWorld(UUID uuid, String worldName) {
		WorldCreator Creator = new WorldCreator("housing/" + uuid + "/" + worldName);
		Creator.generateStructures(false);
		Creator.generator(new voidWorld());
		Creator.environment(Environment.NORMAL);
		Creator.seed(0);
		Creator.keepSpawnLoaded(TriState.TRUE);
		try {
			Player player = Bukkit.getPlayer(uuid);
			addWorld(player, getWorldUtils().createWorld(Creator));
		} catch (IOException | ContentValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Used to unload custom worlds,
	 * Anything else prints "UnsupportedOperationException"
	 * @param uuid The world owners UUID.
	 */
	public void unloadWorld(UUID uuid) {
		MinecraftServer server = DedicatedServer.getServer();
		World world = getWorld(uuid);
		world.save();
		CraftWorld craftWorld = (CraftWorld) world;

        // Get the handle (WorldServer) from CraftWorld
        WorldServer worldServer = craftWorld.getHandle();
		server.removeLevel(worldServer);
		
	}
	
	/**
	 * Gets a players world instance from UUID.
	 * @param uuid The world owners UUID.
     * @return  The world instance.
	 */
	public World getWorld(UUID uuid) {
		if (getConfigManager().hasWorld(uuid) && worlds.get(Bukkit.getOfflinePlayer(uuid)) == null) {
			addWorld(Bukkit.getOfflinePlayer(uuid), Bukkit.getWorld("housing/" + uuid + "/" + getWorldConfig(uuid).getString("default-world")));
		}
		return worlds.get(Bukkit.getOfflinePlayer(uuid));
	}
	
	/*
	 * Joins a players world.
	 * @param player The player to join.
	 * @param uuid The world to join.
	 */
	public void joinWorld(Player player, UUID uuid) {
		if (!getConfigManager().hasWorld(uuid)) {
			player.sendMessage("This player does not have a world!");
			return;
		}
		String worldName = getWorldConfig(uuid).getString("default-world");
		
		if (getPlayerRank(getWorld(uuid), player).isEmpty()) {
			List<String> defaultRank = (List<String>) getWorldConfig(uuid).getList(worldName + ".ranks.default.members");
			defaultRank.add(player.getUniqueId().toString());
			getWorldConfig(uuid).set(worldName + ".ranks.default.members", defaultRank);
			try {
				getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		if (((List<String>)getWorldConfig(uuid).getList(worldName + ".banned")).contains(player.getUniqueId().toString())) {
			player.sendMessage("You are banned on this world!");
			return;
		}
		
		if (getWorldConfig(uuid).getString(worldName + ".settings.status").contains("PRIVATE") && player.getUniqueId() != uuid) {
			if (!((List<String>)getWorldConfig(uuid).getList(worldName + ".whitelist")).contains(player.getUniqueId().toString())) {
				player.sendMessage("You are not whitelisted on this world!");
				return;
			}
		}
		
		Bukkit.getRegionScheduler().runDelayed(PlayerHousing.instance, getWorld(uuid).getSpawnLocation(), (SchedulesTask) -> {
			for (String permission : getRankPermissions(getWorld(uuid), player)) {
				if (!player.hasPermission(permission)) {
					addPlayerPermission(uuid, player, permission);
				}
			}
			player.teleportAsync(getWorld(uuid).getSpawnLocation().toBlockLocation());
			player.setGameMode(GameMode.valueOf(getWorldConfig(uuid).getString(worldName + ".settings.gamemode")));
		}, 5L);
	}
	
	/*
	 * Adds a permission to a player
	 * @param uuid The world UUID.
     * @param player The player.
     * @param permission The Permission to add.
	 * */
	private void addPlayerPermission(UUID uuid, Player player, String permission) {
		if (!player.hasPermission(permission)) {
			lp.getUserManager().modifyUser(player.getUniqueId(), user -> {
		        user.data().add(Node.builder(permission).withContext(DefaultContextKeys.WORLD_KEY, getWorld(uuid).getName()).build());
		    });			
		}
		
	}
	
	/*
	 * Removes a permission to a player
	 * @param uuid The world UUID.
     * @param player The player.
     * @param permission The Permission to remove.
	 * */
	private void removePlayerPermission(UUID uuid, Player player, String permission) {
		if (player.hasPermission(permission)) {
			lp.getUserManager().modifyUser(player.getUniqueId(), user -> {
				user.data().remove(Node.builder(permission).withContext(DefaultContextKeys.WORLD_KEY, getWorld(uuid).getName()).build());
		    });
		}
		
	}

	/**
     * Sets the player's world instance. Usually used for changing their worlds.
     * @param offlinePlayer The offline player whose world instance is being set.
     * @param world The world instance to be set.
     */
	public void addWorld(@NotNull OfflinePlayer offlinePlayer, World world) {
		worlds.put(offlinePlayer, world);
	}
	
	
	/**
     * Retrieves the WorldUtils instance. Deprecated method, use with caution.
     * @return The WorldUtils instance.
     * @deprecated This method is to be replaced by the Bukkit API.
     */
	@Deprecated
	protected WorldUtils getWorldUtils() {
		return worldUtils;
	}
	
	/**
     * Loads a world manually. Not recommended for general use.
     * @param uuid The UUID of the world to be loaded.
     */
	public void loadWorld(UUID uuid) {
		if (getConfigManager().hasWorld(uuid)) {
			getConfigManager().verifyConfig(uuid, getWorldConfig(uuid).getString("default-world"));
			CreateWorld(uuid, getWorldConfig(uuid).getString("default-world"));
			return;
		}
		
		
	}
	
	/**
     * Performs an action on a player.
     * @param player The player performing the action.
     * @param target The target player on which the action is being performed.
     * @param action The action to be performed.
     * @param arg1 Optional argument 1 for the action. (Ehh rank)
     * @param arg2 Optional argument 2 for the action. (ehh permission)
     */
	public void performAction(Player player, OfflinePlayer target, Action action, @Nullable String arg1, @Nullable String arg2) {
		OfflinePlayer owner = getWorldOwner(player.getWorld());
		if (target == owner) {
			if (action != Action.addGroupPermission && action != Action.removeGroupPermission) {
				player.sendMessage("The world owner can not be modified!");
				return;
			}
			
		}
		UUID uuid = getWorldOwner(player.getWorld()).getUniqueId();
		String selectedWorld = getWorldConfig(uuid).getString("default-world");
		switch(action) {
			case kick:{
				target.getPlayer().teleportAsync(Bukkit.getWorld("world").getSpawnLocation());
				target.getPlayer().sendMessage("You have been kicked from this world!");
				player.sendMessage("Kicked player!");
				break;
			}
			case ban:{
				
				if(getWorldConfig(uuid).getList(selectedWorld + ".banned").contains(target.getUniqueId().toString())) {
					player.sendMessage("This player is already banned!");
					player.closeInventory();
					break;
				}
				List<String> whitelist = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".banned");
				whitelist.add(target.getUniqueId().toString());
				getWorldConfig(uuid).set(selectedWorld + ".banned", whitelist);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				target.getPlayer().teleportAsync(Bukkit.getWorld("world").getSpawnLocation());
				target.getPlayer().sendMessage("You have been banned from this world!");
				player.sendMessage("Banned player!");
				break;
			}
			case unban:{
				if(!getWorldConfig(uuid).getList(selectedWorld + ".banned").contains(target.getUniqueId().toString())) {
					player.sendMessage("This player is not banned!");
					player.closeInventory();
					break;
				}
				List<String> whitelist = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".banned");
				whitelist.remove(target.getUniqueId().toString());
				getWorldConfig(uuid).set(selectedWorld + ".banned", whitelist);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage("Unbanned player!");
				break;
			}
			case addWhitelist: {
				if(getWorldConfig(uuid).getList(selectedWorld + ".whitelist").contains(target.getUniqueId().toString())) {
					player.sendMessage("This player is already whitelisted!");
					player.closeInventory();
					break;
				}
				List<String> whitelist = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".whitelist");
				whitelist.add(target.getUniqueId().toString());
				getWorldConfig(uuid).set(selectedWorld + ".whitelist", whitelist);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage("Updated whitelisted!");
				break;
			}
			case removeWhitelist: {
				if(!getWorldConfig(uuid).getList(selectedWorld + ".whitelist").contains(target.getUniqueId().toString())) {
					player.sendMessage("This player is not whitelisted!");
					player.closeInventory();
					break;
				}
				List<String> whitelist = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".whitelist");
				whitelist.remove(target.getUniqueId().toString());
				getWorldConfig(uuid).set(selectedWorld + ".whitelist", whitelist);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.sendMessage("Updated whitelist!");
				break;
			}
			case addGroupPermission: {
				if(getWorldConfig(uuid).getList(selectedWorld + ".ranks." + arg1 + ".permissions").contains(arg2)) {
					player.sendMessage("Already has permission!");
					player.closeInventory();
					break;
				}
				List<String> permissions = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".ranks." + arg1 + ".permissions");
				permissions.add(arg2);
				getWorldConfig(uuid).set(selectedWorld + ".ranks." + arg1 + ".permissions", permissions);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addPlayerPermission(uuid, player, arg2);
				player.sendMessage("Added permission to group!");
				break;
			}
			case removeGroupPermission: {
				if(!getWorldConfig(uuid).getList(selectedWorld + ".ranks." + arg1 + ".permissions").contains(arg2)) {
					player.sendMessage("Permission not found!");
					player.closeInventory();
					break;
				}
				List<String> permissions = (List<String>) getWorldConfig(uuid).getList(selectedWorld + ".ranks." + arg1 + ".permissions");
				permissions.remove(arg2);
				getWorldConfig(uuid).set(selectedWorld + ".ranks." + arg1 + ".permissions", permissions);
				try {
					getWorldConfig(uuid).save(getConfigManager().getFile(uuid));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				removePlayerPermission(uuid, player, arg2);
				player.sendMessage("Removed permission to group!");
				break;
			}
		default:
			break;
		}
		
	}
	
	/**
     * Retrieves the rank of a player in a world.
     * @param world The world in which the rank is being checked.
     * @param player The player whose rank is being checked.
     * @return The rank of the player in the world.
     */
	public String getPlayerRank(World world, OfflinePlayer player) {
		String worldName = getWorldNameFromWorld(world);
		for (String rank: getWorldConfig(getWorldOwner(world).getUniqueId()).getConfigurationSection(worldName + ".ranks").getKeys(false)) {
			if (getWorldConfig(getWorldOwner(world).getUniqueId()).getList(worldName + ".ranks." + rank + ".members").contains(player.getUniqueId().toString())) {
				return rank;
			}
		}
		return "";
	}
	
	/**
     * Retrieves the name of the world from the World object.
     * @param world The world whose name is being retrieved.
     * @return The name of the world.
     */
	public String getWorldNameFromWorld(World world) {
		return world.getName().split("/")[2];
	}

	/**
     * Retrieves the permissions of a player based on their rank in a world.
     * @param world The world in which the permissions are being checked.
     * @param player The player whose permissions are being checked.
     * @return The list of permissions assigned to the player's rank in the world.
     */
	public List<String> getRankPermissions(World world, OfflinePlayer player) {
		String worldName = getWorldNameFromWorld(world);
		for (String rank: getWorldConfig(getWorldOwner(world).getUniqueId()).getConfigurationSection(worldName + ".ranks").getKeys(false)) {
			if (getWorldConfig(getWorldOwner(world).getUniqueId()).getList(worldName + ".ranks." + rank + ".members").contains(player.getUniqueId().toString())) {
				return (List<String>) getWorldConfig(getWorldOwner(world).getUniqueId()).getList(worldName + ".ranks." + rank + ".permissions");
			}
		}
		return List.of();
	}
}