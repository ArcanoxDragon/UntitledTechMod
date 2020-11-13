package me.arcanox.techmod.api;

import org.jetbrains.annotations.Contract;

public class TechModApi {
	private static ITechModApi instance;
	
	@Contract( pure = true )
	public static ITechModApi getInstance() {
		return TechModApi.instance;
	}
	
	@Contract( pure = true )
	public static boolean isModAvailable() {
		return TechModApi.instance != null;
	}
	
	static void initialize( ITechModApi instance ) {
		if ( TechModApi.instance != null ) throw new UnsupportedOperationException( "Cannot initialize an already initialized API" );
		
		TechModApi.instance = instance;
	}
}
