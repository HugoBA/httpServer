package vector2D;

/**
 * Represents a Position and the corresponding position
 * @author Quentin
 *
 */
public class PositionProportion 
{
	/**
	 * The position
	 */
	private Position position;
	
	/**
	 * The corresponding proportion
	 */
	private double proportion;
	
	public PositionProportion(Position pos, double k)
	{
	 this.position= pos;
	 this.proportion= k;
	}
	
	/**
	 * @return position
	 */	
	public Position getPosition()
	{
		return this.position;
	}
	
	/**
	 * @return proportion
	 */
	public double getProportion()
	{
		return this.proportion;
	}
	
	/**
	 * ( Position.X ; Position.Y ) | Proportion
	 */
	public String toString()
	{
		return this.position.toString()+" | "+this.proportion;
	}
}
