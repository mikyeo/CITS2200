/*
 * Michael Yeo, 22394414
 * Beining Chen, 22384298
 * 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


public class Project implements CITS2200Project{
	
	Graph g;
	Graph gTransposed;
	
	public Project() {
		g = new Graph();
		gTransposed = new Graph();
	}
	
	public void addEdge(String urlFrom, String urlTo) {
		
		g.addEdge(urlFrom, urlTo);
		gTransposed.addEdge(urlTo, urlFrom);
		
	}
	//for the project tester
	public Graph getGraph() {
		return g;
	}
	public Graph getGraphT() {
		return gTransposed;
	}
	
	
	public int getShortestPath(String urlFrom, String urlTo) {
		int urlFromNodeIndex = g.getCorrespondingInt(urlFrom);
		int urlToNodeIndex = g.getCorrespondingInt(urlTo);
		
		//check if the given node is inside the graph
		if(urlFromNodeIndex == -1 || urlToNodeIndex == -1) {
			return -1;
		}
		
		
		int numVertices = g.numOfNodes;
		int[] parents = new int[numVertices];
		
		int[] distance = new int[numVertices];
		Arrays.fill(distance, -1);
		
		boolean[] seen = new boolean[numVertices];
		Arrays.fill(seen, false);
		
		Queue<Integer> queue = new LinkedList<>();
		queue.add(urlFromNodeIndex);
		
		distance[urlFromNodeIndex] = 0;
		
		while(!queue.isEmpty()) {
			int currentNode = queue.remove();
			if(currentNode == urlToNodeIndex) {
				return distance[currentNode];
			}
			//visit the first node in the queue
			if(!seen[currentNode]) {
				//we set this node as seen 
				seen[currentNode] = true;
				//iterate through the children of this node
				for(Nodes node : g.graph.get(g.indexToUrl.get(currentNode)).adjacentVertices) {
					//set the distance of each children to be plus one of the 
					//distance of the current node.
					if(!seen[node.nodeIndex]) {
						// check if the child node has an improved distance that is not -1
					if(distance[node.nodeIndex] == -1 || distance[node.nodeIndex] > distance[currentNode] + 1) {
					distance[node.nodeIndex] = distance[currentNode] + 1;
					//set the parent of this child to be the current node
					parents[node.nodeIndex] = currentNode;
					//add the children to the queue
					queue.add(node.nodeIndex);
					}
					}
				}
				
			}
		}
		return -1;
	}
	
	
	@Override
	public String[] getCenters() {
		//create an array list to hold the eccentricities for all the vertices
		ArrayList<String> radius = new ArrayList<String>();
		int shortestEccentricities = Integer.MAX_VALUE;
		
		//Perform BFS for each of the vertices in the graph
		for(Map.Entry<String, Nodes> entry : g.graph.entrySet()) {
			int currentNodeIndex = entry.getValue().nodeIndex;
			
			Queue<Integer> queue = new LinkedList<>();
			
			int[] distance = new int[g.numOfNodes];
			Arrays.fill(distance, -1);
			
			boolean[] seen = new boolean[g.numOfNodes];
			Arrays.fill(seen, false);
			
			int[] parents = new int[g.numOfNodes];
			
			queue.add(currentNodeIndex);
			distance[currentNodeIndex] = 0;
			
			while(!queue.isEmpty()) {
				int currentNode = queue.remove();
				if(!seen[currentNode]) {
					seen[currentNode] = true;
					
					for(Nodes node : g.graph.get(g.indexToUrl.get(currentNode)).adjacentVertices) {
						//set the distance of each children to be plus one of the 
						//distance of the current node.
						if(!seen[node.nodeIndex]) {
							// check if the child node has an improved distance that is not -1
						if(distance[node.nodeIndex] == -1 || distance[node.nodeIndex] > distance[currentNode] + 1) {
						distance[node.nodeIndex] = distance[currentNode] + 1;
						//set the parent of this child to be the current node
						parents[node.nodeIndex] = currentNode;
						//add the children to the queue
						queue.add(node.nodeIndex);
						}
						
						}
					}
					
					
				}
			}
			
			//finding the eccentricity of currentNodeIndex
			int max = -1;
			for(int i = 0; i < distance.length ; i++) {
				 if(distance[i] > max) {
					max = distance[i];
				}
			}
			//finding the shortest vertex eccentricity 
			//and adding that vertex to radius
			if(max == shortestEccentricities ) {
				radius.add(entry.getKey());
			}
			
			else if(max < shortestEccentricities) {
				shortestEccentricities = max;
			    radius.clear();
			    radius.add(entry.getKey());
			}
			

			
		}
		/*
		 * String[] centers = new String[radius.size()];
		 * 
		 * for(int j = 0; j < radius.size(); j++) { centers[j] = radius.get(j); }
		 */
		 String[] centers = radius.toArray(new String[radius.size()]);
		
		
		
		return centers;
	}
	
	
	//Code referenced from: https://www.geeksforgeeks.org/strongly-connected-components/
	@Override
	public String[][] getStronglyConnectedComponents() {
		Stack<Integer> stack = new Stack<>();
		
		ArrayList<String[]> scc = new ArrayList<>();
		//Mark all the vertices as not visited
		boolean visited[] = new boolean[g.numOfNodes];
		Arrays.fill(visited, false);
		
		//Fill vertices in stack according to their finishing times
		for(int i = 0; i < g.numOfNodes ; i++) {
			if(!visited[i]) {
				fillOrder(i, visited, stack);
			}
		}
		for(int i : stack) {
			System.out.print(i);
			
		}
		System.out.println();
		
		//Mark all the vertices in the second DFS as not visited
		Arrays.fill(visited, false);
		//Now process all the vertices in order defined by stack
		while(!stack.isEmpty()) {
			//Pop the top vertex from stack
			int current = stack.pop();
			System.out.println(current);
			//create a new ArrayList to store the scc
			
			if(!visited[current]) {
				ArrayList<String> s = new ArrayList<>();
			DFS(current, gTransposed, visited,s);
			//System.out.println();
			scc.add(s.toArray(new String[s.size()]));
			}
			
			
			
			
		}
		String[][] SCC = scc.toArray(new String[scc.size()][]);
		//System.out.println(Arrays.deepToString(SCC));
		return SCC;
	}
	
	
	//A recursive function to print DFS starting from v
	public void DFS(int v, Graph gr, boolean[] visited, ArrayList<String> s) {
		
		//Mark the current node as visited 
		visited[v] = true;
		//nodeIndex still follows g not gT
		s.add(g.indexToUrl.get(v));
		//System.out.print(gr.indexToUrl.get(v));
		int n;
		
		//Recur for all the vertices adjacent to this vertex
		for(Nodes node : gr.graph.get(g.indexToUrl.get(v)).adjacentVertices) {
			/*if(node.numOfChildren == 0) {
				break;
			}*/
			//get this node from the non transposed graph! not the transposed one
			//the index for each node in gTransposed should be the same as g
			//but got swapped instead haha. 
			n = g.graph.get(node.urlName).nodeIndex;
			if(!visited[n])
				DFS(n, gr, visited,s);
			
		}
		
		
	}
	
	
	//Fill vertices in stack according to their finishing times
	public void fillOrder(int v, boolean visited[], Stack<Integer> stack) {
		//Mark the current node as visited
		visited[v] = true;
		//Recur for all the vertices adjacent to this vertex
		for(Nodes node : g.graph.get(g.indexToUrl.get(v)).adjacentVertices) {
			int n = node.nodeIndex;
			if(!visited[n])
				fillOrder(n,visited,stack);
		}
		//All vertices reachable from v are processed by now
		stack.push(new Integer(v));
	}
	
 
	//Used backtracking method
	@Override
	public String[] getHamiltonianPath() {
		int V =g.numOfNodes;
		//record Hamiltonian backtrack path
		ArrayList<String> HamPath = new ArrayList<>();
		//mark if the vertex has been visited
		boolean[] visited=new boolean[V];
		for(int i=0; i<V;i++) {
			//initialize, when no vertex is visited
			visited[i]=false;
		}
		
		
		int start = 0;
		HamPath.add(g.indexToUrl.get(start));
		visited[start] = true;
		
		//traverse from 0th vertex,if there is a HamPath,return it, otherwise none 
		dfs(HamPath,visited,start,start);
		
				
		for(String i:HamPath) {
			System.out.println(i);
		    }
		if(HamPath.size() < g.numOfNodes) {
			return null;
		}

		
		String[] HamiltonianPath = HamPath.toArray(new String[HamPath.size()]);
        return HamiltonianPath;
	}

	public void dfs(ArrayList<String> path, boolean[] visited, int currentNode,int s) {
		//System.out.println(g.indexToUrl.get(currentNode));
		
			//visit the adjacent vertices
			for(Nodes node : g.graph.get(g.indexToUrl.get(currentNode)).adjacentVertices) {
			int n = node.nodeIndex;
			//adjacent vertices must not be visited and the start vertex
			if(!visited[n]) {
				visited[n]=true;
				path.add(g.indexToUrl.get(n));
				//hitting a deadend 
				if(node.numOfChildren == 0 && path.size() == g.numOfNodes - 2) {
					visited[n]=false;
					path.remove(g.indexToUrl.get(n));
					//continue to next adjacent vertex
					continue;
				}
				if(node.numOfChildren == 0 && path.size() < g.numOfNodes -2) {
					break;
				}
				if(path.size() == g.numOfNodes) {
					break;
				}
				dfs(path,visited,n,s);
				
			}
			
		
		}
			
	}
		
	}
	
    

	

