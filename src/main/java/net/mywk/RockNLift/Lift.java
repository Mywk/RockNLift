package net.mywk.RockNLift;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Lift
{
  
	  private Block liftBlock;
	  public HashSet<Block> baseBlocks;
	  private World world;
	  private Material baseBlockType;
	  public HashSet<Floor> floors;

	  public Lift(HashSet<Block> baseBlocks, Material baseBlockType, HashSet<Floor> floors, Block liftBlock)
	  {
	    this.baseBlocks = baseBlocks;
	    this.baseBlockType = baseBlockType;
	    this.floors = floors;
	    this.liftBlock = liftBlock;
	    this.world = liftBlock.getWorld();
	  }

	  public Floor getFloorByN(int n) {
	    for(Floor f : floors)
	    {
	    	if(f.getFloorN() == n)
	    		return f;
	    }
	    return null;
	  }
	  
	  public Floor getFloorByY(int n) {
	    for(Floor f : floors)
	    {
	    	if(f.getFloorY() == n)
	    		return f;
	    }
	    return null;
	  }
	  
	  public Block getLiftBlock() {
		    return this.liftBlock;
		  }

	  public int getTotalFloors() {
	    return this.floors.size();
	  }
	
}
