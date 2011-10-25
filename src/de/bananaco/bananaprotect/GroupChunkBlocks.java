package de.bananaco.bananaprotect;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GroupChunkBlocks {
	
	private final Groups groups;
	private final GroupChunkCache gcc;
	private final boolean defaultBuild;
	
	GroupChunkBlocks(Groups groups, GroupChunkCache gcc, boolean defaultBuild) {
		this.groups = groups;
		this.gcc = gcc;
		this.defaultBuild = defaultBuild;
	}
	
	public boolean blockBreak(World world, int x, int y, int z, Player player) {
		byte id = groups.getGroup(player);
		int cx = world.getChunkAt(new Location(world, x, y, z)).getX();
		int cz = world.getChunkAt(new Location(world, x, y, z)).getZ();
		x = x % 16;
		z = z % 16;
		if(x<0)
			x=16+x;
		if(z<0)
			z=16+z;
		
		GroupChunk gc = gcc.get(world, cx, cz);
		
		if(gc.isProtected(x, y, z))
			if(gc.isProtectedFrom(x, y, z, id))
				return onBlockPlace(player, false);
		if(id == -1)
			return onBlockPlace(player, defaultBuild);
		
		gc.set(x, y, z, null);
		return onBlockPlace(player, true);
	}
	
	public boolean blockPlace(World world, int x, int y, int z, Player player) {
		byte id = groups.getGroup(player);
		if(id == -1)
			return onBlockPlace(player, defaultBuild);
		int cx = world.getChunkAt(new Location(world, x, y, z)).getX();
		int cz = world.getChunkAt(new Location(world, x, y, z)).getZ();
		x = x % 16;
		z = z % 16;
		if(x<0)
			x=16+x;
		if(z<0)
			z=16+z;
		
		GroupChunk gc = gcc.get(world, cx, cz);
		gc.set(x, y, z, id);
		return onBlockPlace(player, true);
	}
	
	public boolean onBlockBreak(Player player, boolean result) {
		if(!result)
			player.sendMessage(ChatColor.RED+"You can't build there.");	
		return !result;
	}
	
	public boolean onBlockPlace(Player player, boolean result) {
		if(!result)
			player.sendMessage(ChatColor.RED+"You can't build there.");
		return !result;
	}
	
	public GroupChunkCache getCache() {
		return gcc;
	}

}
