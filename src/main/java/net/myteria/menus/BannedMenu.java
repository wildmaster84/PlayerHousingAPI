package net.myteria.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import net.myteria.HousingAPI;
import net.myteria.PlayerHousing;
import net.myteria.HousingAPI.Action;

public class BannedMenu implements InventoryHolder {
	HousingAPI api = PlayerHousing.getAPI();
	Inventory inv = null;
	int[] purpleSlots = {3, 4, 5, 21, 22, 23};
	int[] graySlots = {0, 1, 7, 8, 9, 17, 18, 19, 25, 26};
	int[] magentaSlots = {2, 6, 10, 12, 14, 16, 20, 24};
	int[] removeSlots = {11};
	int[] headSlot = {13};
	int[] addSlot = {15};
	public void setupMenu(OfflinePlayer player) {
		inv = Bukkit.createInventory(this, 3*9, "Ban Manager");

		
		ItemStack purple = setMeta(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), " ", null);
		ItemStack gray = setMeta(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " ", null);
		ItemStack magenta = setMeta(new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE), " ", null);
		ItemStack removeBtn = setMeta(new ItemStack(Material.RED_WOOL), "Unban", null);
		ItemStack headBtn = setSkullMeta(new ItemStack(Material.PLAYER_HEAD), player);
		ItemStack addBtn = setMeta(new ItemStack(Material.GREEN_WOOL), "Ban", null);
		
		
		setSlot(purpleSlots, purple);
		setSlot(graySlots, gray);
		setSlot(magentaSlots, magenta);
		setSlot(removeSlots, removeBtn);
		setSlot(headSlot, headBtn);
		setSlot(addSlot, addBtn);
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
	public ItemStack setMeta(ItemStack item, String display, Action action, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(display);
		meta.getPersistentDataContainer().set(new NamespacedKey(PlayerHousing.getInstance(), "action"), PersistentDataType.STRING, action.name());
		if (lore != null) {
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
		
	}
	public ItemStack setSkullMeta(ItemStack item, OfflinePlayer player) {
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(player);
		meta.setDisplayName(player.getName());
		item.setItemMeta(meta);
		return item;
		
	}
	
	public void setSlot(int[] slot, ItemStack item) {
		for (int invSlot: slot) {
			inv.setItem(invSlot, item);
		}
	}

}
