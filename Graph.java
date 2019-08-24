/*
 * Michael Yeo, 22394414
 * Beining Chen, 22384298
 * 
 */
import java.util.LinkedHashMap;
import java.util.Map;

public class Graph {
	Map<String, Nodes> graph;
	Map<Integer, String> indexToUrl;
	int numOfNodes = 0;
	int numOfEdges = 0;
	
	public Graph() {
		graph = new LinkedHashMap<>();
		indexToUrl = new LinkedHashMap<>();
	}
	
	public boolean hasVertex() {
		return numOfNodes == 0;
	}
	
	
	public int getCorrespondingInt(String S) {
		return graph.get(S).nodeIndex;
	}
	
	
	
public void addEdge(String urlFrom, String urlTo) {
		
		//Search the graph for urlFrom and urlTo
		//if the graph does not contain urlFrom and urlTo, add it to the graph
		if(!graph.containsKey(urlFrom) && !graph.containsKey(urlTo)){
			//nodeIndex of that node becomes numOfNodes++
			graph.put(urlFrom, new Nodes(urlFrom, numOfNodes));
			indexToUrl.put(graph.get(urlFrom).nodeIndex, urlFrom);
			numOfNodes ++;
			graph.put(urlTo, new Nodes(urlTo, numOfNodes));
			indexToUrl.put(graph.get(urlTo).nodeIndex, urlTo);
			numOfNodes ++;
			//make urlTo the child of urlFrom
	        graph.get(urlFrom).addAdjacentVertex(graph.get(urlTo));
	        return;
		}
		
		//if the graph contains urlFrom but not UrlTo, add it to the graph
		if(graph.containsKey(urlFrom) && !graph.containsKey(urlTo)){
			graph.put(urlTo, new Nodes(urlTo, numOfNodes));
			indexToUrl.put(graph.get(urlTo).nodeIndex, urlTo);
			numOfNodes++;
			//make urlTo the child of urlFrom
	        graph.get(urlFrom).addAdjacentVertex(graph.get(urlTo));
	        return;
		}
		
		//if the graph contains urlTo but not UrlFrom, add it to the graph
		if(!graph.containsKey(urlFrom) && graph.containsKey(urlTo)){
			graph.put(urlFrom, new Nodes(urlFrom, numOfNodes));
			indexToUrl.put(graph.get(urlFrom).nodeIndex, urlFrom);
			numOfNodes++;
			//make urlTo the child of urlFrom
	        graph.get(urlFrom).addAdjacentVertex(graph.get(urlTo));
	        return;
		}
		
		//if both are present
	
		else if(graph.containsKey(urlFrom) && graph.containsKey(urlTo)) {
		//make urlTo the child of urlFrom
        graph.get(urlFrom).addAdjacentVertex(graph.get(urlTo));
        
		}
		
		
	}

}
