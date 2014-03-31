package road;

import vector2D.Position;
/**
 * Represents a Position on a Road
 * Use to create Roads from JSON
 * @author Quentin
 *
 */
public class RoadNodePosition extends Position 
{
	/**
	 * ID of the position
	 */
	private double id;
	
	public RoadNodePosition (double id, double x, double y)
	{
		super(x, y);
		this.id = id;
	}
	
	/**
	 * @return the corresponding position, with the same coordinates
	 */
	public Position toPosition()
	{
		return new Position(this.x, this.y);
	}

	/**
	 * @return the ID
	 */
	public double getId() 
	{
		return id;
	}
}
