package de.bananaco.bananaprotect.superstick;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import de.bananaco.bananaprotect.BananaProtect;
import de.bananaco.bananaprotect.GroupChunk;
import de.bananaco.bananaprotect.Groups;

public class SuperStickListener extends PlayerListener {

	public SuperStickListener(Groups groups, BananaProtect bananaprotect) {
		this.groups = groups;
		this.bananaprotect = bananaprotect;
		bananaprotect
				.getServer()
				.getPluginManager()
				.registerEvent(Event.Type.PLAYER_INTERACT, this,
						Priority.Normal, bananaprotect);
	}

	private final Groups groups;
	private final BananaProtect bananaprotect;

	public final Map<String, SuperStick> tmap = new HashMap<String, SuperStick>();

	public void place(Player player, SuperStick type) {
		tmap.put(player.getName(), type);
	}

	public void remove(Player player) {
		tmap.remove(player.getName());
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		Player player = event.getPlayer();
		byte group = groups.getGroup(player);
		if (!tmap.containsKey(player.getName()))
			return;
		if (!(player.getItemInHand().getType() == Material.STICK))
			return;
		if (group == -1)
			return;
		event.setCancelled(true);

		SuperStick tStick = tmap.get(player.getName());
		/*
		 * CLAIM
		 */
		if (tStick == SuperStick.CLAIM) {
			int x = block.getX() % 16;
			int y = block.getY();
			int z = block.getZ() % 16;
			if (x < 0)
				x = 16 + x;
			if (z < 0)
				z = 16 + z;

			int cx = block.getWorld().getChunkAt(block).getX();
			int cz = block.getWorld().getChunkAt(block).getZ();

			GroupChunk gc = bananaprotect.get(block.getWorld(), cx, cz);

			if (gc.isProtected(x, y, z)) {
				player.sendMessage(ChatColor.RED + "That block is protected!");
				return;
			}
			gc.set(x, y, z, group);
			player.sendMessage(ChatColor.LIGHT_PURPLE + "" + block.getX() + ","
					+ block.getY() + "," + block.getZ() + " claimed.");
			/*
			 * WHOPLACED
			 */
		} else if (tStick == SuperStick.WHOPLACED) {
			int x = block.getX() % 16;
			int y = block.getY();
			int z = block.getZ() % 16;
			if (x < 0)
				x = 16 + x;
			if (z < 0)
				z = 16 + z;

			int cx = block.getWorld().getChunkAt(block).getX();
			int cz = block.getWorld().getChunkAt(block).getZ();

			GroupChunk gc = bananaprotect.get(block.getWorld(), cx, cz);

			Byte id = gc.get(x, y, z);
			if (id == null)
				player.sendMessage(ChatColor.LIGHT_PURPLE + "" + block.getX()
						+ "," + block.getY() + "," + block.getZ()
						+ " is unclaimed.");
			else
				player.sendMessage(ChatColor.LIGHT_PURPLE + "" + block.getX()
						+ "," + block.getY() + "," + block.getZ()
						+ " belongs to group:" + id);
		}
	}
}
