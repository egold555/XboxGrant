package org.golde.xboxgrant.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.xboxgrant.ChestGUI;
import org.golde.xboxgrant.ChestGUI.OptionClickEvent;
import org.golde.xboxgrant.ChestGUI.OptionClickEventHandler;
import org.golde.xboxgrant.GrantConfig;
import org.golde.xboxgrant.GrantConfig.ConfigKit;
import org.golde.xboxgrant.Main;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CmdGrant implements CommandExecutor {

	private static final String BLANK_WORLD_PEX_FIX = "";
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		if(!(sender instanceof Player)) {
			Main.getInstance().sendMessage(sender, ChatColor.RED + "Only players may execute this command. Sorry about that :(");
			return true;
		}

		Player player = (Player)sender;

		if(args.length == 0 || args[0] == null || args[0].isEmpty()) {
			Main.getInstance().sendMessage(sender, ChatColor.RED + "/grant <player>");
			return true;
		}

		Player grantie = Bukkit.getPlayer(args[0]);
		if(grantie == null || !grantie.isOnline()) {
			Main.getInstance().sendMessage(sender, ChatColor.RED + "I could not find the player: " + args[0]);
			return true;
		}

		openUpGrantMenu(player, grantie);

		return true;
	}

	private void openUpGrantMenu(final Player to, final Player grantie) {
		ChestGUI mainGui = new ChestGUI("Grants", 36, new OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {

				if(event.getPosition() == 21) {
					new BukkitRunnable() {

						@Override
						public void run() {
							openGrantMenuPrefixes(to, grantie);
						}

					}.runTaskLater(Main.getInstance(), 2);
				}
				else if(event.getPosition() == 22) {
					new BukkitRunnable() {

						@Override
						public void run() {
							openGrantMenuRanks(to, grantie);
						}

					}.runTaskLater(Main.getInstance(), 2);
				}
				else if(event.getPosition() == 23) {
					new BukkitRunnable() {

						@Override
						public void run() {
							openGrantMenuKits(to, grantie);
						}

					}.runTaskLater(Main.getInstance(), 2);
				}
				else {
					event.setWillClose(false);
					event.setWillDestroy(false);
					return;
				}
				event.setWillClose(true);
				event.setWillDestroy(true);

			}
		});

		mainGui.setOption(13, getPlayerSkull(grantie), ChatColor.GREEN + "Granting: " +ChatColor.WHITE + grantie.getName());
		mainGui.setOption(21, new ItemStack(Material.NAME_TAG), ChatColor.YELLOW + "Prefixes");
		mainGui.setOption(22, new ItemStack(Material.PAPER), ChatColor.YELLOW + "Ranks");
		mainGui.setOption(23, new ItemStack(Material.DIAMOND_SWORD), ChatColor.YELLOW + "Kits");
		mainGui.open(to);
	}

	private void openGrantMenuPrefixes(final Player to, final Player grantie) {
		ChestGUI gui = new ChestGUI("Prefixes", 54, new OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {
				if((event.getPosition()+1) >GrantConfig.getPrefixes().size()) {
					event.setWillClose(false);
					return;
				}

				final String selectedPrefix = GrantConfig.getPrefixes().get(event.getPosition());
				new BukkitRunnable() {

					@Override
					public void run() {
						openConfirmMenu(to, new TimedRunnable() {

							@Override
							public void run(int days) {
								PermissionsEx.getUser(grantie).setPrefix(selectedPrefix, BLANK_WORLD_PEX_FIX);
							}
						}, 0); //time doesnt matter in this case

					}
				}.runTaskLater(Main.getInstance(), 2);

				event.setWillClose(true);
				event.setWillDestroy(true);
			}
		});

		for(int i = 0; i < GrantConfig.getPrefixes().size(); i++) {
			gui.setOption(i, new ItemStack(Material.NAME_TAG), GrantConfig.getPrefixes().get(i));
		}

		gui.open(to);
	}

	private void openGrantMenuRanks(final Player to, final Player grantie) {
		ChestGUI gui = new ChestGUI("Ranks", 54, new OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {

				if((event.getPosition()+1) > PermissionsEx.getPermissionManager().getGroupList().size()) {
					event.setWillClose(false);
					return;
				}

				final PermissionGroup selectedGroup = PermissionsEx.getPermissionManager().getGroupList().get(event.getPosition());

				new BukkitRunnable() {

					@Override
					public void run() {
						openTimeSelectMenu(to, new TimedRunnable() {

							@Override
							public void run(int days) {
								PermissionsEx.getUser(grantie).addGroup(selectedGroup.getName(), BLANK_WORLD_PEX_FIX, days); //I hate pex
								Main.getInstance().sendMessage(grantie, ChatColor.GREEN + "You have been added to the group: " + selectedGroup.getName());
								Main.getInstance().sendMessage(to, ChatColor.YELLOW + grantie.getName() + ChatColor.GREEN + " has been added to the group: " + selectedGroup.getName());
							}
						});

					}
				}.runTaskLater(Main.getInstance(), 2);
				event.setWillClose(true);
				event.setWillDestroy(true);

			}
		});

		for(int i = 0; i < PermissionsEx.getPermissionManager().getGroupList().size(); i++) {
			PermissionGroup pg = PermissionsEx.getPermissionManager().getGroupList().get(i);
			if(pg.getPrefix().length() != 2) {
				gui.setOption(i, new ItemStack(Material.PAPER), pg.getPrefix().replace("&", "§"));
			}
			else {
				gui.setOption(i, new ItemStack(Material.PAPER), pg.getName().replace("&", "§"));
			}

		}

		gui.open(to);
	}

	private void openGrantMenuKits(final Player to, final Player grantie) {
		ChestGUI gui = new ChestGUI("Kits", 54, new OptionClickEventHandler() {
			
			@Override
			public void onOptionClick(OptionClickEvent event) {

				if((event.getPosition()+1) > GrantConfig.getKits().size()) {
					event.setWillClose(false);
					return;
				}
				
				final ConfigKit kit = GrantConfig.getKits().get(event.getPosition());

				new BukkitRunnable() {
					
					@Override
					public void run() {

						openTimeSelectMenu(to, new TimedRunnable() {
							
							@Override
							public void run(int days) {

								PermissionUser user = PermissionsEx.getUser(grantie);
								user.addTimedPermission(kit.getPerm(), BLANK_WORLD_PEX_FIX, days);

							}
						});
						
					}
				}.runTaskLater(Main.getInstance(), 2);
				
				
			}
		});
		
		for(int i = 0; i < GrantConfig.getKits().size(); i++) {
			gui.setOption(i, new ItemStack(Material.DIAMOND_SWORD), GrantConfig.getKits().get(i).getName());
		}
		
		gui.open(to);
	}
	
	private void openTimeSelectMenu(final Player to, final TimedRunnable callback) {
		ChestGUI gui = new ChestGUI("Select Time", 4*9, new OptionClickEventHandler() {
			
			@Override
			public void onOptionClick(OptionClickEvent event) {
				
				if(event.getPosition() > 35) {
					event.setWillClose(false);
					return;
				}
				
				String clickedName = event.getItemAtPosition(event.getPosition()).getItemMeta().getDisplayName();
				clickedName = ChatColor.stripColor(clickedName);
				clickedName = clickedName.substring(0, 1);
				final int seconds = Integer.parseInt(clickedName);
				
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						openConfirmMenu(to, callback, seconds * 60 * 60 * 24); //seconds * 60 = min * 60 = hour * 24 = day
					}
					
				}.runTaskLater(Main.getInstance(), 2);
				
				
				event.setWillClose(true);
				event.setWillDestroy(true);
				
			}
		});
		
		for(int i = 1; i < 31; i++) {
			String name = ChatColor.YELLOW + "" + i + ChatColor.AQUA + " Day";
			if(i > 1) {
				name += "s";
			}
			
			gui.setOption(i-1, new ItemStack(Material.WATCH), name);
		}
		
		gui.open(to);
	}

	private void openConfirmMenu(final Player to, final TimedRunnable callback, final int days) {
		ChestGUI gui = new ChestGUI("Are you sure?", 9, new OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {
				int p = event.getPosition();
				if(p == 0 || p == 1 || p == 2 || p == 3) {
					//NO
				}

				else if(p == 5 || p == 6 || p == 7 || p == 8) {
					//YES
					callback.run(days);
				}

				else {
					event.setWillClose(false);
					return;
				}
				event.setWillClose(true);
				event.setWillDestroy(true);
			}
		});

		for(int i = 0; i < 4; i++) {
			gui.setOption(i, new ItemStack(Material.WOOL, 1, (short) 14), "§c§lCancel", ChatColor.WHITE + "Click to " + ChatColor.RED + "Cancel");
			gui.setOption(i+5, new ItemStack(Material.WOOL, 1, (short) 5), "§a§lConfirm", ChatColor.WHITE + "Click to " + ChatColor.GREEN + "Confirm");
		}

		gui.open(to);
	}

	private ItemStack getPlayerSkull(Player player){
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player.getName());
		item.setItemMeta(meta);
		return item;
	}
	
	private static interface TimedRunnable {
		public void run(int days);
	}

}
