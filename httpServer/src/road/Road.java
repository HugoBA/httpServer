package road;

import java.util.ArrayList;

import vector2D.Position;
import vector2D.RoadNodePosition;

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

	public Road(int id, ArrayList<Position> nodeList)
	{
		this.id = id;
		this.roadNodes = nodeList;
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
		return "Road :\nID : "+this.id+"\nPoints :\n"+this.roadNodes.toString();
	}

	/**
	 * @param position the position to search
	 * @return true if the position is on the road
	 */
	public boolean isOnRoad(Position position)
	{
		for(int i = 0; i < this.roadNodes.size(); i++)
		{
			if (position.equals(this.roadNodes.get(i).equals(position)))
				return true;
		}
		return false;
	}
}