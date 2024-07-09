package dittonut;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.common.PluginMessagePacket;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.tag.Tag;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        VelocityProxy.enable("vlGL5tcJ0VoX");
        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the ChunkGenerator
        instanceContainer.setGenerator(ignored -> {
        });

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 0, 0));


        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Inventory inventory = createInventory();
            event.getPlayer().openInventory(inventory);
        });

        globalEventHandler.addListener(InventoryCloseEvent.class, event -> {
            Inventory inventory = createInventory();
            event.getPlayer().openInventory(inventory);
        });

        globalEventHandler.addListener(InventoryPreClickEvent.class, event -> {
            Material n = event.getClickedItem().material();
            if (n==Material.GRASS_BLOCK) {
                send(event.getPlayer(), "wild");
            } else if (n==Material.COMMAND_BLOCK) {
                send(event.getPlayer(), "play");
            } else if (n==Material.BARRIER) {
		event.getPlayer().kick("Bye!");
	    } else {
                event.setCancelled(true);
            }
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 30001);
    }
    private static Inventory createInventory() {
        Inventory i = new Inventory(InventoryType.CHEST_3_ROW, Component.text("서버 선택"));
        ItemStack ph = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.text(" "));

        // Wild
        i.setItemStack(11, ItemStack.of(Material.GRASS_BLOCK).withDisplayName(Component.text("야생서버").decoration(TextDecoration.ITALIC, false)).withTag(Tag.String("server"), "wild"));
        // Play
        i.setItemStack(15, ItemStack.of(Material.COMMAND_BLOCK).withDisplayName(Component.text("놀이터서버").decoration(TextDecoration.ITALIC, false)).withTag(Tag.String("server"), "play"));
        // Exit
	i.setItemStack(22, ItemStack.of(Material.BARRIER).withDisplayName(Component.text("나가기").decoration(TextDecoration.ITALIC, false)).withTag(Tag.String("server"), "exit"));
 

        for (int slot = 0; slot < i.getSize(); slot++) {
            if (i.getItemStack(slot).isAir()) i.setItemStack(slot, ph);
        }
        return i;
    }
    public static void send(Player player, String serverName) {
	/*BinaryWriter out = new BinaryWriter();
	out.writeSizedString("Connect");
	out.writeSizedString(serverName);
	
        PluginMessagePacket pluginMessagePacket = new PluginMessagePacket("BungeeCord", out.toByteArray());
	player.sendPacket(pluginMessagePacket);
        System.out.println("Sending "+player.getUsername()+" to "+serverName);*/
	ByteArrayDataOutput out = ByteStreams.newDataOutput();
	out.writeUTF("Connect");
	out.writeUTF(serverName);

	player.sendPacket(new PluginMessagePacket("BungeeCord", out.toByteArray()));
    }  
}
