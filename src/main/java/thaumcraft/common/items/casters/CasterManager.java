package thaumcraft.common.items.casters;

import net.minecraft.entity.EntityLivingBase;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.FocusEngine;
import java.util.Iterator;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.SoundsTC;
import java.util.TreeMap;
import net.minecraft.world.World;
import thaumcraft.api.casters.ICaster;
import net.minecraft.inventory.IInventory;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.api.potions.PotionVisExhaust;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;

public class CasterManager
{
    static HashMap<Integer, Long> cooldownServer;
    static HashMap<Integer, Long> cooldownClient;
    
    /*
     * calculates the total vis discount from all pieces of gear a player has equipped (including main-hand and off-hand items, something ol' Az forgot to do), as well as factor in if the player has Flux flu/flux Phage.
     */
    public static float getTotalVisDiscount(final EntityPlayer player) {
        int totalDiscount = 0;
    // check that the function is being performed on a player (other entities aren't special enough to get the reduction!)
        if (player == null) { return (float)totalDiscount; }
    // check the baubles for IVisDiscountGear
        final IInventory baubles = BaublesApi.getBaubles(player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof IVisDiscountGear) {
                totalDiscount += ((IVisDiscountGear)baubles.getStackInSlot(a).getItem()).getVisDiscount(baubles.getStackInSlot(a), player);
            }
        }
    // check armor for IVisDiscountGear
        for (int a = 0; a < 4; ++a) {
            if (((ItemStack)player.inventory.armorInventory.get(a)).getItem() instanceof IVisDiscountGear) {
                totalDiscount += ((IVisDiscountGear)((ItemStack)player.inventory.armorInventory.get(a)).getItem()).getVisDiscount((ItemStack)player.inventory.armorInventory.get(a), player);
            }
        }
    // check main hand and off-hand for the relevant IVisDiscountGear (basically just the gauntlets), and apply the highest value
        int MHDiscount = 0;
        int OHDiscount = 0;
        
        if ((player.inventory.mainInventory.get(player.inventory.currentItem).getItem() instanceof IVisDiscountGear) && (player.inventory.mainInventory.get(player.inventory.currentItem).getItem() instanceof ItemCaster)) {
        	MHDiscount = ((IVisDiscountGear)player.inventory.mainInventory.get(player.inventory.currentItem).getItem()).getVisDiscount(player.inventory.mainInventory.get(player.inventory.currentItem), player);
        }
        
