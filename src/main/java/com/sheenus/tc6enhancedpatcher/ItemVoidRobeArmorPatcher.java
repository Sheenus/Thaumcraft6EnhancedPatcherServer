package com.sheenus.tc6enhancedpatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;

public class ItemVoidRobeArmorPatcher implements IClassTransformer{
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.contains("thaumcraft.common.items.armor.ItemVoidRobeArmor")) {
			basicClass = patchClassInJar(name, basicClass, name, PatcherLoader.location);
		}
		return basicClass;
	}
	public byte[] patchClassInJar(String name, byte[] bytes, String ObfName, File location) {
		PatcherLoader.log.info("************* Patching: " + name + "******************");
		try {
			//open the jar as zip
			ZipFile zip = new ZipFile(location.toString().replace("%20", " "));
			//find the file inside the zip that is called te.class or net.minecraft.entity.monster.EntityCreeper.class
			//replacing the . to / so it would look for net/minecraft/entity/monster/EntityCreeper.class
			ZipEntry entry = zip.getEntry(name.replace('.', '/') + ".class");
			
			if (entry == null) {
				PatcherLoader.log.info(name + " not found in " + location.getName());
			} else { 
				PatcherLoader.log.info("Found " + entry.toString() + " of size " + entry.getSize() + " bytes.");
	
				//serialize the class file into the bytes array
				InputStream zin = zip.getInputStream(entry);
				bytes = new byte[(int) entry.getSize()];
				
				int offset = 0;
				while (offset < bytes.length) {
					int bytesRead = zin.read(bytes, offset, (bytes.length - offset));
					if (bytesRead == -1) {
						break;
					}
					offset += bytesRead;
					PatcherLoader.log.info(offset + " bytes read");
				}
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
