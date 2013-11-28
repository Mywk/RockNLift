package net.mywk.RockNLift.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.mywk.RockNLift.Floor;
import net.mywk.RockNLift.Lift;
import net.mywk.RockNLift.RockNLift;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener{
	
	  private RockNLift plugin;

	  public PlayerListener(RockNLift plugin) {
	    Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	    this.plugin = plugin;
	  }

	  @EventHandler(ignoreCancelled=true)
	  public void onPlayerInteract(PlayerInteractEvent event) {
	    if (event.getClickedBlock().getType() == Material.WALL_SIGN &&((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_BLOCK)))
	    {
	      Block clickedBlock = event.getClickedBlock();

	      if (plugin.isValidLift(clickedBlock))
	      {
	    	
	        final Lift lift = plugin.liftManager.newLift(clickedBlock);

	        // Not a valid lift, just in case anything goes wrong
	        if (lift == null)
	          return;

	        if (lift.getTotalFloors() <= 1)
	         return; // no floors
       
	        
	        Sign signFloor = (Sign)clickedBlock.getRelative(BlockFace.UP).getState();
	        Sign signDestination = (Sign)clickedBlock.getState();
	        
	        // Not a good idea, ignore this
	        //float test = (float)Math.toDegrees(Math.atan2(event.getPlayer().getLocation().getBlockX() - clickedBlock.getX(), clickedBlock.getZ() - event.getPlayer().getLocation().getBlockZ()));

	        // Right click changes destination floor
	        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
	        {
	        	try{ // Just in case
	        		
		        	String[] c = signFloor.getLine(1).split(ChatColor.WHITE+"")[1].split(" of ");
		        	int currentFloor = Integer.parseInt(c[0]);	
		        	
		        	// Debug
		        	//System.out.println("currentFloor " + currentFloor);
		        	
		        	String[] d = signDestination.getLine(0).split(" ");
		        	int destinationFloor = Integer.parseInt(d[2]);	
		        	destinationFloor++;
		        	
		        	if(destinationFloor > lift.getTotalFloors())
		        		destinationFloor = 1;
		        	if(destinationFloor == currentFloor)
		        		destinationFloor++;
	        		// Temporary
		        	if(destinationFloor > lift.getTotalFloors())
		        		destinationFloor = 1;
		        	
		        	//System.out.println("destinationFloor " + destinationFloor);

		        	signDestination.setLine(0, ChatColor.GOLD + "Going To" + ChatColor.WHITE + " " + destinationFloor);
		        	String[] s = lift.getFloorByN(destinationFloor).getName().split("\n");
		        	if(s.length > 0)
		        	{
			        	signDestination.setLine(1, s[0]);
			        	signDestination.setLine(2, s[1]);
		        	}
		        	signDestination.update();
		        	
		        	//plugin.liftManager.lifts.add(lift); -- REMOVED
		        	event.setCancelled(true);
		        	
	        	} catch (Exception e){  }
	        	
	        }
	        // Left click moves the player to destination floor
	        else if(event.getAction() == Action.LEFT_CLICK_BLOCK && !event.getPlayer().isSneaking())
	        {
	        	try { // Just in case
		        	String[] d = signDestination.getLine(0).split(" ");
		        	int destinationFloor = Integer.parseInt(d[2]);	
		        	
		        	plugin.liftManager.movePlayer(lift, event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN), event.getPlayer(), destinationFloor);
		        	event.setCancelled(true);
	        	} catch (Exception e){  }
        	}
	        
	        // For delayed trigger we use queweLiftRemoval -- DISABLED    
	        /*System.out.println("-" + System.currentTimeMillis());
	        RockNLift.queweLiftRemoval.put(lift.getLiftBlock().getLocation(), System.currentTimeMillis());
	        plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(plugin.getInstance(), new Runnable() {
                
				public void run() {
                	
					for(Entry<Location,Long> q : RockNLift.queweLiftRemoval.entrySet())
					{
						Lift l = plugin.liftManager.liftSearch(q.getKey());
						if (System.currentTimeMillis() > q.getValue()+3500) // Lifts remain active for 3.5 seconds
						{
							System.out.println("--" + q.getValue());
							plugin.liftManager.lifts.remove(lift);
							RockNLift.queweLiftRemoval.remove(q);
						}
					}
					
                }
              }, 80L);*/
	      }
	    }
	  }
	 

}
