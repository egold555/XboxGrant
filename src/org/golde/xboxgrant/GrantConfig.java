package org.golde.xboxgrant;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class GrantConfig {

	private static String prefix;
	private static List<String> prefixes;
	private static List<ConfigKit> kits;

	public static void load() {
		
		kits = new ArrayList<ConfigKit>(); //clear
		prefixes = new ArrayList<String>(); //clear
		

		FileConfiguration config = Main.getInstance().getConfig();

		prefix = Main.getInstance().color(config.getString("prefix", "&bGrants >") + " ");

		for(String p : config.getStringList("prefixes")) { //Color all prefixes
			prefixes.add(Main.getInstance().color(p));
		}
		
		ConfigurationSection kitSection = config.getConfigurationSection("kits");
		for(String key : kitSection.getKeys(false)) {
			String name = kitSection.getString(key + ".name", "&cNo name found for kit " + key + "!");
			name = Main.getInstance().color(name);
			String perm = kitSection.getString(key + ".perm", "no.permission.was.found.for.kit." + key);
			kits.add(new ConfigKit(name, perm));
		}

	}

	public static String getPrefix() {
		return prefix;
	}
	
	public static List<String> getPrefixes() {
		return prefixes;
	}
	
	public static List<ConfigKit> getKits() {
		return kits;
	}
	
	public static class ConfigKit {
		private final String name, perm;
		private ConfigKit(String name, String perm) {
			this.name = name;
			this.perm = perm;
		}
		
		public String getName() {
			return name;
		}
		
		public String getPerm() {
			return perm;
		}
	}

}
