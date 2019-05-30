package org.golde.xboxgrant.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.golde.xboxgrant.GrantConfig;
import org.golde.xboxgrant.Main;

public class CmdGrantReload implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		Main.getInstance().reloadConfig();
		GrantConfig.load();
		Main.getInstance().sendMessage(sender, "&aConfig reloaded.");
		return true;
	}

}