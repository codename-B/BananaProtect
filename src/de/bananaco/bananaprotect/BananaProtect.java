package de.bananaco.bananaprotect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bananaprotect.superstick.SuperStickListener;

public class BananaProtect extends JavaPlugin {

	private Groups groups = new Groups();
	private GroupChunkCache groupChunkCache = new GroupChunkCache();
	private BlockEvent be;
	private GroupChunkBlocks gcb;
	private BananaProtectExec bpe;
	private SuperStickListener ssl;
	
	private Configuration c;
	private static List<Integer> blackList;
	
	public boolean caseMatching = false;
	public boolean defaultBuild = false;
	
	public static void blackList(List<Integer> blackList) {
		BananaProtect.blackList = blackList;
	}
	
	public static List<Integer> blackList() {
		return blackList;
	}
	
	public SuperStickListener getListener() {
		return ssl;
	}
	
	public GroupChunk get(World world, int x, int z) {
		return groupChunkCache.get(world, x ,z);
	}
	
	@SuppressWarnings("unchecked")
	public void setupConfig() {
		List<Integer> blackList = new ArrayList<Integer>();
				blackList.add(0);
				blackList.add(6);
				blackList.add(8);
				blackList.add(9);
				blackList.add(10);
				blackList.add(11);
				blackList.add(12);
				blackList.add(13);
				blackList.add(51);
				blackList.add(87);
		
		c = new Configuration(this);
		
		caseMatching = c.getBoolean("caseMatching", caseMatching);
		defaultBuild = c.getBoolean("defaultBuild", defaultBuild);
		blackList = c.getList("blacklist-ids", blackList);
		BananaProtect.blackList(blackList);
		
		c.setProperty("caseMatching", caseMatching);
		c.setProperty("defaultBuild", defaultBuild);
		c.setProperty("blacklist-ids", blackList);
		
		c.save();
		log(false, "Config setup - defaultBuild:"+defaultBuild+" caseMatching:"+caseMatching);
	}

	@Override
	public void onDisable() {
		groups.save();
		
		groupChunkCache.saveAllUncache();
		groupChunkCache.clear();
		
		getServer().getScheduler().cancelTasks(this);
		
		log(false, "Disabled.");
	}

	@Override
	public void onEnable() {
		setupConfig();
		PluginManager pm = this.getServer().getPluginManager();
		
		new ParentPermission("bananaprotect.*", PermissionDefault.OP, "bananaprotect.admin").registerPermission(pm);
		new ParentPermission("bananaprotect.admin", PermissionDefault.FALSE, "bananaprotect.super").registerPermission(pm);
		new ParentPermission("bananaprotect.super", PermissionDefault.FALSE, "bananaprotect.user").registerPermission(pm);
		new ParentPermission("bananaprotect.user", PermissionDefault.TRUE).registerPermission(pm);
		
		bpe = new BananaProtectExec(this, groups);
		
		groups.load();
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this,
				new Runnable() {
					public void run() {
						groupChunkCache.saveAll();
					}
				}, 1000, 1000);
		
		gcb = new GroupChunkBlocks(groups, groupChunkCache, defaultBuild);
		
		ssl = new SuperStickListener(groups, this);
		
		be = new BlockEvent(gcb);
		be.registerEvents(this, this.getServer().getPluginManager());
		
		log(false, "Enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			String comString = command.getName();
			String[] targs = new String[2];
			for(int i=0; i<args.length && i<2; i++)
				targs[i] = args[i];
			bpe.onCommand(sender, comString, targs[0], targs[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Incorrect command: "+e.getClass().getCanonicalName()+" "+ e.getMessage());
			// More info for ConsoleCommandSender
			if(!(sender instanceof Player)) {
				String name = e.getStackTrace()[0].getClassName();
				int line = e.getStackTrace()[0].getLineNumber();
			sender.sendMessage("Debug info:");
			sender.sendMessage("Class: "+name);
			sender.sendMessage("Line: "+line);
			}
			return true;
		}
		return true;
	}

	public static void log(boolean error, String message) {
		if (error)
			System.err.println("[BananaProtect] " + message);
		else
			System.out.println("[BananaProtect] " + message);
	}
	
	public Player getPlayer(String name, CommandSender sender) {
		Player player = getServer().getPlayer(name);
		if(player == null && !caseMatching)
			player = new FakePlayer(name, sender);
		return player;
	}

}
