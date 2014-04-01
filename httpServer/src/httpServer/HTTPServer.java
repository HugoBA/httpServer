package httpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import polygon2DDetection.NotPolygonException;
import polygon2DDetection.Polygon;
import polygon2DDetection.PolygonDetection;
import polygonDB.PatternFromDB;
import road.Road;
import road.RoadCheckPolygon;
import road.RoadNodePosition;
import vector2D.Position;
import vector2D.Vector;

/**
 * Is used as a HTTP Server for PolygonDetection requests from the Android application
 * @author  Hugo && Quentin
 *
 */
public class HTTPServer extends Thread
{
	/**
	 * A client connection
	 */
	Socket connectedClient = null;
	
	/**
	 * Request Buffer for HTTP client's requests
	 */
	BufferedReader inFromClient = null;
	
	/**
	 * Stream to answer to the client
	 */
	OutputStreamWriter outToClient = null;
	
	/**
	 * Approximation radius
	 */
	public final static double APROXIMATION = 0.01;

	public HTTPServer(Socket client) 
	{
		connectedClient = client;
	}
	
	public static void main (String args[])
	{
		System.out.println("HTTPServer.java");
		ServerSocket Server = null;
		try
		{
			Server = new ServerSocket (5000, 10, InetAddress.getByName("192.168.43.247"));
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown host");
		}
		catch (IOException e)
		{
			System.out.println("Socket cration failed");
		}
		System.out.println ("TCPServer Waiting for client on port 5000");

		while(true) 
		{
			Socket connected = null;
			try
			{
				connected = Server.accept();
			}
			catch (IOException e)
			{
				System.out.println("Connexion failed");
			}
			(new HTTPServer(connected)).start();
		}
	}
	
