/*
 * Michael Yeo, 22394414
 * Beining Chen, 22384298
 * 
 */
import java.util.LinkedList;

public class Nodes {
	String urlName;
	int nodeIndex;
	LinkedList<Nodes> adjacentVertices;
	int numOfChildren = 0;
	
	public Nodes(String urlName, int nodeIndex) {
		this.urlName = urlName;
		this.nodeIndex = nodeIndex;
		adjacentVertices = new LinkedList<>();
		
	}
	//Adds a node that is adjacent
	public void addAdjacentVertex(Nodes node) {
		adjacentVertices.add(node);
		numOfChildren++;
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public int getNodeIndex() {
		return nodeIndex;
	}
	public boolean hasChild(int index) {
		boolean b = false;
		for(Nodes node : adjacentVertices) {
			b = (index == node.nodeIndex);
				
		}
		return b;
	}

}
