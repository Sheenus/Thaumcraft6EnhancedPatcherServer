package thaumcraft.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;

public class ThaumcraftMaterials
{
    public static Item.ToolMaterial TOOLMAT_THAUMIUM;
    public static Item.ToolMaterial TOOLMAT_VOID;
    public static Item.ToolMaterial TOOLMAT_ELEMENTAL;
    public static ItemArmor.ArmorMaterial ARMORMAT_THAUMIUM;
    public static ItemArmor.ArmorMaterial ARMORMAT_SPECIAL;
    public static ItemArmor.ArmorMaterial ARMORMAT_VOID;
    public static ItemArmor.ArmorMaterial ARMORMAT_VOIDROBE;
    public static ItemArmor.ArmorMaterial ARMORMAT_FORTRESS;
    public static ItemArmor.ArmorMaterial ARMORMAT_CULTIST_PLATE;
    public static ItemArmor.ArmorMaterial ARMORMAT_CULTIST_ROBE;
    public static ItemArmor.ArmorMaterial ARMORMAT_CULTIST_LEADER;
    public static final Material MATERIAL_TAINT;
    
    /*
     * For modifying armor materials, the int array specifies the armor values for each piece {feet, legs, body, head}. the final float specifies the toughness value each piece of armor gets.
     */
    static {
        ThaumcraftMaterials.TOOLMAT_THAUMIUM = EnumHelper.addToolMaterial("THAUMIUM", 3, 500, 7.0f, 2.5f, 22).setRepairItem(new ItemStack(ItemsTC.ingots));
        ThaumcraftMaterials.TOOLMAT_VOID = EnumHelper.addToolMaterial("VOID", 4, 150, 8.0f, 3.0f, 10).setRepairItem(new ItemStack(ItemsTC.ingots, 1, 1));
        ThaumcraftMaterials.TOOLMAT_ELEMENTAL = EnumHelper.addToolMaterial("THAUMIUM_ELEMENTAL", 3, 1500, 9.0f, 3.0f, 18).setRepairItem(new ItemStack(ItemsTC.ingots));
        ThaumcraftMaterials.ARMORMAT_THAUMIUM = EnumHelper.addArmorMaterial("THAUMIUM", "THAUMIUM", 25, new int[] { 2, 5, 6, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f);
        ThaumcraftMaterials.ARMORMAT_SPECIAL = EnumHelper.addArmorMaterial("SPECIAL", "SPECIAL", 25, new int[] { 1, 2, 3, 1 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0f);
        ThaumcraftMaterials.ARMORMAT_VOID = EnumHelper.addArmorMaterial("VOID", "VOID", 10, new int[] { 3, 6, 8, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0f);
        ThaumcraftMaterials.ARMORMAT_VOIDROBE = EnumHelper.addArmorMaterial("VOIDROBE", "VOIDROBE", 18, new int[] { 3, 6, 7, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2.0f);
        ThaumcraftMaterials.ARMORMAT_FORTRESS = EnumHelper.addArmorMaterial("FORTRESS", "FORTRESS", 40, new int[] { 4, 7, 9, 4 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3.0f);
        ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE = EnumHelper.addArmorMaterial("CULTIST_PLATE", "CULTIST_PLATE", 18, new int[] { 2, 5, 6, 2 }, 13, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f);
        ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE = EnumHelper.addArmorMaterial("CULTIST_ROBE", "CULTIST_ROBE", 17, new int[] { 2, 4, 5, 2 }, 13, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
        ThaumcraftMaterials.ARMORMAT_CULTIST_LEADER = EnumHelper.addArmorMaterial("CULTIST_LEADER", "CULTIST_LEADER", 30, new int[] { 3, 6, 7, 3 }, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f);
        MATERIAL_TAINT = new MaterialTaint();
    }
    
    public static class MaterialTaint extends Material
    {
        public MaterialTaint() {
            super(MapColor.PURPLE);
            this.setNoPushMobility();
        }
        
        public boolean blocksMovement() {
            return true;
        }
    }
}

