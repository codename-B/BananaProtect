package de.bananaco.bananaprotect;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bananaco.bananaprotect.superstick.SuperStick;

public class BananaProtectExec {

	BananaProtectExec(BananaProtect bp, Groups groups) {
		this.groups = groups;
		this.bp = bp;
		setupValues();
	}

	private BananaProtect bp;
	private Groups groups;
	private HashMap<Byte, HashSet<String>> mappy;

	private void setupValues() {
		mappy = new HashMap<Byte, HashSet<String>>();
		for (int i = 0; i < 255; i++)
			mappy.put((byte) i, new HashSet<String>());
	}

	public boolean isAdmin(Player player) {
		return player.hasPermission("bananaprotect.admin");
	}

	public boolean isLeader(Player player) {
		return player.hasPermission("bananaprotect.leader");
	}
	
	public boolean isSuper(Player player) {
		return player.hasPermission("bananaprotect.super");
	}
	
	public boolean isUser(Player player) {
		return player.hasPermission("bananaprotect.user");
	}

	public byte getGroup(Player player) {
		return groups.getGroup(player);
	}

	public void addRequest(Player player, Byte bt) throws Exception {
		HashSet<String> set = mappy.get(bt);
		set.add(player.getName());
	}

	public boolean containsRequest(Player player, Byte bt) throws Exception {
		HashSet<String> set = mappy.get(bt);
		return set.contains(player.getName());
	}

	public void removeRequest(Player player, Byte bt) throws Exception {
		HashSet<String> set = mappy.get(bt);
		set.remove(player.getName());
	}

	public void onCommand(CommandSender sender, String command, String target,
			String argument) throws Exception {
		/*
		 * This command allows a server admin to specify a group "leader" This
		 * leader can protect unprotected blocks and accept new group members
		 * Usage: /setleader NAME true|false
		 */
/*		if (command.equalsIgnoreCase("setleader")) {
			boolean allowed = true;
			if (sender instanceof Player) {
				allowed = isAdmin((Player) sender);
			}
			if (allowed) {
				// Now this
				Player tp = bp.getPlayer(target, sender);
				boolean vl = Boolean.valueOf(argument);

				if (tp == null)
					throw new NullPointerException(target + " not online");

				groups.setLeader(tp, vl);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + tp.getName()
						+ ".leader:" + vl);
			} else {
				sender.sendMessage(ChatColor.RED + "Too bad, so sad.");
			}
		} 
*/
		
		/*
		 * This command allows a server admin to specify a group for a player
		 * Usage: /setplayer NAME 0-255
		 */
		if (command.equalsIgnoreCase("setplayer")) {
			boolean allowed = true;
			if (sender instanceof Player) {
				allowed = isAdmin((Player) sender);
			}
			if (allowed) {
				// Now this
				Player tp = bp.getPlayer(target, sender);
				byte bt = (byte) Integer.parseInt(argument);

				if (tp == null)
					throw new NullPointerException(target + " not online");

				groups.setGroup(tp, bt);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + tp.getName()
						+ ".group:" + bt);
			} else {
				sender.sendMessage(ChatColor.RED + "Too bad, so sad.");
			}
		}
		/*
		 * These are a series of Player only commands. To avoid issues with
		 * console names etc, we don't messing about with fakePlayer objects for
		 * the console.
		 */
		else if (sender instanceof Player) {
			Player player = (Player) sender;
			/*
			 * This command allows group leaders to claim unclaimed blocks
			 * with the superstick
			 * Usage: /bananastick claim|whoplaced
			 */
			if(command.equalsIgnoreCase("superstick")) {
				if(isSuper(player))
				if(target == null) {
				bp.getListener().remove(player);
				player.sendMessage(ChatColor.AQUA+"SuperStick disabled");
				}
				else {
				SuperStick type = SuperStick.getByName(target);
				bp.getListener().place(player, type);
				player.sendMessage(ChatColor.AQUA+"SuperStick enabled - type:"+type.getName());
				}
				else
				player.sendMessage(ChatColor.RED+"Nu-uh. You're not a leader.");
			}
			/*
			 * This simple command lets a player request to join a group Usage:
			 * /requestgroup 0-255
			 */
			if (command.equalsIgnoreCase("requestgroup")) {
				if(isLeader(player)) {
				player.sendMessage(ChatColor.RED+"Leaders cannot join other groups!");	
				} else {
				Byte bt = (byte) Integer.parseInt(target);
				addRequest(player, bt);
				player.sendMessage(ChatColor.LIGHT_PURPLE+"Request put in for group:" + bt);
				}
			}
			/*
			 * This command lets a group leader accept a join request from a
			 * player Usage: /acceptplayer NAME
			 */
			if (command.equalsIgnoreCase("acceptplayer")) {
				if (isLeader(player)) {
					Player tp = bp.getPlayer(target, sender);
					Byte bt = groups.getGroup(player);
					if (containsRequest(tp, bt)) {
						removeRequest(tp, bt);
						player.sendMessage(ChatColor.LIGHT_PURPLE
								+ tp.getName() + " accepted!");
					} else {
						player.sendMessage(ChatColor.LIGHT_PURPLE
								+ "No request from " + tp.getName());
					}
				} else {
					player.sendMessage(ChatColor.RED+"You must be super to do this...");
				}
			}
			/*
			 * This command lets someone leave a group. If a leader leaves, they
			 * automatically lose leader status (duh). Usage: /leavegroup
			 */
			if (command.equalsIgnoreCase("leavegroup")) {
				if(isUser(player)) {
				Byte group = groups.getGroup(player);
				groups.setGroup(player, (byte) -1);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Left group:"
						+ group);
				} else {
					player.sendMessage(ChatColor.RED+"You are not allowed to do this.");
				}
			}
		} else {
			sender.sendMessage(command + " is a player-only command!");
		}
	}

}
