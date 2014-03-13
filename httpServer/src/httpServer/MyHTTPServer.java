package httpServer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import polygon2DDetection.NotPolygonException;
import polygon2DDetection.Polygon;
import polygon2DDetection.PolygonDetection;
import vector2D.Position;
import vector2D.Vector;

public class MyHTTPServer extends Thread {

	Socket connectedClient = null;
	BufferedReader inFromClient = null;
	OutputStreamWriter outToClient = null;
	public final static double APROXIMATION = 0.001;

	public MyHTTPServer(Socket client) {
		connectedClient = client;
	}

	@SuppressWarnings("unchecked")
	public void run() {

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

			if (httpMethod.equals("GET")) {
				
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
				HashMap<String, String> coordinatesList = new HashMap<String, String>();
				List<Position> p = new ArrayList<Position>();

				while (iterator.hasNext()) 
				{
					JSONObject currentElement = iterator.next();
					if(currentElement.containsKey("type") && currentElement.get("type").equals("node"))
					{
						p.add(new Position(Double.parseDouble(currentElement.get("lat").toString()) ,
								Double.parseDouble(currentElement.get("lon").toString())) );
					}
				}
				System.out.println("Positions successfully added");
				Date x = new Date();
				System.out.println("Starting seeking patterns");
				
				Position a = new Position(1,1);
				Position b = new Position(4,1);
				Position c = new Position(2,4);
				Position c1 = new Position(44.912198,4.916489);
				Position c2= new Position(44.911712,4.916039);
				Position c3 = new Position(44.911666,4.915674);
				Position c4= new Position(44.911119,4.915717);
				Position c5= new Position(44.91045,4.916382);
				Position c6= new Position(44.910314,4.916811);
				Position c7= new Position(44.909675,4.917541);
				Position c8= new Position(44.909691,4.91812);
				Position c9 = new Position(44.909128,4.918742);
				Position c10= new Position(44.910131,4.919364);
				Position c11= new Position(44.910291,4.919171);
				Position c12= new Position(44.911423,4.920008);
				Position c13= new Position(44.912145,4.918195);
				Position c14= new Position(44.911537,4.917927);

				Vector vh1 = new Vector(c1,c2);
				Vector vh2 = new Vector(c2,c3);
				Vector vh3 = new Vector(c3,c4);
				Vector vh4 = new Vector(c4,c5);
				Vector vh5 = new Vector(c5,c6);
				Vector vh6 = new Vector(c6,c7);
				Vector vh7 = new Vector(c7,c8);
				Vector vh8 = new Vector(c8,c9);
				Vector vh9 = new Vector(c9,c10);
				Vector vh10= new Vector(c10,c11);
				Vector vh11= new Vector(c11,c12);
				Vector vh12= new Vector(c12,c13);
				Vector vh13= new Vector(c13,c14);
				Vector vh14= new Vector(c14,c1);


				Vector v1 = new Vector(a,b); 
				Vector v2 = new Vector(b,c);
				Vector v3 = new Vector(c,a);

				ArrayList<Vector> l1 = new ArrayList<Vector>();

				ArrayList<Vector> vcoeur = new ArrayList<Vector>();

				Polygon triangle1Pattern = null, heartPattern = null;

				l1.add(v1);
				l1.add(v2);
				l1.add(v3);

				vcoeur.add(vh1);
				vcoeur.add(vh2);
				vcoeur.add(vh3);
				vcoeur.add(vh4);
				vcoeur.add(vh5);
				vcoeur.add(vh6);
				vcoeur.add(vh7);
				vcoeur.add(vh8);
				vcoeur.add(vh9);
				vcoeur.add(vh10);
				vcoeur.add(vh11);
				vcoeur.add(vh12);
				vcoeur.add(vh13);
				vcoeur.add(vh14);

				try
				{
					triangle1Pattern = new Polygon("triangle1", l1);
					heartPattern = new Polygon("coeur", vcoeur);
				}
				catch (NotPolygonException exception)
				{
					System.out.println("Erreur création du triangle");
				}
				
				JSONArray patternsList = new JSONArray();
				JSONObject pattern = null;
				JSONArray nodeList = null;
				JSONObject node=null;

				ArrayList<ArrayList<Position>> heartResults = PolygonDetection.isTherePolygon(p, heartPattern, APROXIMATION);
				for(int i=0; i < heartResults.size(); i++)
				{
					pattern=new JSONObject();
					nodeList = new JSONArray();
					for(int j=0; j < heartResults.get(i).size(); j++)
					{
						node=new JSONObject();
						node.put("lat",heartResults.get(i).get(j).getX());
						node.put("lon",heartResults.get(i).get(j).getY());
						nodeList.add(node);
					}
					pattern.put("name", "Heart");
					pattern.put("nodes", nodeList);
					patternsList.add(pattern);
				}
				System.out.println("Patterns found :" + patternsList.toString());
				ArrayList<ArrayList<Position>> triangle1Results = PolygonDetection.isTherePolygon(p, triangle1Pattern, APROXIMATION);
				for(int i=0; i < 10 && i < triangle1Results.size(); i++)
				{
					pattern=new JSONObject();
					nodeList = new JSONArray();
					for(int j=0; j < triangle1Results.get(i).size(); j++)
					{
						node=new JSONObject();
						node.put("lat",triangle1Results.get(i).get(j).getX());
						node.put("lon",triangle1Results.get(i).get(j).getY());
						nodeList.add(node);
					}
					pattern.put("name", "Triangle");
					pattern.put("nodes", nodeList);
					patternsList.add(pattern);
				}
				//System.out.println(patternsList.toString());
				
				JSONParser parser1 = new JSONParser();
				List<Position> patternCoords = new LinkedList<Position>();
				JSONArray jsonPatternsList = (JSONArray) parser1.parse(patternsList.toString());
				Iterator<JSONObject> patternsIterator = jsonPatternsList.iterator();
				
				while (patternsIterator.hasNext()) 
				{
					JSONObject currentPattern = patternsIterator.next();
					JSONArray jsonNodessList = (JSONArray) currentPattern.get("nodes");
					Iterator<JSONObject> nodesIterator = jsonNodessList.iterator();
					while (nodesIterator.hasNext()) 
					{
						JSONObject currentNode = nodesIterator.next();
						patternCoords.add(new Position( Double.parseDouble(currentNode.get("lat").toString()),
								Double.parseDouble(currentNode.get("lon").toString())) );
					}
				}
				System.out.println("Finished ! Took "+ Long.toString(new Date().getTime() - x.getTime()) + " milliseconds" );
				
				sendResponse(200, patternsList.toString());
			}
			else sendResponse(404, "<b>The Requested resource not found ...." +
					"Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>");
		} catch (Exception e) {
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


	public static void main (String args[]) throws Exception {

		ServerSocket Server = new ServerSocket (5000, 10, InetAddress.getByName("172.20.10.4"));
		System.out.println ("TCPServer Waiting for client on port 5000");

		while(true) {
			Socket connected = Server.accept();
			(new MyHTTPServer(connected)).start();
		}
	}
}