package com.sheenus.tc6enhancedpatcher;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.12.2")
public class PatcherLoader implements IFMLLoadingPlugin{

	public static File location;
	public static final Logger log = LogManager.getLogger("Thaumcraft6patcher");
	
	/* 
	 * appends the list of strings (full class name-locations) to the full list of classes that use the IClassTransformer interface (classes that need to be overwritten post-init I think?)
	 * append classes with full name-location from their .jar root here as a string in order to overwrite them in other mods.
	 */
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { // "com.sheenus.tc6enhancedpatcher.TC6EnhancedPatcher",
				"com.sheenus.tc6enhancedpatcher.CasterManagerPatcher",
				"com.sheenus.tc6enhancedpatcher.ItemFortressArmorPatcher",
				"com.sheenus.tc6enhancedpatcher.ItemVoidRobeArmorPatcher",
				"com.sheenus.tc6enhancedpatcher.ModConfigPatcher",
				"com.sheenus.tc6enhancedpatcher.PlayerEventsPatcher",
				"com.sheenus.tc6enhancedpatcher.ThaumcraftMaterialsPatcher",
		};
	}
	
	/*
	 * Method for forge to identify the class to use for the container for the mod; normally said information would be pulled from the Main.class, however without one to speak of as a FML plugin it does not follow standard forge layout. 
	 */
	@Override
	public String getModContainerClass() {
		return "com.sheenus.tc6enhancedpatcher.PatcherContainer";
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		location = new File(PatcherLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		location = new File(location.getAbsolutePath());

	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}