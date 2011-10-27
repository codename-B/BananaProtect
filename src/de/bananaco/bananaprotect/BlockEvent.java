package de.bananaco.bananaprotect;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockEvent extends BlockListener {
	
	private final GroupChunkBlocks gcb;
	private final ExtraBlockEvent ebe;
	
	public BlockEvent(GroupChunkBlocks gcb) {
		this.gcb = gcb;
		this.ebe = new ExtraBlockEvent(gcb);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled())
			return;
		boolean cancel = gcb.blockPlace(event.getBlock().getWorld(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ(), event.getPlayer());
		event.setCancelled(cancel);
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled())
			return;
		
		boolean cancel = gcb.blockBreak(event.getBlock().getWorld(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ(), event.getPlayer());	

		event.setCancelled(cancel);
	}
	
	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if(event.isCancelled())
			return;
		for(Block block : event.getBlocks()) {
			World world = block.getWorld();
			int cx = block.getX() >> 4;
			int cz = block.getZ() >> 4;
			
			int x = block.getX() % 16;
			int y = block.getY();
			int z = block.getZ() % 16;
			
			if(x<0)
				x=16+x;
			if(z<0)
				z=16+z;
			
			boolean cancel = gcb.getCache().get(world, cx, cz).isProtected(x, y, z);
			if(cancel) {
			event.setCancelled(cancel);
			return;
			}
		}
	}
	
	public void registerEvents(JavaPlugin jp, PluginManager pm) {
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Normal, jp);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Priority.Normal, jp);
		
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this, Priority.Normal, jp);
		pm.registerEvent(Event.Type.BLOCK_BURN, this, Priority.Normal, jp);
		
		pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, this, Priority.Normal, jp);
		this.ebe.registerEvents(jp, pm);
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.isCancelled())
			return;
		World world = event.getBlock().getWorld();
		int cx = event.getBlock().getX() >> 4;
		int cz = event.getBlock().getZ() >> 4;
		
		int x = event.getBlock().getX() % 16;
		int y = event.getBlock().getY();
		int z = event.getBlock().getZ() % 16;
		
		if(x<0)
			x=16+x;
		if(z<0)
			z=16+z;
		
		boolean cancel = gcb.getCache().get(world, cx, cz).isProtected(x, y, z);
		event.setCancelled(cancel);
		
		Block rel = event.getBlock().getRelative(0, 1, 0);
		if(rel.getType() == Material.FIRE)
			rel.setType(Material.AIR);
	}
	
	@Override
	public void onBlockBurn(BlockBurnEvent event) {
		if(event.isCancelled())
			return;
		World world = event.getBlock().getWorld();
		int cx = event.getBlock().getX() >> 4;
		int cz = event.getBlock().getZ() >> 4;
		
		int x = event.getBlock().getX() % 16;
		int y = event.getBlock().getY();
		int z = event.getBlock().getZ() % 16;
		
		if(x<0)
			x=16+x;
		if(z<0)
			z=16+z;
		
		boolean cancel = gcb.getCache().get(world, cx, cz).isProtected(x, y, z);
		event.setCancelled(cancel);
		Block rel = event.getBlock().getRelative(0, 1, 0);
		if(rel.getType() == Material.FIRE)
			rel.setType(Material.AIR);
	}
	
}
class ExtraBlockEvent extends PlayerListener {

	private final GroupChunkBlocks gcb;
	private final ExtraExtraBlockEvent eebe;
	
	public ExtraBlockEvent(GroupChunkBlocks gcb) {
		this.gcb = gcb;
		this.eebe = new ExtraExtraBlockEvent(gcb);
	}

	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if(event.isCancelled())
			return;
		
		World world = event.getPlayer().getWorld();
		Player player = event.getPlayer();
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		boolean cancel = gcb.blockPlace(world, x, y, z, player);
		event.setCancelled(cancel);
	}
	
	@Override
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if(event.isCancelled())
			return;
		
		World world = event.getPlayer().getWorld();
		Player player = event.getPlayer();
		Block block = event.getBlockClicked();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		boolean cancel = gcb.blockBreak(world, x, y, z, player);
		event.setCancelled(cancel);
	}
	
	public void registerEvents(JavaPlugin jp, PluginManager pm) {
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this, Priority.Normal, jp);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this, Priority.Normal, jp);
		this.eebe.registerEvents(jp, pm);
	}
	
}
class ExtraExtraBlockEvent extends EntityListener {
	
	private final GroupChunkBlocks gcb;
	public ExtraExtraBlockEvent(GroupChunkBlocks gcb) {
		this.gcb = gcb;
	}

	@Override
	public void onPaintingPlace(PaintingPlaceEvent event) {
		if(event.isCancelled())
			return;
		World world = event.getPlayer().getWorld();
		Player player = event.getPlayer();
		int x = event.getPainting().getLocation().getBlockX();
		int y = event.getPainting().getLocation().getBlockY();
		int z = event.getPainting().getLocation().getBlockZ();
		boolean cancel = gcb.blockPlace(world, x, y, z, player);
		event.setCancelled(cancel);
	}
	
	@Override
	public void onPaintingBreak(PaintingBreakEvent e) {
		if(e.isCancelled())
			return;
		PaintingBreakByEntityEvent event = null;
		Player player = null;
		World world = null;
		if(e instanceof PaintingBreakByEntityEvent) 
			event = (PaintingBreakByEntityEvent) e;
		else
			return;
		if(event.getRemover() instanceof Player)
			player = (Player) event.getRemover();
		else
			return;
		world = player.getWorld();
		int x = event.getPainting().getLocation().getBlockX();
		int y = event.getPainting().getLocation().getBlockY();
		int z = event.getPainting().getLocation().getBlockZ();
		boolean cancel = gcb.blockBreak(world, x, y, z, player);
		event.setCancelled(cancel);
	}
	
	public void registerEvents(JavaPlugin jp, PluginManager pm) {
		pm.registerEvent(Event.Type.PAINTING_BREAK, this, Priority.Normal, jp);
		pm.registerEvent(Event.Type.PAINTING_PLACE, this, Priority.Normal, jp);
	}
}