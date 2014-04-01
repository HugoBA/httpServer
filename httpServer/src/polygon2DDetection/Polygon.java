package polygon2DDetection;

import java.util.ArrayList;

import vector2D.Position;
import vector2D.Vector;

/**
 * Class which represent a Polygon
 * A Polygon is characterized by its list of vector
 * By applying the Vector V1 on the position P1, it result the position P2
 * V2 on P2 -> P3
 * V3 on P3 -> P4
 * ...
 * Vn-1 on Pn-1 -> Pn
 * Vn on Pn -> P1
 * @author Quentin
 *
 */
public class Polygon 
{
	/**
	 * The name of the polygon
	 */
	private String name;
	
	/**
	 * The list of Vectors which represents the polygon
	 */
	private ArrayList<Vector> vectorList;
	
	/**
	 * Constructor based on given name and given vector list
	 * @param inName given name
	 * @param inVectorList given vector list
	 */
	public Polygon(String inName, ArrayList<Vector> inVectorList) throws NotPolygonException
	{
		Position a;
		a = new Position(0,0);
		for (int i = 0; i < inVectorList.size(); i++)
			a = inVectorList.get(i).application(a);
		if (!a.equals(new Position(0,0)))
			throw new NotPolygonException();
		this.name = inName;
		this.vectorList = inVectorList;
	}
	
	/**
	 * Constructor based on an ArrayList of Positions
	 * Create the vector from the list
	 * @param positionList list of position which form the Polygon
	 * @param name the name of the Polygon
	 */
	public Polygon(ArrayList<Position> positionList, String name) throws NotPolygonException
	{
		ArrayList<Vector> vectorList = new ArrayList<Vector>();
		for (int i = 0; i < positionList.size(); i++)
		{
			if (i == positionList.size()-1)
				vectorList.add(new Vector(positionList.get(i),positionList.get(0)));
			else
				vectorList.add(new Vector(positionList.get(i), positionList.get(i+1)));
		}
		
		this.name = name;
		this.vectorList = vectorList;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @return the list of vectors
	 */
	public ArrayList<Vector> getVectorList()
	{
		return this.vectorList;
	}
	
	/**
	 * Return the name and the list.toString()
	 */
	public String toString()
	{
		return this.name+" : "+this.vectorList.toString();
	}
	
	/**
	 * Two polygon are considered as equals if their vectors are the same
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o == this)
			return true;
		else
		{
			Polygon temp = (Polygon) o;
			if (this.vectorList.size() != temp.getVectorList().size())
				return false;
			else
			{
				for(int i = 0; i < this.vectorList.size(); i++)
				{
					int j = 0;
					while(j < temp.getVectorList().size() 
							&& temp.getVectorList().get(j) != this.vectorList.get(i))
						j++;
					if (j == temp.getVectorList().size())
						return false;
				}
				return true;
			}
		}
	}
}
