package pl.bkkuc.atomicitems.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.bkkuc.atomicitems.Plugin;
import pl.bkkuc.atomicitems.tools.ColorUtility;

import java.lang.reflect.Field;
import java.util.UUID;

public class ItemBuilder {

    public static ItemStack get(Item item){

        ConfigurationSection section = Plugin.getInstance().getItems().getConfigurationSection("items." + item.getName());

        if(section.getKeys(false).isEmpty()){
            Plugin.getInstance().getLogger().warning("Секция предмет '" + item.getName() + "' пуста.");
            return new ItemStack(Material.STONE);
        }

        ItemStack newItemStack = getMaterial(section.getString("material", "STONE"));
        ItemMeta meta = newItemStack.getItemMeta();

        if(section.get("name") != null) meta.setDisplayName(ColorUtility.color(section.getString("name")));
        if(section.get("lore") != null) meta.setLore(ColorUtility.color(section.getStringList("lore")));

        newItemStack.setItemMeta(meta);

        if(section.get("type") != null){
            NBTItem nbtItem = new NBTItem(newItemStack);
            nbtItem.setString("atomicitems", section.getString("type"));
            nbtItem.applyNBT(newItemStack);
        }

        return newItemStack;
    }

    public static ItemStack getMaterial(String material) {
        if (material.startsWith("player_head:"))
            return playerHead(material);
        if (material.startsWith("head:"))
            return getBase64Head(material.split(":")[1]);

        Material matchedMaterial = Material.matchMaterial(material.toUpperCase());
        if (matchedMaterial == null) {
            Plugin.getInstance().getLogger().warning("Материал '" + material + "' не найден.");
            return new ItemStack(Material.STONE);
        }

        return new ItemStack(matchedMaterial, 1);
    }

    public static ItemStack playerHead(String owner) {
        ItemStack item;
        if (Material.matchMaterial("PLAYER_HEAD") != null) {
            item = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } else {
            item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1);
            item.setDurability((short) 3);
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getBase64Head(String value) {
        ItemStack item;
        if (Material.matchMaterial("PLAYER_HEAD") != null) {
            item = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } else {
            item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1);
            item.setDurability((short) 3);
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        item.setItemMeta(meta);
        return item;
    }
}
