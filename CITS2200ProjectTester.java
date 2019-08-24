import java.io.*;
import java.util.*;

public class CITS2200ProjectTester {
	public static  void loadGraph(CITS2200Project project, String path) {
		// The graph is in the following format:
		// Every pair of consecutive lines represent a directed edge.
		// The edge goes from the URL in the first line to the URL in the second line.
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while (reader.ready()) {
				String from = reader.readLine();
				String to = reader.readLine();
				//System.out.println("Adding edge from " + from + " to " + to);
				//System.out.println("Adding edge from " + to + " to " + from);
				project.addEdge(from, to);
			}
		} catch (Exception e) {
			System.out.println("There was a problem:");
			System.out.println(e.toString());
		}
		//check if graph is correct
		/*for(Map.Entry<String, Nodes> entry : project.getGraph().graph.entrySet()) {
			System.out.println("parent " + entry.getKey());
			System.out.println("No of children= "+entry.getValue().numOfChildren);
			for(Nodes n : entry.getValue().adjacentVertices) {
				System.out.println("children are: " + n.urlName);
			}
			
		}*/
		/*for(Map.Entry<Integer, String> entry : project.getGraphT().indexToUrl.entrySet()) {
			System.out.println(entry.getKey() +" "+ entry.getValue());
		}*/
		
	}

	public static void main(String[] args) {
		// Change this to be the path to the graph file.
		//medium_graph2 is based on the youtube vid
		String pathToGraphFile = "C:\\Users\\Michael\\Desktop\\medium_graph.txt";
		// Create an instance of your implementation.
		CITS2200Project proj = new Project();
		// Load the graph into the project.
		loadGraph(proj, pathToGraphFile);
		
		//int result = proj.getShortestPath("/wiki/Colombia1", "/wiki/Iraq6");
		//System.out.println(result);
		
		String[] S = proj.getCenters();
		for(String s : S) {
			System.out.println(s);
		}
		
		//String[][] ss = proj.getStronglyConnectedComponents();
		/*for(int i = 0; i < ss.length; i++ ) {
			System.out.println(ss[i].length);
			for(int j = 0 ; j < ss[i].length; j ++) {
				
				System.out.println(ss[i][j]);
			}
			
		}*/
		//proj.getHamiltonianPath();
	}
}
	
	
	
