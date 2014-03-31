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
	public final static double APROXIMATION = 0.001;

	public HTTPServer(Socket client) 
	{
		connectedClient = client;
	}
	
	@SuppressWarnings("resource")
	public static void main (String args[])
	{
		System.out.println("HTTPServer.java");
		ServerSocket Server = null;
		try
		{
			Server = new ServerSocket (5000, 10, InetAddress.getByName("127.0.0.1"));
		}
		catch (UnknownHostException e)
		{
			System.out.println("Hôte inconnu");
		}
		catch (IOException e)
		{
			System.out.println("Erreur de création du ServerSocket");
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
				System.out.println("Erreur de connexion");
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
				
				System.out.println("Liste des noeuds :\n"+nodeList.toString());
				System.out.println("Liste des routes :\n"+roads.toString());
				
				System.out.println("Positions successfully added");
				Date x = new Date();
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
				
				Vector vh1 = new Vector(h1,h2);
				Vector vh2 = new Vector(h2,h3);
				Vector vh3 = new Vector(h3,h4);
				Vector vh4 = new Vector(h4,h5);
				Vector vh5 = new Vector(h5,h6);
				Vector vh6 = new Vector(h6,h1);
				
				ArrayList<Vector> houseVectorList = new ArrayList<Vector>();
				houseVectorList.add(vh1);
				houseVectorList.add(vh2);
				houseVectorList.add(vh3);
				houseVectorList.add(vh4);
				houseVectorList.add(vh5);
				houseVectorList.add(vh6);
				
				Polygon house = null;
				
				try
				{
					house = new Polygon("The house", houseVectorList);
				}
				catch (NotPolygonException exception)
				{
					System.out.println("Erreur création de la maison");
				}
				
				JSONArray patternsList = new JSONArray();
				JSONObject pattern = null;
				JSONArray finalNodeList = null;
				JSONObject node=null;
				
				ArrayList<ArrayList<Position>> houseResults = PolygonDetection.isTherePolygon(nodeList, 
						house, APROXIMATION);
				System.out.println("Liste de \"house\" détectés :\n"+houseResults.toString());
				System.out.println("Nettoyage des résultats :");
				ArrayList<ArrayList<Position>> cleanHouseResults = 
						RoadCheckPolygon.getClearedListFromBadPolygonRoad(houseResults, roads);
				System.out.println("Liste de \"house\" valides :\n"+cleanHouseResults.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
