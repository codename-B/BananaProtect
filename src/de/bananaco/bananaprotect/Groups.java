package de.bananaco.bananaprotect;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Groups {

	private YamlConfiguration yml;
	private final File file;
	
	Groups() {
		yml = new YamlConfiguration();
		file = new File("plugins/BananaProtect/bananaprotect.yml");
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			try {
			file.createNewFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte getGroup(Player player) {
		int group = yml.getInt(player.getName(), -1);
		return (byte) group;
	}
	
	public void setGroup(Player player, byte group) {
		yml.set(player.getName(), (int) group);
		save();
	}
	
	public void log(boolean error, String message) {
		if(error)
			System.err.println("[BananaProtect] "+message);
		else
			System.out.println("[BananaProtect] "+message);
	}
	
	public void load() {
		try {
		yml.load(file);
		onLoad();
		} catch (Exception e) {
			log(true, "Error loading bananaprotect.yml");
			log(true, e.getMessage());
		}
	}
	
	public void save() {
		try {
		yml.save(file);
		onSave();
		} catch (Exception e) {
			log(true, "Error saving bananaprotect.yml");
			log(true, e.getMessage());
		}
	}
	
	private void onLoad() {
		int fileSize = yml.getKeys(false).size();
		log(false, fileSize + " players loaded.");
		if(fileSize > 300)
		log(false, "You may want to remove old members");
	}
	
	private void onSave() {
		int fileSize = yml.getKeys(false).size();
		log(false, fileSize + " players saved.");
		if(fileSize > 300)
		log(false, "You may want to remove old members");
	}
	
		
}
