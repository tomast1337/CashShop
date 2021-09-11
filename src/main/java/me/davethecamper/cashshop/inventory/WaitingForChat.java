package me.davethecamper.cashshop.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.davethecamper.cashshop.CashShop;
import me.davethecamper.cashshop.inventory.configs.IdentificableMenu;

public class WaitingForChat implements Listener {
	
	public WaitingForChat(final UUID player, final WaitingForChat.Primitives type, final String var_name, final IdentificableMenu caller) {
		this.player = player;
		this.type = type;
		this.caller = caller;
		this.var_name = var_name;
		
		
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME));
		
		Bukkit.getPlayer(player).sendMessage(caller.getMessages().getString("chat.to_do." + type.toString()));
		Bukkit.getPlayer(player).closeInventory();
	}
	
	private String var_name;
	
	private UUID player;
	
	private WaitingForChat.Primitives type;
	
	private IdentificableMenu caller;
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().getUniqueId().equals(player)) {
			e.setCancelled(true);
			String message = e.getMessage();
			
			if (isValid(message)) {
				Object o = message;
				
				switch (type) {
					case INTEGER:
						o = Integer.parseInt(message);
						break;
						
					case LONG:
						o = Long.parseLong(message);
						break;
						
					case DOUBLE:
						o = Double.parseDouble(message);
						break;
						
					case FLOAT:
						o = Float.parseFloat(message);
						break;
						
					case STRING:
						o = message.replaceAll("&", "�");
						break;
				}
				
				finish(o);
			} else {
				e.getPlayer().sendMessage(caller.getMessages().getString("chat.generic_error." + type.toString()));
			}
		}
	}
	
	private boolean isValid(String message) {
		try {
			switch (type) {
				case INTEGER:
					{
						int val = Integer.parseInt(message);
						return val > 0;
					}
					
				case LONG:
					{
						long val = Long.parseLong(message);
						return val > 0;
					}
					
				case DOUBLE:
					{
						double val = Double.parseDouble(message);
						return val > 0;
					}
					
				case FLOAT:
					{
						float val = Float.parseFloat(message);
						return val > 0;
					}
					
				default: break;
			}
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private void finish(Object obj) {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin(CashShop.PLUGIN_NAME), () -> {
			caller.changerVar(var_name, obj);
		});
	}
	
	public enum Primitives {
		STRING,
		INTEGER,
		LONG,
		DOUBLE,
		FLOAT;
	}

}
