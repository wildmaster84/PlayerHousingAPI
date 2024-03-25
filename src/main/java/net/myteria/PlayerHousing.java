package net.myteria;

import org.bukkit.plugin.java.JavaPlugin;

import net.myteria.menus.BannedMenu;
import net.myteria.menus.HousingMenu;


public final class PlayerHousing extends JavaPlugin {
	private static HousingAPI api;
	public static PlayerHousing instance;
	public HousingMenu housingMenu;
	public BannedMenu bannedMenu;
	
	public void onEnable() {
		instance = this;
		api = new HousingAPI();
		housingMenu = new HousingMenu();
		bannedMenu = new BannedMenu();
		
	}
	
	public static HousingAPI getAPI() {
		return api;
	}
	
	public static PlayerHousing getInstance() {
		return instance;
	}
}
