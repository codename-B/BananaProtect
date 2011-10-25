package de.bananaco.bananaprotect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public class ParentPermission {

	private Map<String, Boolean> children = null;
	private String name = null;
	private PermissionDefault defaultValue = null;
	
	public ParentPermission(String name, PermissionDefault defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	public ParentPermission(String name, PermissionDefault defaultValue,
			String child) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.children = new HashMap<String, Boolean>();
		children.put(child, true);
	}
	
	public Permission getPermission() {
		if(children == null)
			return new Permission(name, defaultValue);
		return new Permission(name, defaultValue, children);
	}
	
	public void registerPermission(PluginManager pm) {
		pm.addPermission(getPermission());
	}

}
