package net.mywk.RockNLift;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LiftManager {
	private static RockNLift plugin;
	  //public HashSet<Lift> lifts = new HashSet(); REMOVED

	  public LiftManager(RockNLift plugin)
	  {
	    this.plugin = plugin;
	  }

	  public Lift newLift(Block block) {

		// Lift can be made of anything except AIR
		  if (block.getType() == Material.AIR)
			  return null;
		  
		// SECTION REMOVED
		// If present in the HashSet just return it instead of creating it all over again
		//Lift l = plugin.liftManager.liftSearch(block.getLocation());
		//if(l!=null){System.out.println("EXISTS"); return l;}
		  
	    HashSet<Block> baseBlocks = new HashSet(); 
	    HashSet<Floor> floors = new HashSet();
	  
	    Material bType = block.getWorld().getBlockAt(block.getX(), block.getY()-2, block.getZ()).getType();
        scanBaseBlocks(block.getWorld().getBlockAt(block.getX(), block.getY()-2, block.getZ()), bType, baseBlocks);

		Lift lift = new Lift(baseBlocks, bType, floors, block);
	    createFloors(lift);

	    return lift;
	    
	  }
	  
	  public void scanBaseBlocks(Block block,Material bType, HashSet<Block> baseBlocks)
	  {

	    if (baseBlocks.size() >= plugin.maxArea)return;
		if (baseBlocks.contains(block)) return;
	    
		// Debug
		// System.out.println("Scan: TypeId " + block.getTypeId() + " - " +baseBlocks.size()+" of "+plugin.maxArea);

		baseBlocks.add(block);

	    if (block.getRelative(BlockFace.NORTH, 1).getType() == bType)
	      scanBaseBlocks(block.getRelative(BlockFace.NORTH), bType,baseBlocks);
	    if (block.getRelative(BlockFace.EAST, 1).getType() == bType)
	      scanBaseBlocks(block.getRelative(BlockFace.EAST), bType,baseBlocks);
	    if (block.getRelative(BlockFace.SOUTH, 1).getType() == bType)
	      scanBaseBlocks(block.getRelative(BlockFace.SOUTH), bType,baseBlocks);
	    if (block.getRelative(BlockFace.WEST, 1).getType() == bType)
	      scanBaseBlocks(block.getRelative(BlockFace.WEST), bType,baseBlocks);
	  }

	  public void createFloors(Lift lift)
	  {
	    String message = "";
	    int maxY = plugin.maxHeight;
	    int floorNumber = 1;

	    // Used to re-write all [Floor] signs after complete creation
	    HashSet<Sign> floorSigns = new HashSet();
	    TreeMap<Integer,Sign> destinationSigns = new TreeMap();
	    
	    World currentWorld = lift.getLiftBlock().getWorld();

	      int yAt = 1;
	      while (true)
	      {

	        if (yAt >= plugin.maxHeight || yAt >= 256) break;

		    for (Block b : lift.baseBlocks) {
		      int x = b.getX();
		      int z = b.getZ();
	
		        Block blockAt = b.getWorld().getBlockAt(x, yAt, z);
		        if (plugin.isValidLift(blockAt)) {
		        	Sign destinationSign = (Sign)blockAt.getState();
		        	destinationSigns.put(floorNumber,destinationSign);
		        	blockAt = b.getWorld().getBlockAt(x, yAt+1, z);
			          Sign floorSign = (Sign)blockAt.getState();
			          floorSigns.add(floorSign);
			          
			          Floor floor; 
			          if(floorSign.getLine(0).toLowerCase().equals("[floor]"))
			          {
			        	  String text1 = " ";
			        	  String text2 = " ";
			        	  if(floorSign.getLine(1) != null && floorSign.getLine(1).length()>0)
			        		  text1 = floorSign.getLine(1);
			        	  if(floorSign.getLine(2) != null && floorSign.getLine(2).length()>0)
			        		  text2 = floorSign.getLine(2);
				          floor = new Floor(floorNumber, ((int)blockAt.getLocation().getY())-2, text1 + "\n" + text2);
			        	  floorSign.setLine(0,ChatColor.WHITE + "["+ChatColor.GOLD+"Floor"+ChatColor.WHITE+"]");
			        	  
				          HashSet<String> allowedColors = new HashSet<String>(java.util.Arrays.asList(
				              new String[] {"BLACK","DARK_BLUE","DARK_GREEN","DARK_AQUA","DARK_RED","DARK_PURPLE","GRAY","DARK_GRAY","BLUE","GREEN","AQUA","RED","YELLOW","LIGHT_PURPLE","WHITE"}));
				          if(allowedColors.contains(floorSign.getLine(3).toUpperCase()))
				          {
				        	  ChatColor c = ChatColor.valueOf(floorSign.getLine(3).toUpperCase());
				        	  floorSign.setLine(3, c + floorSign.getLine(2));
				        	  floorSign.setLine(2, c + floorSign.getLine(1));
				          }
				          else
				          {
				        	  floorSign.setLine(3, floorSign.getLine(2));
				        	  floorSign.setLine(2, floorSign.getLine(1));
				          }
			          }
			          else
			          {
				          floor = new Floor(floorNumber, ((int)blockAt.getLocation().getY())-2, floorSign.getLine(2) + "\n" + floorSign.getLine(3));
			          }
			      
			          
			      // Check if floor at same Y already exists
		          // Check if floorNumber already exists before increasing
			      boolean exists = false;
	        	  for(Floor f : lift.floors)
	        	  {
	        		  if(f.getFloorY() == (((int)blockAt.getLocation().getY())-2))
	        		  {exists = true; break;}
	        	  }
	        	  if(!exists)
	        	  {
	        		  floorNumber++;
	        		  floorSigns.add(floorSign);
	        		  lift.floors.add(floor);
	        	  }
	        	  
	        	  // Debug
	        	  //System.out.println("NEW FLOOR:" +blockAt.getType()+"-"+blockAt.getType()+"-"+ ((blockAt.getLocation().getY())-2) + ""+floorNumber+ floorSign.getLine(1) + "\n" + floorSign.getLine(2));
			       
		        }
		        
		      }
	      
        yAt++; 
	      
	    }
	    
	      for( Sign floorSign : floorSigns)
	      {
	    	  floorSign.setLine(1, ChatColor.WHITE + "" + lift.getFloorByY((int)(floorSign.getLocation().getY())-2).getFloorN() + " of " + lift.getTotalFloors());
	    	  floorSign.update();
	      }
	      
	      if(lift.getTotalFloors() > 1)
	      {
		      for( Entry<Integer,Sign> destinationSign : destinationSigns.entrySet())
		      {   	  
		    	  String[] s;
		    	  
		    	  // If the sign is already valid no need to reset it
		          int destinationFloor = -1;	
		          try{ destinationFloor = Integer.parseInt(((Sign)destinationSign.getValue()).getLine(0).split(" ")[2]);} catch (Exception e){  }
		        	
		          if(destinationFloor != -1 && destinationFloor <= lift.getTotalFloors())
		        	  s = lift.getFloorByN(destinationFloor).getName().split("\n");
		          else if(destinationSign.getKey() == 1)
		    	  {
		        	  destinationFloor = 2;
		    		  s = lift.getFloorByN(2).getName().split("\n");
		    	  }
	    		  else
	    		  {
	    			  destinationFloor = 1;
	    			  s = lift.getFloorByN(1).getName().split("\n");
	    		  }
		    	  
	    		  destinationSign.getValue().setLine(0, ChatColor.GOLD + "Going To" + ChatColor.WHITE + " " + destinationFloor);
		    	 
		    	  if(s.length > 0)
		    	  {
		    		  destinationSign.getValue().setLine(1, s[0]);
		    		  destinationSign.getValue().setLine(2, s[1]);
		    	  }
		    	  else
		    	  {
		    		  destinationSign.getValue().setLine(1, "");
		    		  destinationSign.getValue().setLine(2, "");
		    	  }
		    	 
		    	 destinationSign.getValue().setLine(3, ChatColor.WHITE + "["+ChatColor.GOLD+"Go"+ChatColor.WHITE+"]");
		    	  
		    	 destinationSign.getValue().update();
		      }
	      }
	    
	  }

	  // Using destination as Int to prevent malfunction if the players
	  // sets several floors with the same name
	  public void movePlayer(Lift lift, Block floorBlock, Player player, int destination)
	  {
		  //for(Lift l : lifts) -- REMOVED
		  {

	        	for(Block b : lift.baseBlocks)
	        	{
	        		if(b.getX() == floorBlock.getX() && b.getZ() == floorBlock.getZ())
	        		{
	        			Location loc = player.getLocation();
	        			loc.setY(lift.getFloorByN(destination).getFloorY());
	        			player.teleport(loc);
			        	player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 1));
	        			return;
	        		}
	        	}
	        }
          // Else no floor was found
	      
	  }
	  
	  /*public Lift liftSearch(Location loc) -- REMOVED
	  {

		  for(Lift l : lifts){
			  for(Block b : l.baseBlocks){ // Same as .contains
				  
				  if(b.getWorld() != loc.getWorld())
					  break;
				  
				  if(b.getX() == loc.getX() && b.getZ() == loc.getZ()) // Ignore Y
					  return l;
			  }
			}

		    
		  return null;
	  }*/

}
