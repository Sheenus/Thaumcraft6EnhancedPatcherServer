package com.sheenus.tc6enhancedpatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;

public class TC6EnhancedPatcher implements IClassTransformer{
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		String[] classesToPatch = { "thaumcraft.proxies.CommonProxy",
									"thaumcraft.common.items.casters.CasterManager",
									"thaumcraft.client.lib.events.HudHandler",
									"thaumcraft.common.items.armor.ItemFortressArmor",
									"thaumcraft.common.items.armor.ItemVoidRobeArmor",
									"thaumcraft.common.config.ModConfig",
									"thaumcraft.common.lib.events.PlayerEvents",
									"thaumcraft.api.ThaumcraftMaterials"
				};
		
		for (int i = 0; i == classesToPatch.length; i++) {
			if(name.contains(classesToPatch[i])) {
				System.out.println("patching " + classesToPatch[i] + "... ");
				basicClass = patchClassInJar(name, basicClass, name, PatcherLoader.location);
				
			}
		}
		return basicClass;
	}
	
	/* @Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.contains("thaumcraft.proxies.CommonProxy")) {
			basicClass = patchClassInJar(name, basicClass, name, PatcherLoader.location);
		}
		return basicClass;
	}*/
	
	public byte[] patchClassInJar(String name, byte[] bytes, String ObfName, File location) {
		PatcherLoader.log.info("************* Patching: " + name + "******************");
		try {
			//open the jar as zip
			ZipFile zip = new ZipFile(location.toString().replace("%20", " "));
			//find the file inside the zip that is called te.class or net.minecraft.entity.monster.EntityCreeper.class
			//replacing the . to / so it would look for net/minecraft/entity/monster/EntityCreeper.class
			ZipEntry entry = zip.getEntry(name.replace('.', '/') + ".class");
			PatcherLoader.log.info(name.replace('.', '/') + ".class");
	
	
			if (entry == null) {
				PatcherLoader.log.info(name + " not found in " + location.getName());
			} else {
	
				//serialize the class file into the bytes array
				InputStream zin = zip.getInputStream(entry);
				bytes = new byte[(int) entry.getSize()];
				int len = zin.read(bytes);
				PatcherLoader.log.info(len + " bytes found");
				zin.close();
			}
			zip.close();
			} catch (Exception e) {
				try {
					PatcherLoader.log.info("Looks like this may be a dev enviroment...");
					PatcherLoader.log.info(e.getLocalizedMessage());
					File file = new File(location.getAbsolutePath() + "/" + name.replace(".", "/") + ".class");
					byte[] fileData = new byte[(int)file.length()];
					FileInputStream in = new FileInputStream(file);
					in.read(fileData);
					in.close();
					bytes = fileData;
				} catch(Exception err){
					throw new RuntimeException("Error overriding " + name + " from " + location.getName(), err);
				}
			}
			//return the new bytes
			return bytes;
		}
}
