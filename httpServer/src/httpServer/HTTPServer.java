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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
	public final static double APROXIMATION = 0.5;

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

				System.out.println("Latitude : "+finaltab.get("latitude")
						+" / Longitude : "+finaltab.get("longitude"));
				
				//Request to OverPass Turbo
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
				// RoadNodePosition list, for Road creation
				List<RoadNodePosition> nodeRoadList = new ArrayList<RoadNodePosition>();
				//Position list, representing all the nodes
				ArrayList<Position> nodeList = new ArrayList<Position>();
				//Road list
				ArrayList<Road> roads = new ArrayList<Road>();
				
				while(iterator.hasNext())
				{
					JSONObject currentElement = iterator.next();
					//If that's a node
					if (currentElement.get("type").equals("node"))
					{
						//Creating the RoadNodePosition corresponding, which is added to the RoadNodePosition list
						nodeRoadList.add(new RoadNodePosition(
								Double.parseDouble(currentElement.get("id").toString()),
								Double.parseDouble(currentElement.get("lat").toString()),
								Double.parseDouble(currentElement.get("lon").toString()
										)));
					}
					//If that's a road
					else if (currentElement.get("type").equals("way"))
					{
						//Temp list for road node
						ArrayList<Position> roadNodeList = new ArrayList<Position>();
						JSONArray jsonWayNodes = (JSONArray) currentElement.get("nodes");
						Iterator<Long> nodesIterator = jsonWayNodes.iterator();
						//Browsing the JSON, seeking the node
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
				
				System.out.println("Node list : Retreived from OverPass");
				
				//Converting the RoadNodePosition list to a Position list
				for (int i = 0; i < nodeRoadList.size(); i++)
					nodeList.add(nodeRoadList.get(i).toPosition());
				
				System.out.println("Road list : Successfully built from node list");
				
				
				//Getting the Polygon list from the XML file
				ArrayList<Polygon> polygonList = PatternFromDB.getPolygonList();
				System.out.println("Starting seeking patterns : "+polygonList.size()+" pattern(s) to search...");
				
				JSONArray patternsList = new JSONArray();
				JSONObject pattern = null;
				JSONArray finalNodeList = null;
				JSONObject node=null;
				
				//List of detected polygons
				ArrayList<ArrayList<ArrayList<Position>>> detectedPolygons = 
						new ArrayList<ArrayList<ArrayList<Position>>>();
				
				//Getting the available Polygons
				for(int i=0; i < polygonList.size(); i++)
				{
					detectedPolygons.add(PolygonDetection.isTherePolygon(nodeList, polygonList.get(i), APROXIMATION));
				}
				
				ArrayList<ArrayList<ArrayList<Position>>> cleanedPolygons = new ArrayList<ArrayList<ArrayList<Position>>>();
				
				//Cleaning the list from bad Polygons, which don't match with roads
				for(int i=0; i < detectedPolygons.size(); i++)
				{
					cleanedPolygons.add(RoadCheckPolygon.getClearedListFromBadPolygonRoad(
							detectedPolygons.get(i), roads));
				}

				int nbValidePolygon = 0, nbFoundPolygon = 0;

				for (int i = 0; i < detectedPolygons.size(); i++)
					nbFoundPolygon += detectedPolygons.get(i).size();
				
				for (int i = 0; i < cleanedPolygons.size(); i++)
					nbValidePolygon += cleanedPolygons.get(i).size();

				System.out.println(nbFoundPolygon+" Patterns found.");
				System.out.println(nbValidePolygon+" Patterns left after the road check.");
				
				//Sending the cleaned list as a JSON object
				for (int i0 = 0; i0 < detectedPolygons.size(); i0++)
				{
					ArrayList<ArrayList<Position>> currentCleanedPolygonList = cleanedPolygons.get(i0);
					for(int i=0; i < 3 && i < currentCleanedPolygonList.size(); i++)
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
