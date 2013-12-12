package net.mywk.RockNLift;

// Add settings for SpeedBoost after Elevator use?
// Add settings for afterElevatorUse animation and time

// Only creator can update elevator? Or maybe townypermissions?

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.mywk.RockNLift.listeners.PlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class RockNLift extends JavaPlugin implements Listener
{

  public static boolean redstone = true;
  
  public static RockNLift rockNLift; 
  public static LiftManager liftManager; 
  
  //public static Map<Location,Long> queweLiftRemoval = new HashMap<Location,Long>();
  
  // This will only allow players within this area to use the elevator
  public static int maxArea = 16;
  public static int maxHeight = 256;

  public static RockNLift getInstance() {
      return rockNLift;
}

  public void onEnable() {
    
    new PlayerListener(this);
    liftManager = new LiftManager(this);
    rockNLift = this;

    /*getConfig().options().copyDefaults(true);
    this.maxArea = getConfig().getInt("maxArea",16);
    this.maxHeight = getConfig().getInt("maxHeight",256);

    saveConfig();*/

    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    
    

    System.out.println("RockNLift v"+this.getDescription().getVersion()+" enabled!");
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	  
    if ((cmd.getName().equalsIgnoreCase("rocknlift"))) {
    	
    	if(args.length == 0)
    	{
	      long time = System.currentTimeMillis();
	      Player player = (Player)sender;
	      sender.sendMessage(ChatColor.AQUA + "RockNLift v" + this.getDescription().getVersion() + " by Mywk");
	      sender.sendMessage(ChatColor.AQUA + "HowTo: " + ChatColor.WHITE +"Right click the bottom sign to change ");
	      sender.sendMessage(ChatColor.WHITE +"         destination floor, left click to go.");
	      sender.sendMessage(ChatColor.AQUA + "Commands:" + ChatColor.WHITE +" /lift help" + ChatColor.AQUA +" , " + ChatColor.WHITE +" /lift advanced");
	      return true;
    	}
    	else if (args[0].equalsIgnoreCase("help"))
    	{
    	      Player player = (Player)sender;
    	      sender.sendMessage(ChatColor.AQUA + "----------------------------------------");
    	      sender.sendMessage(ChatColor.AQUA + "[Sign]" + ChatColor.WHITE +"   Write [Floor] on the first line.");
    	      sender.sendMessage(ChatColor.AQUA + "[Sign]" + ChatColor.WHITE +"   Empty, right click when it's ready to use.");
    	      sender.sendMessage(ChatColor.AQUA + "[Air] " + ChatColor.WHITE +"   Don't place any block here.");
    	      sender.sendMessage(ChatColor.AQUA + "[Base]" + ChatColor.WHITE +"  May be any Solid block, max floor");
    	      sender.sendMessage(ChatColor.WHITE + "          size is 16 (E.g.. 4x4 Sandstone)");
    	      sender.sendMessage(ChatColor.AQUA + "Video Tutorial:" + ChatColor.WHITE +" http://youtu.be/c6GTslmFo4M");
    	      sender.sendMessage(ChatColor.AQUA + "----------------------------------------");
    	}
    	else if (args[0].equalsIgnoreCase("advanced"))
    	{
    	      Player player = (Player)sender;
    	      sender.sendMessage(ChatColor.AQUA + "----------------------------------------");
    	      sender.sendMessage(ChatColor.WHITE +"Optional settings for [Floor] sign:");
    	      sender.sendMessage(ChatColor.WHITE +"Second and Third line are the Floor Name");
    	      sender.sendMessage(ChatColor.WHITE +"Fourth line is the Color of the Floor");
    	      sender.sendMessage(ChatColor.WHITE +"Available colors: BLACK, DARK_BLUE, GRAY");
    	      sender.sendMessage(ChatColor.WHITE +"DARK_GREEN, DARK_RED, RED, DARK_GRAY,");
    	      sender.sendMessage(ChatColor.WHITE +"DARK_PURPLE, DARK_AQUA, AQUA, BLUE");
    	      sender.sendMessage(ChatColor.WHITE +"GREEN, YELLOW, LIGHT_PURPLE, WHITE");
    	      sender.sendMessage(ChatColor.AQUA + "----------------------------------------");
    	}
    }

    return false;
  }

  public void onDisable() {
    //this.liftManager.lifts.clear(); REMOVED
    System.out.println(this + " disabled!");
  }


  
  public boolean isValidLift(Block clickedBlock)
  {
	  Block signBlock = clickedBlock.getRelative(BlockFace.UP);
	  if ((signBlock != null) && (signBlock.getType() == Material.WALL_SIGN) && (clickedBlock.getType() == Material.WALL_SIGN))
	  {
		  if(((Sign)(signBlock.getState())).getLine(0).toLowerCase().equals("[floor]")||((Sign)(signBlock.getState())).getLine(0).contains(ChatColor.WHITE + "["+ChatColor.GOLD+"Floor"+ChatColor.WHITE+"]"))
		  {
			  // The block directly below MUST be AIR and the one after MUST be solid
			  if(clickedBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && clickedBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().isSolid())
				  return true;
		  }
	  }
	  
	return false;
  }
}