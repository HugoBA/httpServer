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
	private static boolean doesDetectedPolygonMatchesWithRoads(ArrayList<Position> detectedPolygon, ArrayList<Road> roads)
	{
		ArrayList<Road>[] roadListByNode = new ArrayList[detectedPolygon.size()];
		for (int i = 0; i < detectedPolygon.size(); i++)
			roadListByNode[i] = new ArrayList<Road>();

		for (int i = 0; i < detectedPolygon.size(); i++)
		{
			for (int j = 0; j < roads.size(); j++)
			{
				Road currentRoad = roads.get(j);
				if (currentRoad.isOnRoad(detectedPolygon.get(i)))
					roadListByNode[i].add(currentRoad);
			}
		}
		
		for (int i = 0; i < detectedPolygon.size(); i++)
		{
			if (i == detectedPolygon.size()-1)
				// Traitement du dernier point
				return isThereACommonRoad(roadListByNode[i], roadListByNode[0]);
			else
			{
				// On abandonne si le point courrant et le suivant n'ont pas de routes en commun
				if (!(isThereACommonRoad(roadListByNode[i], roadListByNode[i+1])))
					return false;
			}
		} 
		return false;
	}
	
	/**
	 * Check if the two given road list has a common road
	 * @param roadList1 the first list
	 * @param roadList2 the second list
	 * @return 
	 */
	private static boolean isThereACommonRoad(ArrayList<Road> roadList1, ArrayList<Road> roadList2)
	{
		for (int i = 0; i < roadList1.size(); i++)
		{
			for (int j = 0; j < roadList2.size(); j++)
			{
				if (roadList1.get(i).equals(roadList2.get(j)))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Clear the list from bad detected polygon which don't match with roads
	 * @param detectedPolygons the list containing the detected Polygons
	 * @param roads list of roads
	 * @return the cleared list
	 */
	public static ArrayList<ArrayList<Position>> getClearedListFromBadPolygonRoad(ArrayList<ArrayList<Position>> detectedPolygons, ArrayList<Road> roads)
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
