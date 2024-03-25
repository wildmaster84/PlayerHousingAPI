package net.myteria.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import net.myteria.HousingAPI;
import net.myteria.PlayerHousing;

public class HousingMenu implements InventoryHolder {
	HousingAPI api = PlayerHousing.getAPI();
	Inventory inv = null;
	int[] purpleSlots = {3, 4, 5, 13, 21, 22, 23};
	int[] graySlots = {0, 1, 7, 8, 9, 17, 18, 19, 25, 26};
	int[] magentaSlots = {2, 6, 10, 11, 15, 16, 20, 24};
	int[] settingsSlots = {12};
	int[] managementSlots = {14};
	public HousingMenu() {
		inv = Bukkit.createInventory(this, 3*9, "Housing Menu");

		
		ItemStack purple = setMeta(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), " ", null);
		ItemStack gray = setMeta(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " ", null);
		ItemStack magenta = setMeta(new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE), " ", null);
		ItemStack settingsBtn = setMeta(new ItemStack(Material.COMMAND_BLOCK), "Settings", null);
		ItemStack managementBtn = setMeta(new ItemStack(Material.WRITABLE_BOOK), "Player Manager", null);
		
		
		setSlot(purpleSlots, purple);
		setSlot(graySlots, gray);
		setSlot(magentaSlots, magenta);
		setSlot(settingsSlots, settingsBtn);
		setSlot(managementSlots, managementBtn);
	}
	
	@Override
	public @NotNull Inventory getInventory() {
		// TODO Auto-generated method stub
		return inv;
	}
	
	public ItemStack setMeta(ItemStack item, String display, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(display);
		if (lore != null) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
		
	}
	
	public void setSlot(int[] slot, ItemStack item) {
		for (int invSlot: slot) {
			inv.setItem(invSlot, item);
		}
	}

}
