package road;

import java.util.ArrayList;
import vector2D.Position;

/**
 * A road is characterized by an ID and a list of Position
 * @author Quentin
 *
 */
public class Road
{
	/**
	 * ID of the road
	 */
	private int id;
	
	/**
	 * List of the road nodes
	 */
	private ArrayList<Position> roadNodes;
	
	public Road(int id, ArrayList<Position> pointList)
	{
		this.id = id;
		this.roadNodes = pointList;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return the roadNodes
	 */
	public ArrayList<Position> getRoadNodes()
	{
		return roadNodes;
	}
	
	/**
	 * Two roads are considered as equals if their Position list are equals
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o == this)
			return true;
		else
		{
			Road temp = (Road) o;
			return (this.roadNodes.equals(temp.getRoadNodes()));
		}
	}
	
	public int hashCode()
	{
		return this.roadNodes.hashCode();
	}
	
	public String toString()
	{
		return this.id+this.roadNodes.toString();
	}
	
	/**
	 * @param node the position to search
	 * @return true if the position is on the road
	 */
	public boolean isOnRoad(Position node)
	{
		for(int i = 0; i < this.roadNodes.size(); i++)
		{
			if (node.equals(this.roadNodes.get(i)))
				return true;
		}
		return false;
	}
}
