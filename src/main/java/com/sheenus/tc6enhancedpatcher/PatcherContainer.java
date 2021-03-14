package com.sheenus.tc6enhancedpatcher;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class PatcherContainer extends DummyModContainer {

	public PatcherContainer() {

		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "thaumcraft6enhancedpatcher";
		meta.name = "Thaumcraft 6 Enhanced Patcher";
		meta.version = "1.0.2"; 	// String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
		meta.credits = "Azanor for making such an amazing IP and mod, DidYouIronTheCat for running the TC-RP Server, Kookyboy9 for showing and letting me use some of his code";
		meta.authorList = Arrays.asList("Sheenus");
		meta.description = "Coremod patcher for TC6 to modify Thaumcraft 6 classes in order to add perceived needed functionality and fix bugs. This is a server-side patcher, with many references to client-side code removed. FOR USE ON DEDICATED SERVERS ONLY.";
		meta.url = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";

	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	
	@Subscribe
	public void modConstruction(FMLConstructionEvent evt){

	}

	
	@Subscribe
	public void preInit(FMLPreInitializationEvent evt) {
	}
	
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {

	}
	
	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {

	}

}