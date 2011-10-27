package de.bananaco.bananaprotect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.World;

public class GroupChunk {
	
	public GroupChunk(World world, int x, int z) {
		this.lastUsed = System.currentTimeMillis();
		
		this.groupChunk = new Byte[16*16*128];
		this.modifies = 0;
		this.x = x;
		this.z = z;
		this.world = world;
		file = new File("plugins/BananaProtect/"+world.getName()+"/"+x+"."+z+".dat");
		
		if(!file.exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private final File file;
	public long lastUsed;
	private int modifies;
	
	private final int x;
	private final int z;
	private final World world;
	
	private final Byte[] groupChunk;
	
	public Byte get(int x, int y, int z) {
		lastUsed = System.currentTimeMillis();
		return groupChunk[(x * 16 + z) * 128 + y];
	}
	
	public void set(int x, int y, int z, Byte id) {
		if(isBlackListed(x, y, z)) {
		//	System.err.println("Block cannot be protected, id blacklisted!");
			return;
		}
		groupChunk[(x * 16 + z) * 128 + y] = id;
		modifies++;
		lastUsed = System.currentTimeMillis();
	}
	
	private int getModifications() {
		return modifies;
	}
	
	private void resetModifications() {
		modifies = 0;
	}
	
	public boolean isProtected(int x, int y, int z) {
		lastUsed = System.currentTimeMillis();
		return get(x,y,z) != null;
	}
	
	public boolean isProtectedFrom(int x, int y, int z, byte id) {
		if(isProtected(x,y,z)) {
			return get(x,y,z) != id;
		}
		return false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void load() {

		try {
		if(file.exists()) {
		GZIPInputStream zi;
		zi = new GZIPInputStream(new FileInputStream(file));
		byte[] data = new byte[16*16*128];
		zi.read(data);
		for(int i=0; i<data.length; i++)
		if(data[i] >= 0)
		groupChunk[i] = data[i];
		else
		groupChunk[i] = null;
		zi.close();
		resetModifications();
		} else {
			
		}
		} catch (Exception e) {
		e.printStackTrace();
		}

		}
	
	public void save() {
		if(getModifications() == 0) 
			return;
	
		try {
		GZIPOutputStream zo;
		zo = new GZIPOutputStream(new FileOutputStream(file));
		if(!file.exists())
			file.createNewFile();
		for(int i=0; i<groupChunk.length; i++)
		if(groupChunk[i] != null)
			zo.write(groupChunk[i]);
		else
			zo.write(-1);
		zo.close();
		resetModifications();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		}
	
	@Override
	public int hashCode() {
		return new StringBuilder().append(world.getName()).append(":").append(x).append(",").append(z).toString().hashCode();
	}
	
	public static int hashCode(World world, int x, int z) {
		return new StringBuilder().append(world.getName()).append(":").append(x).append(",").append(z).toString().hashCode();
	}
	
	private boolean isBlackListed(int x, int y, int z) {
		if(BananaProtect.blackList().contains(world.getChunkAt(getX(), getZ()).getBlock(x, y, z).getTypeId())) {
	//		System.err.println("Type:"+(world.getChunkAt(getX(), getZ()).getBlock(x, y, z).getTypeId()));
			return true;
		}
		return false;
	}
	
}
