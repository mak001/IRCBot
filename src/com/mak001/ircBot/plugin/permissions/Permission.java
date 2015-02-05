package com.mak001.ircBot.plugin.permissions;

import java.util.ArrayList;
import java.util.List;


public class Permission {

	private final String perm;
	private final String description;
	private List<String> parents = new ArrayList<String>();

	public Permission(String perm) {
		this(perm, "");
	}

	public Permission(String perm, String description) {
		this.perm = perm;
		this.description = description;
		
	}
	
	public String getPermissionString(){
		return perm;
	}
	
	public String getDescription(){
		return description;
	}

	public List<String> getParents(){
		return parents;
	}
}
