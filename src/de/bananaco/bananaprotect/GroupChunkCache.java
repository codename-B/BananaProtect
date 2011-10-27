package de.bananaco.bananaprotect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.World;

public class GroupChunkCache {
	
	private final Map<Integer, GroupChunk> cache;
	
	GroupChunkCache() {
		this.cache = new HashMap<Integer, GroupChunk>();
	}
		
	public GroupChunk get(World world, int x, int y, int z) {
		return get(world, x >> 4, z >> 4);
	}
	
	public GroupChunk get(World world, int x, int z) {
		if(!cache.containsKey(GroupChunk.hashCode(world, x, z)))
		cache(world, x, z);
		return cache.get(GroupChunk.hashCode(world, x, z));
	}
	
	public void cache(World world, int x, int z) {
		GroupChunk newChunk = new GroupChunk(world, x, z);
		newChunk.load();
		cache.put(newChunk.hashCode(), newChunk);
	}
	
	public void uncache(World world, int x, int z) {
		GroupChunk newChunk = cache.get(GroupChunk.hashCode(world, x, z));
		newChunk.save();
		cache.remove(newChunk);
	}
	
	public void saveAll(BananaProtect bp) {
		if(bp.disabled)
			return;
		for(GroupChunk chunk : cache.values()) {
			if(bp.disabled)
				return;
			if(chunk != null)
			chunk.save();
			long timeLast = System.currentTimeMillis()-chunk.lastUsed;
			if(timeLast > 300000)
			uncache(chunk.getWorld(), chunk.getX(), chunk.getZ());
		}
	}

	public void saveAllUncache() {
		System.out.println("[BananaProtect] Saving records... may take some time");
		for(GroupChunk chunk : cache.values()) {
			if(chunk != null)
			uncache(chunk.getWorld(), chunk.getX(), chunk.getZ());
		}
		System.out.println("[BananaProtect] Saving complete!");
	}
	
	public void clear() {
		cache.clear();
	}
	
	public void cacheWorld(World world) {
		for(Chunk chunk : world.getLoadedChunks()) {
			int x = chunk.getX();
			int z = chunk.getZ();
			cache(world, x, z);
		}
	}
}
