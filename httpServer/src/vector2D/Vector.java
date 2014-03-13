package vector2D;

/**
 * Class representing a 2D vector
 * A vector is characterized by both of his X and Y coordinates
 * @author Quentin
 */
public class Vector 
{
	/**
	 * X vector coordinate
	 */
	private double x;
	
	/**
	 * Y vector coordinate
	 */
	private double y;
	
	/**
	 * Constructor based on given X and Y coordinates
	 * @param inX given X
	 * @param inY given Y
	 */
	public Vector(double inX, double inY)
	{
		this.x = inX;
		this.y = inY;
	}
	
	/**
	 * COntructor based on two given points
	 * @param A the first point
	 * @param B the second point
	 */
	public Vector(Position A, Position B)
	{
		this.x = B.getX() - A.getX();
		this.y = B.getY() - A.getY();
	}

	/**
	 * X getter
	 * @return X
	 */
	public double getX() 
	{
		return this.x;
	}

	/**
	 * Y getter
	 * @return Y
	 */
	public double getY() 
	{
		return this.y;
	}
	
	/**
	 * Application of the vector on a given position
	 * @param startPosition the position to apply the vector
	 * @return the resulting position
	 */
	public Position application(Position startPosition)
	{
		return new Position(startPosition.getX()+this.x,startPosition.getY()+this.y);
	}
	
	/**
	 * Addition of this vector with a given vector
	 * @param addingVector the second vector
	 * @return the resulting vector
	 */
	public Vector add(Vector addingVector)
	{
		return new Vector(this.x+addingVector.getX(), this.y+addingVector.getY());
	}
	
	/**
	 * Method to get the length of the vector
	 * @return the length of the vector
	 */
	public double norm()
	{
		return java.lang.Math.sqrt( this.x*this.x + this.y*this.y);
	}
	
	public int hashCode()

	{
		return (int) (this.x-this.y);
	}
	/**
	 * Two vectors are equals if their X and Y are equals
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o == this)
			return true;
		else
		{
			Vector temp = (Vector) o;
			return ((this.getX() == temp.getX()) && (this.getY() == temp.getY()));
		}
	}
	
	/**
	 * Return the coordinates of the vector as : ->( X ; Y )<-
	 */
	public String toString()
	{
		return "->( "+this.x+" ; "+this.y+" )<-";
	}
	
	/**
	 * Vector multiplication by a real
	 * @param k coefficient
	 * @return the resulting vector
	 */
	public Vector multiplied(double k)
	{
		return new Vector(this.x*k, this.y*k);
	}
}