        if ((player.inventory.offHandInventory.get(0).getItem() instanceof IVisDiscountGear) && (player.inventory.offHandInventory.get(0).getItem() instanceof ItemCaster)) {
        	OHDiscount = ((IVisDiscountGear)player.inventory.offHandInventory.get(0).getItem()).getVisDiscount(player.inventory.offHandInventory.get(0), player);
        }
        totalDiscount += Math.max(MHDiscount, OHDiscount);
    // checks to see if the player has Flux Flu or Flux Phage
        if (player.isPotionActive(PotionVisExhaust.instance) || player.isPotionActive(PotionInfectiousVisExhaust.instance)) {
            int level1 = 0;
            int level2 = 0;
            if (player.isPotionActive(PotionVisExhaust.instance)) {
                level1 = player.getActivePotionEffect(PotionVisExhaust.instance).getAmplifier();
            }
            if (player.isPotionActive(PotionInfectiousVisExhaust.instance)) {
                level2 = player.getActivePotionEffect(PotionInfectiousVisExhaust.instance).getAmplifier();
            }
            totalDiscount -= (Math.max(level1, level2) + 1) * 10;
        }
     // return the calculated total cooldown reduction, converting to a decimal value (for later calculation)
        return totalDiscount / 100.0f;
    }
    
    public static boolean consumeVisFromInventory(final EntityPlayer player, final float cost) {
        for (int a = player.inventory.mainInventory.size() - 1; a >= 0; --a) {
            final ItemStack item = (ItemStack)player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ICaster) {
                final boolean done = ((ICaster)item.getItem()).consumeVis(item, player, cost, true, false);
                if (done) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void changeFocus(final ItemStack is, final World w, final EntityPlayer player, final String focus) {
        final ICaster wand = (ICaster)is.getItem();
        final TreeMap<String, Integer> foci = new TreeMap<String, Integer>();
        final HashMap<Integer, Integer> pouches = new HashMap<Integer, Integer>();
        int pouchcount = 0;
        ItemStack item = ItemStack.EMPTY;
        final IInventory baubles = BaublesApi.getBaubles(player);
        for (int a = 0; a < baubles.getSizeInventory(); ++a) {
            if (baubles.getStackInSlot(a).getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                item = baubles.getStackInSlot(a);
                pouches.put(pouchcount, a - 4);
                final NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = (ItemStack)inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        final String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        for (int a = 0; a < 36; ++a) {
            item = (ItemStack)player.inventory.mainInventory.get(a);
            if (item.getItem() instanceof ItemFocus) {
                final String sh2 = ((ItemFocus)item.getItem()).getSortingHelper(item);
                if (sh2 == null) {
                    continue;
                }
                foci.put(sh2, a);
            }
            if (item.getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                pouches.put(pouchcount, a);
                final NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = (ItemStack)inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        final String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        if (focus.equals("REMOVE") || foci.size() == 0) {
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.inventory.addItemStackToInventory(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, null);
                player.playSound(SoundsTC.ticks, 0.3f, 0.9f);
            }
            return;
        }
        if (foci != null && foci.size() > 0 && focus != null) {
            String newkey = focus;
            if (foci.get(newkey) == null) {
                newkey = foci.higherKey(newkey);
            }
            if (newkey == null || foci.get(newkey) == null) {
                newkey = foci.firstKey();
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                item = ((ItemStack)player.inventory.mainInventory.get((int)foci.get(newkey))).copy();
            }
            else {
                final int pid = foci.get(newkey) / 1000;
                if (pouches.containsKey(pid)) {
                    final int pouchslot = pouches.get(pid);
                    final int focusslot = foci.get(newkey) - pid * 1000;
                    ItemStack tmp = ItemStack.EMPTY;
                    if (pouchslot >= 0) {
                        tmp = ((ItemStack)player.inventory.mainInventory.get(pouchslot)).copy();
                    }
                    else {
                        tmp = baubles.getStackInSlot(pouchslot + 4).copy();
                    }
                    item = fetchFocusFromPouch(player, focusslot, tmp, pouchslot);
                }
            }
            if (item == null || item.isEmpty()) {
                return;
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                player.inventory.setInventorySlotContents((int)foci.get(newkey), ItemStack.EMPTY);
            }
            player.playSound(SoundsTC.ticks, 0.3f, 1.0f);
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.inventory.addItemStackToInventory(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, ItemStack.EMPTY);
            }
            if (wand.getFocus(is) == null) {
                wand.setFocus(is, item);
            }
            else if (!addFocusToPouch(player, item, pouches)) {
                player.inventory.addItemStackToInventory(item);
            }
        }
    }
    
    private static ItemStack fetchFocusFromPouch(final EntityPlayer player, final int focusid, final ItemStack pouch, final int pouchslot) {
        ItemStack focus = ItemStack.EMPTY;
        final NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
        final ItemStack contents = (ItemStack)inv.get(focusid);
        if (contents.getItem() instanceof ItemFocus) {
            focus = contents.copy();
            inv.set(focusid, (ItemStack)ItemStack.EMPTY);
            ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
            if (pouchslot >= 0) {
                player.inventory.setInventorySlotContents(pouchslot, pouch);
                player.inventory.markDirty();
            }
            else {
                final IInventory baubles = BaublesApi.getBaubles(player);
                baubles.setInventorySlotContents(pouchslot + 4, pouch);
                BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
                baubles.markDirty();
            }
        }
        return focus;
    }
    
    private static boolean addFocusToPouch(final EntityPlayer player, final ItemStack focus, final HashMap<Integer, Integer> pouches) {
        final IInventory baubles = BaublesApi.getBaubles(player);
        for (final Integer pouchslot : pouches.values()) {
            ItemStack pouch;
            if (pouchslot >= 0) {
                pouch = (ItemStack)player.inventory.mainInventory.get((int)pouchslot);
            }
            else {
                pouch = baubles.getStackInSlot(pouchslot + 4);
            }
            final NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
            for (int q = 0; q < inv.size(); ++q) {
                final ItemStack contents = (ItemStack)inv.get(q);
                if (contents.isEmpty()) {
                    inv.set(q, (ItemStack)focus.copy());
                    ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
                    if (pouchslot >= 0) {
                        player.inventory.setInventorySlotContents((int)pouchslot, pouch);
                        player.inventory.markDirty();
                    }
                    else {
                        baubles.setInventorySlotContents(pouchslot + 4, pouch);
                        BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
                        baubles.markDirty();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void toggleMisc(final ItemStack itemstack, final World world, final EntityPlayer player, final int mod) {
        if (!(itemstack.getItem() instanceof ICaster)) {
            return;
        }
        final ICaster caster = (ICaster)itemstack.getItem();
        final ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        final FocusPackage fp = ItemFocus.getPackage(caster.getFocusStack(itemstack));
        if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
            int dim = getAreaDim(itemstack);
            if (mod == 0) {
                int areax = getAreaX(itemstack);
                int areay = getAreaY(itemstack);
                int areaz = getAreaZ(itemstack);
                final int max = getAreaSize(itemstack);
                if (dim == 0) {
                    ++areax;
                    ++areaz;
                    ++areay;
                }
                else if (dim == 1) {
                    ++areax;
                }
                else if (dim == 2) {
                    ++areaz;
                }
                else if (dim == 3) {
                    ++areay;
                }
                if (areax > max) {
                    areax = 0;
                }
                if (areaz > max) {
                    areaz = 0;
                }
                if (areay > max) {
                    areay = 0;
                }
                setAreaX(itemstack, areax);
                setAreaY(itemstack, areay);
                setAreaZ(itemstack, areaz);
            }
            if (mod == 1) {
                if (++dim > 3) {
                    dim = 0;
                }
                setAreaDim(itemstack, dim);
            }
        }
    }
    
    private static int getAreaSize(final ItemStack itemstack) {
        final boolean pot = false;
        if (!(itemstack.getItem() instanceof ICaster)) {
            return 0;
        }
        final ICaster caster = (ICaster)itemstack.getItem();
        final ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        return pot ? 6 : 3;
    }
    
    public static int getAreaDim(final ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aread")) {
            return stack.getTagCompound().getInteger("aread");
        }
        return 0;
    }
    
    public static int getAreaX(final ItemStack stack) {
        final ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areax")) {
            int a = stack.getTagCompound().getInteger("areax");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaY(final ItemStack stack) {
        final ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areay")) {
            int a = stack.getTagCompound().getInteger("areay");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaZ(final ItemStack stack) {
        final ICaster wand = (ICaster)stack.getItem();
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("areaz")) {
            int a = stack.getTagCompound().getInteger("areaz");
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static void setAreaX(final ItemStack stack, final int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areax", area);
        }
    }
    
    public static void setAreaY(final ItemStack stack, final int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areay", area);
        }
    }
    
    public static void setAreaZ(final ItemStack stack, final int area) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("areaz", area);
        }
    }
    
    public static void setAreaDim(final ItemStack stack, final int dim) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setInteger("aread", dim);
        }
    }
    
    public static boolean isOnCooldown(final EntityLivingBase entityLiving) {
        if (entityLiving.world.isRemote && CasterManager.cooldownClient.containsKey(entityLiving.getEntityId())) {
            return CasterManager.cooldownClient.get(entityLiving.getEntityId()) > System.currentTimeMillis();
        }
        return !entityLiving.world.isRemote && CasterManager.cooldownServer.containsKey(entityLiving.getEntityId()) && CasterManager.cooldownServer.get(entityLiving.getEntityId()) > System.currentTimeMillis();
    }
    
    public static float getCooldown(final EntityLivingBase entityLiving) {
        if (entityLiving.world.isRemote && CasterManager.cooldownClient.containsKey(entityLiving.getEntityId())) {
            return (CasterManager.cooldownClient.get(entityLiving.getEntityId()) - System.currentTimeMillis()) / 1000.0f;
        }
        return 0.0f;
    }
    
    public static void setCooldown(final EntityLivingBase entityLiving, final int cd) {
        if (cd == 0) {
            CasterManager.cooldownClient.remove(entityLiving.getEntityId());
            CasterManager.cooldownServer.remove(entityLiving.getEntityId());
        }
        else if (entityLiving.world.isRemote) {
            CasterManager.cooldownClient.put(entityLiving.getEntityId(), System.currentTimeMillis() + cd * 50);
        }
        else {
            CasterManager.cooldownServer.put(entityLiving.getEntityId(), System.currentTimeMillis() + cd * 50);
        }
    }
    
    static {
        CasterManager.cooldownServer = new HashMap<Integer, Long>();
        CasterManager.cooldownClient = new HashMap<Integer, Long>();
    }
}
