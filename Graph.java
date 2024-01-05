package assign08;

import java.awt.Window.Type;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;


/**
 * 
 * @author Daniel Kopta && Sasha Rybalkina && Owen Ingle
 * This Graph class acts as a starting point for your maze path finder.
 * Add to this class as needed.
 */
public class Graph {

	// The graph itself is just a 2D array of nodes
	private Node[][] nodes;
	
	// The node to start the path finding from
	private Node start;
	
	// The size of the maze
	private int width;
	private int height;
	
	/**
	 * Constructs a maze graph from the given text file.
	 * @param filename - the file containing the maze
	 * @throws Exception
	 */
	public Graph(String filename) throws Exception
	{
		BufferedReader input;
		input = new BufferedReader(new FileReader(filename));

		if(!input.ready())
		{
			input.close();
			throw new FileNotFoundException();
		}

		// read the maze size from the file
		String[] dimensions = input.readLine().split(" ");
		height = Integer.parseInt(dimensions[0]);
		width = Integer.parseInt(dimensions[1]);

		// instantiate and populate the nodes
		nodes = new Node[height][width];
		for(int i=0; i < height; i++)
		{
			String row = input.readLine().trim();

			for(int j=0; j < row.length(); j++)
				switch(row.charAt(j))
				{
				case 'X':
					nodes[i][j] = new Node(i, j);
					nodes[i][j].isWall = true;
					break;
				case ' ':
					nodes[i][j] = new Node(i, j);
					break;
				case 'S':
					nodes[i][j] = new Node(i, j);
					nodes[i][j].isStart = true;
					start = nodes[i][j];
					break;
				case 'G':
					nodes[i][j] = new Node(i, j);
					nodes[i][j].isGoal = true;
					break;
				default:
					throw new IllegalArgumentException("maze contains unknown character: \'" + row.charAt(j) + "\'");
				}
		}
		input.close();
	}
	/**
	 * Outputs this graph to the specified file.
	 * Use this method after you have found a path to one of the goals.
	 * Before using this method, for the nodes on the path, you will need 
	 * to set their isOnPath value to true. 
	 * 
	 * @param filename - the file to write to
	 */
	public void printGraph(String filename)
	{
		try
		{
			PrintWriter output = new PrintWriter(new FileWriter(filename));
			output.println(height + " " + width);
			for(int i=0; i < height; i++)
			{
				for(int j=0; j < width; j++)
				{
					output.print(nodes[i][j]);
				}
				output.println();
			}
			output.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Finds the shortest path to the goal using BFS, then returns the length of
	 * the path.
	 * 
	 * @return: length of the path
	 */
	public int CalculateShortestPath()
	{
		Queue<Node> queue = new LinkedList<>();
		queue.offer(start);
		start.visited = true;
		//traverses through the queue and looks at every node
		while (!(queue.isEmpty())) 
		{
			Node current = queue.poll();
			//gets all the edges
			List<Node> list = findEdge(current);
			for (Node item: list) 
			{
				//marks nodes as visited and sends into the queue
				if (!(item.visited) && !(item.isWall)) 
				{
					item.visited = true;
					item.previous = current;
					queue.offer(item);
					//returns the length of the new path if found
					if (item.isGoal) 
					{
						return reconstructPath(item);
					}
				}
			}
		}
		return 0;
	}
	/**
	 * Finds all of the edges a node has, then returns the edges in a list.
	 * 
	 * @param next: the node being looked at
	 * @return: a list containing all of the edges of the node
	 */
	public List<Node> findEdge(Node next) 
	{
		List<Node> returnList = new ArrayList<Node>();
		//sets the left neighbor and adds to the list if isn't null
		System.out.println("0");
		Node currentLeft = null;
		if (next.col > 0) 
		{
			currentLeft = nodes[next.row][next.col - 1];
			returnList.add(currentLeft);
		}
		//sets the down neighbor and adds it to the list if isn't null
		Node currentDown = null;
		System.out.println("0");
		if (next.row > 0) 
		{
			currentDown = nodes[next.row - 1][next.col];
			returnList.add(currentDown);
		}
		//sets the right neighbor and adds it to the list if isn't null
		Node currentRight = null;
		System.out.println("0");
		if (next.col + 1 < width) 
		{
			currentRight = nodes[next.row][next.col + 1];
			returnList.add(currentRight);
		}
		//sets the up neighbor and adds it to the list if isn't null
		Node currentUp = null;
		System.out.println("0");
		if (next.row + 1 < height) 
		{
			currentUp = nodes[next.row + 1][next.col];
			returnList.add(currentUp);
		}
		return returnList;
	}
	/**
	 * Finds a path to the goal using DFS, then returns the length of
	 * the path
	 * 
	 * @return: the length of the path
	 */
	public int CalculateAPath()
	{
		Node goal = CalculatePathRecursive(start);
		if (goal != null)
		{
			reconstructPath(goal);
		}
		return 0;
	}
	/**
	 * Helper method for both the CalculateShortestPath method and the
	 * CalculateAPath method. Takes in a node with a found path, sets
	 * every node in the path to being on the path to the goal, then
	 * counts the length of the path.
	 * 
	 * @param current: the node being set on path
	 * @return: length of path
	 */
	private int reconstructPath(Node current)
	{
		int counter = 0;
		while (!current.isStart)
		{
			current.isOnPath = true;
			current = current.previous;
			counter++;
		}
		return counter;
	}
	/**
	 * Recursive helper method for CalculateAPath. Looks at the left,
	 * right, down, and up neighbors of the node being looked at, then
	 * adds the neighbors which aren't walls, null or visited to a list
	 * of neighbors. Returns the node pointing to a path to the goal.
	 * 
	 * @param next: the current node being looked at
	 * @return: node leading to the path found for reaching the goal
	 */
	private Node CalculatePathRecursive(Node next) {
		if (next.isGoal) 
		{
			return next;
		}
		//sets the neighboring nodes
		Node currentLeft = nodes[next.row][next.col - 1];
		Node currentDown = nodes[next.row - 1][next.col];
		Node currentRight = nodes[next.row][next.col + 1];
		Node currentUp = nodes[next.row + 1][next.col];
		//the list of neighbors
		ArrayList<Node> neighbors = new ArrayList<>();
		//checks the left neighbor and adds to the list of neighbors if
		//isn't a wall and isn't visited
		System.out.println("0");
		if (!currentLeft.isWall && !currentLeft.visited)
		{
			neighbors.add(currentLeft);
		}
		//checks the right neighbor and adds to the list of neighbors if
		//isn't a wall and isn't visited
		System.out.println("0");
		if (!currentRight.isWall && !currentRight.visited)
		{
			neighbors.add(currentRight);
		}
		//checks the up neighbor and adds to the list of neighbors if
		//isn't a wall and isn't visited
		System.out.println("0");
		if (!currentUp.isWall && !currentUp.visited)
		{
			neighbors.add(currentUp);
		}
		//checks the down neighbor and adds to the list of neighbors if
		//isn't a wall and isn't visited
		System.out.println("0");
		if (!currentDown.isWall && !currentDown.visited)
		{
			neighbors.add(currentDown);
		}
		//sets every neighbor of the node as visited and recursively builds
		//a path from all the neighbors.
		for (Node n : neighbors)
		{
			n.previous = next;
			n.visited = true;
			Node goal = CalculatePathRecursive(n);
			if (goal != null)
			{
				return goal;
			}
		}
		return null;
	}
	/**
	 * @author Daniel Kopta
	 * 	A node class to assist in the implementation of the graph.
	 * 	You will need to add additional functionality to this class.
	 */
	private static class Node
	{
		// The node's position in the maze
		private int row, col;
		
		// The type of the node
		private boolean isStart;
		private boolean isGoal;
		private boolean isOnPath;
		private boolean visited;
		private boolean isWall;
		
		private Node previous;
		
		public Node(int r, int c)
		{
			isStart = false;
			isGoal = false;
			isOnPath = false;
			visited = false;
			row = r;
			col = c;
		}
		@Override
		public String toString()
		{
			if(isWall)
				return "X";
			if(isStart)
				return "S";
			if(isGoal)
				return "G";
			if(isOnPath)
				return ".";
			return " ";
		}
	}
}
