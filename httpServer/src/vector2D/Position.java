package vector2D;

/**
* Class representing a position on a map
* Characterized by X and Y coordinates
* @author Quentin
*
*/
public class Position
{
/**
* X coordinate
*/
protected double x;

/**
* Y coordinate
*/
protected double y;

/**
* Constructor based on given X and Y coordinates
* @param inX given X
* @param inY given Y
*/
public Position(double inX, double inY)
{
this.x = inX;
this.y = inY;
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
* HashCode
* All different position have a different hashCode
*/
public int hashCode()
{
return (int) (this.x-this.y);
}
/**
* Equals method
* Two position are equals if their X and Y are equals
*/
public boolean equals(Object o)
{
if (o == null)
return false;
if (o == this)
return true;
else
{
Position temp = (Position) o;
return ((this.getX() == temp.getX()) && (this.getY() == temp.getY()));
}
}

/**
* ToString method
* Return the coordinates of the point as : ( X ; Y )
*/
public String toString()
{
return "( "+this.x+" ; "+this.y+" )";
}
}