package net.mywk.RockNLift;

import org.bukkit.Location;

public class Floor {

	  private String name = "";
	  private int floor;
	  private int floorY;

	  public Floor(int floor, int floorY, String name)
	  {
	    this.floor = floor;
	    this.name = name;
	    this.floorY = floorY;
	  }
	  
	  public String getName() { return this.name; }
	  
	  public int getFloorN() { return this.floor; }
	  public int getFloorY() { return this.floorY; }
	
}
