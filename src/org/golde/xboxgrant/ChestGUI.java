package org.golde.xboxgrant;


import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;


public class ChestGUI implements Listener {

	private String name;
	private int size;
	private OptionClickEventHandler handler;
	private Plugin plugin;

	private String[] optionNames;
	private ItemStack[] optionIcons;

	private InventoryView openView;
	

	public ChestGUI(String name, int size, OptionClickEventHandler handler) {
		this.name = name;
		this.size = size;
		this.handler = handler;
		this.plugin = Main.getInstance();
		this.optionNames = new String[size];
		this.optionIcons = new ItemStack[size];
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public ChestGUI setOption(int position, ItemStack icon, String name, String... info) {
		optionNames[position] = name;
		optionIcons[position] = setItemNameAndLore(icon, name, info);
		return this;
	}

	public void setOption(int position, ItemStack icon) {
		optionNames[position] = icon.getItemMeta().getDisplayName();
		
		ItemMeta im = icon.getItemMeta();
		ItemStack toSet = icon;
		if(im != null) {
			if(im.getDisplayName() == null || im.getDisplayName().isEmpty()) {
				toSet = icon;
			}
			else {
				if(im.getLore() == null || im.getLore().size() == 0) {
					toSet = setItemNameAndLore(icon, im.getDisplayName(), new String[] {});
				}
				else {
					toSet = setItemNameAndLore(icon, im.getDisplayName(), im.getLore());
				}
			}
		}
		optionIcons[position] = toSet;
		//optionIcons[position] = setItemNameAndLore(icon, icon.getItemMeta().getDisplayName(), icon.getItemMeta().getLore().toArray(new String[0]));
	}
	
	public ItemStack getOption(int position)
	{
		return optionIcons[position];
	}

	public void updateOpenLore(int position, String... lore)
	{
		if (openView != null) {
			ItemStack item = openView.getItem(position);
			ItemMeta im = item.getItemMeta();
			im.setLore(Arrays.asList(lore));
			item.setItemMeta(im);
			openView.setItem(position, item);
		}
	}
	
	public void updateOpenItem(int position, ItemStack icon, String name, String... info)
	{
		if (openView != null) {
			ItemStack newItem = setItemNameAndLore(icon, name, info);
			openView.setItem(position, newItem);
		}
	}

	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory(player, size, name);
		for (int i = 0; i < optionIcons.length; i++) {
			if (optionIcons[i] != null) {
				inventory.setItem(i, optionIcons[i]);
			}
		}

		openView = player.openInventory(inventory);
	}

	public void destroy() {
		HandlerList.unregisterAll(this);
		handler = null;
		plugin = null;
		optionNames = null;
		optionIcons = null;
		openView = null;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(name) && event.getView() == openView) {
			event.setCancelled(true);
			int slot = event.getRawSlot();
			if (slot >= 0 && slot < size && optionNames[slot] != null) {
				Plugin plugin = this.plugin;
				OptionClickEvent e = new OptionClickEvent(this, (Player)event.getWhoClicked(), slot, optionNames[slot]);
				handler.onOptionClick(e);
				if (e.willClose()) {
					final Player p = (Player)event.getWhoClicked();
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							p.closeInventory();
						}
					}, 1);
				}
				if (e.willDestroy()) {
					destroy();
				}
			}
		}
	}

	public interface OptionClickEventHandler {
		public void onOptionClick(OptionClickEvent event);       
	}

	public class OptionClickEvent {
		private ChestGUI iconMenu;
		private Player player;
		private int position;
		private String name;
		private boolean close;
		private boolean destroy;

		public OptionClickEvent(ChestGUI iconMenu, Player player, int position, String name) {
			this.iconMenu = iconMenu;
			this.player = player;
			this.position = position;
			this.name = name;
			this.close = true;
			this.destroy = false;
		}
		
		public ChestGUI getIconMenu() {
			return iconMenu;
		}

		public Player getPlayer() {
			return player;
		}
		
		public ItemStack getItemAtPosition(int position){
        	return  optionIcons[position];
        }

		public int getPosition() {
			return position;
		}

		public String getName() {
			return name;
		}

		public boolean willClose() {
			return close;
		}

		public boolean willDestroy() {
			return destroy;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}

		public ItemStack getItem() {
			return iconMenu.getOption(position);
		}

	}

	private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
		return setItemNameAndLore(item, name, Arrays.asList(lore));
	}
	
	private ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		item.setItemMeta(im);
		return item;
	}

}

