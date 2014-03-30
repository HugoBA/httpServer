package road;

import java.util.ArrayList;

import vector2D.Position;

/**
 * Class for Road check methods
 * @author Quentin
 *
 */
public abstract class RoadCheckPolygon
{
	/**
	 * Check if a detected polygon (ArrayList<Position>) is correct according to roads
	 * @param detectedPolygon ArrayList<Position> corresponding to the detected Polygon
	 * @param roads list of roads
	 * @return true if it is correct
	 */
	public boolean doesDetectedPolygonMatchesWithRoads(ArrayList<Position> detectedPolygon, ArrayList<Road> roads)
	{
		for(int j = 0; j < roads.size(); j++)
		{
			for(int i = 0; i < detectedPolygon.size(); i++)
			{
				if(i == detectedPolygon.size()-1)
					return roads.get(j).isOnRoad(detectedPolygon.get(i)) && roads.get(j).isOnRoad(detectedPolygon.get(0));
				if (roads.get(j).isOnRoad(detectedPolygon.get(i)) && roads.get(j).isOnRoad(detectedPolygon.get(i+1)))
					break;
			}
		}
		return false;
	}

	/**
	 * Clear the list from bad detected polygon which don't match with roads
	 * @return the cleared list
	 */
	public ArrayList<ArrayList<Position>> getClearedListFromBadPolygonRoad(ArrayList<ArrayList<Position>> detectedPolygons, ArrayList<Road> roads)
	{
		ArrayList<ArrayList<Position>> result = new ArrayList<ArrayList<Position>>();
		for(int i = 0; i < detectedPolygons.size(); i++)
		{
			ArrayList<Position> current = detectedPolygons.get(i);
			if (doesDetectedPolygonMatchesWithRoads(current, roads))
				result.add(current);
		}
		return result;
	}
}