package de.bananaco.bananaprotect.superstick;

import java.lang.EnumConstantNotPresentException;

public enum SuperStick {
	
	CLAIM("claim"),
	WHOPLACED("whoplaced");
	
	private final String name;
	
	SuperStick(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static SuperStick getByName(String name) throws Exception {
		for(SuperStick ss : SuperStick.values()) {
			if(name.equalsIgnoreCase(ss.getName()))
				return ss;
		}		
		throw new EnumConstantNotPresentException(SuperStick.class, "Cannot match: "+name);
	}
}