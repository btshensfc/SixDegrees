import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

class BFS {
	String startingPoint;
	String goal;
	boolean found = false;

	// finds the shortest path.
	void shortestPath(HashMap<String, LinkedList<String>> map, String startingPoint, String goal){
		this.startingPoint = startingPoint;
		this.goal = goal;

		//queue stores list of actors to check
		LinkedList<String> queue = new LinkedList<String>();
		queue.add(startingPoint);

		// hashset keeps track of what actors are visted
		HashSet<String> visited = new HashSet<String>();
		visited.add(startingPoint);

		// path keeps track of paths that have been tested
		Hashtable<String, String> path = new Hashtable<String, String>();
		path.put(startingPoint, "");

		// this is the path that fitst the best definition
		Stack<String> finalPath = new Stack<String>();
		String tempString = goal;

		// this ends search if there is adirect connection
		if (map.get(startingPoint).contains(goal)){
			System.out.println("Path between " + startingPoint + " and " + goal + ": " + startingPoint + " --> " + goal);
		} else {
			while(!queue.isEmpty()){
				//searches first item in queue
				String currActor = queue.poll();

				LinkedList<String> connections = map.get(currActor);

				for (String coactor : connections){
					if (!visited.contains(coactor)){ //adds to check if hasn't visted
						queue.add(coactor);
						visited.add(coactor);
						path.put(coactor, currActor);

						if (coactor.equals(goal)){ //breaks if goal part of connections
							break;
						}
					}
				}
			}

			// pushed specified path to final path
			while (!tempString.equals(startingPoint)){
				finalPath.push(tempString);
				String link = path.get(tempString);
				tempString = link;
			}
			// push starting point to final path
			finalPath.push(tempString);

			// format output
			System.out.print("Path between " + startingPoint + " and " + goal + ": ");

			// prints out all items inside the final path stack. Arrow only prints if there is a connection.
			while (!finalPath.isEmpty()){
				System.out.print(finalPath.pop());
				try {
					String peek = finalPath.peek();
					System.out.print(" --> ");
				} catch (EmptyStackException e){
					break;
				}
			}
		}
	}
}

public class SixDegrees {
	public static void main(String[] args){
		File inputFile = null;

		// initiate array to simulate hashmap
		HashMap<String, LinkedList<String>> actorsList = new HashMap<String, LinkedList<String>>();

		// find user input from console 
		if (args.length > 0) {
			inputFile = new File(args[0]);
		}

		//inifiate buffered reader to read user input
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(inputFile));
			JSONParser parser = new JSONParser();

			// read in each line from the file
			while ((currentLine = br.readLine()) != null){
				//stores actors temporarily
				ArrayList<String> tempArray = new ArrayList<String>();

				if (currentLine.indexOf("[") != -1) {

					/* edge cases to take care of extra brackets */
					if (currentLine.contains("[Cameo]")){
						currentLine = currentLine.replace("[Cameo]", "(Cameo)");
					}

					if (currentLine.contains("[cameo]")){
						currentLine = currentLine.replace("[cameo]", "(cameo)");
					}

					if (currentLine.contains("[REC]")){
						currentLine = currentLine.replace("[REC]", "(REC)");
					}

					if (currentLine.contains("[Singing voice]")){
						currentLine = currentLine.replace("[Singing voice]", "(Singing voice)");
					}

					//to parse inputs and objectify them
					currentLine = currentLine.substring(currentLine.indexOf("["), currentLine.indexOf("]") + 1);
					currentLine = currentLine.replace("\"\"", "\"");
					Object jsonCast = (Object) parser.parse(currentLine);

					// get names and add them in temp arraylist
					JSONArray jsonCastArray = (JSONArray) jsonCast;
					for (Object obj : jsonCastArray) {
						obj = ((JSONObject) obj).get("name");
						String currActor = ((String) obj);
						currActor = currActor.toLowerCase();
						tempArray.add(currActor);
						LinkedList<String> connections = new LinkedList<String>();
						if (!actorsList.containsKey(currActor)){
							actorsList.put(currActor, connections);
						}
					}


					// add connections
					for (String actor : tempArray){
						LinkedList<String> currActorConnections = actorsList.get(actor);
						for (String coactor : tempArray){
							if (!coactor.equals(actor)){
								currActorConnections.add(coactor);
							}
						}
					}

				}
			}


		} catch (EOFException e){
		} catch (IOException e){
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ParseException e){
			e.printStackTrace();
		}

		// create breath-first search + user inputs
		BFS bfs = new BFS();
		Scanner scanner = new Scanner(System.in);

		//Find Actor 1
		System.out.println("Enter a name for Actor 1 with correct capitalization)");
		String actor1 = (scanner.nextLine()).toLowerCase();
		if (!actorsList.containsKey(actor1)){
			System.out.println("Actor 1 does not exist.");
			return;
		} else {
			System.out.println("Actor 1: " + actor1.toString());
		}


		//Find Actor 2
		System.out.println("Enter a name for Actor 2. (include capitals for first and last name)");
		String actor2 = (scanner.nextLine()).toLowerCase();
		if (!actorsList.containsKey(actor2)){
			System.out.println("Actor 2 does not exist.");
			return;
		} else {
			System.out.println("Actor 2: " + actor2.toString());
		}

		//calls function to calculate the shortest path.
		bfs.shortestPath(actorsList, actor1, actor2);

	}
}
