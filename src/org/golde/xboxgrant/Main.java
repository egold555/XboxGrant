package org.golde.xboxgrant;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.golde.xboxgrant.cmd.CmdGrant;
import org.golde.xboxgrant.cmd.CmdGrantReload;

public class Main extends JavaPlugin {

	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		getCommand("grant").setExecutor(new CmdGrant());
		getCommand("grantreload").setExecutor(new CmdGrantReload());
		GrantConfig.load();
	}

	public static Main getInstance() {
		return instance;
	}

	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(GrantConfig.getPrefix() + color(msg));
	}

	public String color(String in) {
		return ChatColor.translateAlternateColorCodes('&', in);
	}


}