	public void run()
	{
		try 
		{
			System.out.println( "The Client "+
					connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");
			inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));
			outToClient = new OutputStreamWriter(connectedClient.getOutputStream());

			String requestString = inFromClient.readLine();
			String headerLine = requestString;

			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			String httpMethod = tokenizer.nextToken();
			String httpQueryString = tokenizer.nextToken();

			if (httpMethod.equals("GET")) 
			{
				
				httpQueryString = httpQueryString.replace("/?", "");

				String[] parameters = httpQueryString.split("&");

				Hashtable<String, String> finaltab= new Hashtable<String, String>();
				String[] parameterSplitted;
				
				for(int i=0; i<parameters.length; i++)
				{
					parameterSplitted = parameters[i].split("=");
					finaltab.put(parameterSplitted[0], parameterSplitted[1]);
				}
				JSONParser parser = new JSONParser();

				System.out.println("Latitude : "+finaltab.get("latitude"));
				System.out.println("Latitude : "+finaltab.get("longitude"));
				
				InputStream inputStream = new URL("http://overpass-api.de/api/interpreter?data=%3Cosm-script%20output%3D%22json%22%3E%0A%20%20%3Cquery%20type%3D%22way%22%3E%0A%20%20%20%20%3"
						+ "Caround%20lat%3D%22"+finaltab.get("latitude")+"%22%20lon%3D%22"+finaltab.get("longitude")+"%22%20radius%3D%22"+finaltab.get("radius")+"%22%2F%3E%0A"
						+ "%20%20%20%20%3Chas-kv%20k%3D%22highway%22%20modv%3D%22%22%20v%3D%22%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3D%22highway%22%20modv%3D%22not%22%20"
						+ "v%3D%22crossing%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3D%22highway%22%20modv%3D%22not%22%20v%3D%22footway%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3"
						+ "D%22junction%22%20modv%3D%22not%22%20v%3D%22roundabout%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3D%22junction%22%20modv%3D%22not%22%20v%3D%22traffi"
						+ "c_signals%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3D%22maxspeed%22%20modv%3D%22not%22%20v%3D%22110%22%2F%3E%0A%20%20%20%20%3Chas-kv%20k%3D%22maxspe"
						+ "ed%22%20modv%3D%22not%22%20v%3D%22130%22%2F%3E%0A%20%20%3C%2Fquery%3E%0A%20%20%3Cunion%3E%0A%20%20%20%20%3Citem%2F%3E%0A%20%20%20%20%3C"
						+ "recurse%20type%3D%22down%22%2F%3E%0A%20%20%3C%2Funion%3E%0A%20%20%3Cprint%2F%3E%0A%3C%2Fosm-script%3E").openStream();
				Reader reader = new InputStreamReader(inputStream);
				
				JSONObject jsonObject = (JSONObject) parser.parse(reader);
				JSONArray jsonArray = (JSONArray) jsonObject.get("elements");
				
				Iterator<JSONObject> iterator = jsonArray.iterator();
				// Liste de RoadNodePosition, pour créer les routes
				List<RoadNodePosition> nodeRoadList = new ArrayList<RoadNodePosition>();
				//Liste de Position, représentant tout les noeuds
				ArrayList<Position> nodeList = new ArrayList<Position>();
				//Liste des routes
				ArrayList<Road> roads = new ArrayList<Road>();
				
				while(iterator.hasNext())
				{
					JSONObject currentElement = iterator.next();
					//Si c'est un noeud
					if (currentElement.get("type").equals("node"))
					{
						//On créé le RoadNodePosition correspondant, qu'on ajoute à la liste
						nodeRoadList.add(new RoadNodePosition(
								Double.parseDouble(currentElement.get("id").toString()),
								Double.parseDouble(currentElement.get("lat").toString()),
								Double.parseDouble(currentElement.get("lon").toString()
										)));
					}
					//Si c'est une route
					else if (currentElement.get("type").equals("way"))
					{
						//Liste temporaire pour les noeuds de la route
						ArrayList<Position> roadNodeList = new ArrayList<Position>();
						JSONArray jsonWayNodes = (JSONArray) currentElement.get("nodes");
						Iterator<Long> nodesIterator = jsonWayNodes.iterator();
						while (nodesIterator.hasNext()) 
						{
							Double currentNode = Double.parseDouble(nodesIterator.next().toString());
							for (int i=0; i < nodeRoadList.size(); i++)
							{
								if(nodeRoadList.get(i).getId() == currentNode)
								{
									roadNodeList.add(nodeRoadList.get(i).toPosition());
								}
							}
						}
						roads.add(new Road(Integer.parseInt(currentElement.get("id").toString()), roadNodeList));
					}
				}
				
				//A la fin, on convertit la liste de RoadNodePosition en liste de Position
				for (int i = 0; i < nodeRoadList.size(); i++)
					nodeList.add(nodeRoadList.get(i).toPosition());
				
				System.out.println("Node list :\n"+nodeList.toString());
				System.out.println("Road list :\n"+roads.toString());
				
				System.out.println("Positions successfully added");
				System.out.println("Starting seeking patterns");

				
				/*
				 * The house :
				 * 44.9146298, 4.9145186
				 * 44.9148567, 4.9146612
				 * 44.9149414, 4.9149929
				 * 44.9147757, 4.9154621
				 * 44.9145304, 4.9156112
				 * 44.9143414, 4.915506
				 */
				
				Position h1 = new Position(44.9146298, 4.9145186);
				Position h2 = new Position(44.9148567, 4.9146612);
				Position h3 = new Position(44.9149414, 4.9149929);
				Position h4 = new Position(44.9147757, 4.9154621);
				Position h5 = new Position(44.9145304, 4.9156112);
				Position h6 = new Position(44.9143414, 4.915506);
				ArrayList<Position> houseList = new ArrayList<Position>();
				houseList.add(h1);
				houseList.add(h2);
				houseList.add(h3);
				houseList.add(h4);
				houseList.add(h5);
				houseList.add(h6);
				
				/*Position t1 = new Position(0,0);
				Position t2 = new Position(0,3);
				Position t3 = new Position(3,1);*/
				
				Vector vh1 = new Vector(h1,h2);
				Vector vh2 = new Vector(h2,h3);
				Vector vh3 = new Vector(h3,h4);
				Vector vh4 = new Vector(h4,h5);
				Vector vh5 = new Vector(h5,h6);
				Vector vh6 = new Vector(h6,h1);
				/*Vector vt1 = new Vector(t1,t2);
				Vector vt2 = new Vector(t2,t3);
				Vector vt3 = new Vector(t3,t1);*/
				
				ArrayList<Vector> houseVectorList = new ArrayList<Vector>();
				houseVectorList.add(vh1);
				houseVectorList.add(vh2);
				houseVectorList.add(vh3);
				houseVectorList.add(vh4);
				houseVectorList.add(vh5);
				houseVectorList.add(vh6);
				/*ArrayList<Vector> triangleVectorList = new ArrayList<Vector>();
				triangleVectorList.add(vt1);
				triangleVectorList.add(vt2);
				triangleVectorList.add(vt3);
				
				
				Polygon house = null, positionHouse = null;
				Polygon triangle = null;
				
				house = new Polygon("The hard house", houseVectorList);
				positionHouse = new Polygon(houseList, "The hard position house");
				*/
				ArrayList<Polygon> polygonList = PatternFromDB.getPolygonList();
				//polygonList.add(house);
				//polygonList.add(positionHouse);
				System.out.println(polygonList.toString());
				
				JSONArray patternsList = new JSONArray();
				JSONObject pattern = null;
				JSONArray finalNodeList = null;
				JSONObject node=null;
				
				ArrayList<ArrayList<ArrayList<Position>>> detectedPolygons = new ArrayList<ArrayList<ArrayList<Position>>>();
				
				for(int i=0; i < polygonList.size(); i++)
				{
					detectedPolygons.add(PolygonDetection.isTherePolygon(nodeList, polygonList.get(i), APROXIMATION));
				}
				/*
				ArrayList<ArrayList<Position>> houseResults = PolygonDetection.isTherePolygon(nodeList, 
						house, APROXIMATION);
				ArrayList<ArrayList<Position>> triangleResult = PolygonDetection.isTherePolygon(nodeList, 
						triangle, APROXIMATION);
				System.out.println("List of detected \"house\" :\n"+houseResults.toString());
				System.out.println("List of detected \"triangle\" :\n"+triangleResult.toString());
				System.out.println("Clean results :");
				
				ArrayList<ArrayList<Position>> cleanHouseResults = 
						RoadCheckPolygon.getClearedListFromBadPolygonRoad(houseResults, roads);
				ArrayList<ArrayList<Position>> cleanTriangleResults = 
						RoadCheckPolygon.getClearedListFromBadPolygonRoad(triangleResult, roads);
						*/
				ArrayList<ArrayList<ArrayList<Position>>> cleanedPolygons = new ArrayList<ArrayList<ArrayList<Position>>>();
				
				for(int i=0; i < detectedPolygons.size(); i++)
				{
					cleanedPolygons.add(RoadCheckPolygon.getClearedListFromBadPolygonRoad(detectedPolygons.get(i), roads));
				}
				/*
				System.out.println("Cleaned \"house\" list :\n"+cleanHouseResults.toString());
				System.out.println("Cleaned \"triangle\" list :\n"+cleanTriangleResults.toString());
				*/

				System.out.println(cleanedPolygons.toString());
				
				for (int i0 = 0; i0 < cleanedPolygons.size(); i0++)
				{
					ArrayList<ArrayList<Position>> currentCleanedPolygonList = cleanedPolygons.get(i0);
					for(int i=0; i < currentCleanedPolygonList.size(); i++)
					{
						pattern=new JSONObject();
						finalNodeList = new JSONArray();
						for(int j1 = 0; j1 < currentCleanedPolygonList.get(i).size(); j1++)
						{
							node = new JSONObject();
							node.put("lat",currentCleanedPolygonList.get(i).get(j1).getX());
							node.put("lon",currentCleanedPolygonList.get(i).get(j1).getY());
							finalNodeList.add(node);
						}
						pattern.put("name", polygonList.get(i0).getName());
						pattern.put("nodes", finalNodeList);
						patternsList.add(pattern);
					}
				}
				
				sendResponse(200, patternsList.toJSONString());
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendResponse (int statusCode, String responseString) throws Exception 
	{

		String statusLine = "HTTP/1.1 200 OK" + "\r\n";
		String serverdetails = "Server: Java HTTPServer";
		String contentLengthLine = "Content-Length: " + responseString.length();
		String contentTypeLine = "Content-Type: text/html" + "\r\n";

		outToClient.write(statusLine);
		outToClient.write(serverdetails);
		outToClient.write(contentTypeLine);
		outToClient.write(contentLengthLine);
		outToClient.write("Connection: close\r\n");
		outToClient.write("\r\n");
		outToClient.write(responseString);
		outToClient.close();
	}
}
