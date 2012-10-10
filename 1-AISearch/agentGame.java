import java.io.*;
import java.util.*;


public class agentGame {

	static int BOARD_SIZE;
	static int OPERATION1_COST, OPERATION2_COST, OPERATION3_COST, OPERATION4_COST;
	static int POSITION_Ax, POSITION_Ay;
	static int POSITION_Bx, POSITION_By;
	static LinkedList<Position> OBSTACLES = new LinkedList<Position>();
	static Stack<myNode> BFSStack = new Stack<myNode>();
	static Stack<myNode> UCSStack = new Stack<myNode>();
	static int BFS_moves;
	static int UCS_cost;
	static int GOAL_REACHED=0;

	public static void main(String[] args) {
		//Initialize the queue for BFS
		Queue<myNode> qe=new LinkedList<myNode>();

		//Initialize the Priority Queue for UCS
		Comparator<myNode> comparator = new EdgeCostComparator();
        PriorityQueue<myNode> pqe = new PriorityQueue<myNode>(1, comparator);

		// Read values from input file
		readinputfile();
		
		//Defining the ROOT node: Initial start point for players A & B
		myNode root = new myNode(POSITION_Ax,POSITION_Ay,POSITION_Bx,POSITION_By,0,null);
		
		//Call BFS
		BFS(qe,root);

		//Call UCS
		UCS(pqe,root);
		
		//Print output
		printoutputfile();

	}

	//READ INPUT FROM input.txt
	public static void readinputfile() {

		File aFile = new File ("input.txt");

		try {
			BufferedReader input =  new BufferedReader(new FileReader(aFile));

			try {

				BOARD_SIZE = Integer.parseInt(input.readLine());
				OPERATION1_COST = Integer.parseInt(input.readLine());
				OPERATION2_COST = Integer.parseInt(input.readLine());
				OPERATION3_COST = Integer.parseInt(input.readLine());
				OPERATION4_COST = Integer.parseInt(input.readLine());

				String delims = "[ ]+";

				String[] tokens = input.readLine().split(delims);
				POSITION_Ax = Integer.parseInt(tokens[0]);
				POSITION_Ay = Integer.parseInt(tokens[1]);

				tokens = input.readLine().split(delims);
				POSITION_Bx = Integer.parseInt(tokens[0]);
				POSITION_By = Integer.parseInt(tokens[1]);

				String line = null;
				Position p;
				while (( line = input.readLine()) != null){
					tokens = line.split(delims);
					p = new Position();
					p.x = Integer.parseInt(tokens[0]);
					p.y = Integer.parseInt(tokens[1]);	
					OBSTACLES.add(p);

				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
	}

	public static void BFS(Queue<myNode> qe,myNode root){

		myNode current_node=new myNode();
		myNode next_node = new myNode();
		qe.add(root);
		int moves=0;
		while (!qe.isEmpty() && moves<1000){
			current_node = (myNode) qe.remove();
			moves++;
			
			if(checkGoal(current_node))
			{
				GOAL_REACHED=1;
				break;
			}
			//Operation 1 - A goes UP, B goes RIGHT
			if(isValid(current_node.Ax,current_node.Ay-1) && isValid(current_node.Bx+1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay-1,current_node.Bx+1,current_node.By,OPERATION1_COST,current_node);
				qe.add(next_node);
			}
			//Operation 2 - A goes RIGHT, B goes DOWN
			if(isValid(current_node.Ax+1,current_node.Ay) && isValid(current_node.Bx,current_node.By+1))
			{
				next_node = new myNode(current_node.Ax+1,current_node.Ay,current_node.Bx,current_node.By+1,OPERATION2_COST,current_node);
				qe.add(next_node);
			}
			//Operation 3 - A goes DOWN, B goes LEFT
			if(isValid(current_node.Ax,current_node.Ay+1) && isValid(current_node.Bx-1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay+1,current_node.Bx-1,current_node.By,OPERATION3_COST,current_node);
				qe.add(next_node);
			}
			//Operation 4 - A goes LEFT, B goes UP
			if(isValid(current_node.Ax-1,current_node.Ay) && isValid(current_node.Bx,current_node.By-1))
			{
				next_node = new myNode(current_node.Ax-1,current_node.Ay,current_node.Bx,current_node.By-1,OPERATION4_COST,current_node);
				qe.add(next_node);
			}

		}

		//Save the shortest path to stack
		BFSStack.add(current_node);
		BFS_moves=0;
		while (current_node.parent!=null)
		{
			current_node = current_node.parent;
			BFSStack.add(current_node);
			BFS_moves++;
		}	
		

	}

	public static void UCS(PriorityQueue<myNode> qe,myNode root){
		myNode current_node=new myNode();
		myNode next_node = new myNode();
		qe.add(root);
		int moves=0;
		while (!qe.isEmpty() && moves<1000){
			current_node = (myNode) qe.remove();
			moves++;

			if(checkGoal(current_node))
			{
				GOAL_REACHED=1;
				break;
			}
			//Operation 1 - A goes UP, B goes RIGHT
			if(isValid(current_node.Ax,current_node.Ay-1) && isValid(current_node.Bx+1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay-1,current_node.Bx+1,current_node.By,OPERATION1_COST,current_node);
				if(current_node.parent!=null)
					next_node.cost = next_node.cost + next_node.parent.cost;
				qe.add(next_node);
			}
			//Operation 2 - A goes RIGHT, B goes DOWN
			if(isValid(current_node.Ax+1,current_node.Ay) && isValid(current_node.Bx,current_node.By+1))
			{
				next_node = new myNode(current_node.Ax+1,current_node.Ay,current_node.Bx,current_node.By+1,OPERATION2_COST,current_node);
				if(current_node.parent!=null)
					next_node.cost = next_node.cost + next_node.parent.cost;
				qe.add(next_node);
			}
			//Operation 3 - A goes DOWN, B goes LEFT
			if(isValid(current_node.Ax,current_node.Ay+1) && isValid(current_node.Bx-1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay+1,current_node.Bx-1,current_node.By,OPERATION3_COST,current_node);
				if(current_node.parent!=null)
					next_node.cost = next_node.cost + next_node.parent.cost;
				qe.add(next_node);
			}
			//Operation 4 - A goes LEFT, B goes UP
			if(isValid(current_node.Ax-1,current_node.Ay) && isValid(current_node.Bx,current_node.By-1))
			{
				next_node = new myNode(current_node.Ax-1,current_node.Ay,current_node.Bx,current_node.By-1,OPERATION4_COST,current_node);
				if(current_node.parent!=null)
					next_node.cost = next_node.cost + next_node.parent.cost;
				qe.add(next_node);
			}

		}

		//Save the shortest path to stack
		UCSStack.add(current_node);
		UCS_cost = current_node.cost;
		while (current_node.parent!=null)
		{
			current_node = current_node.parent;
			UCSStack.add(current_node);
		}	
		
	}

	public static boolean isValid(int x, int y)
	{
		int blocked=0;

		for(int i = 0, n = OBSTACLES.size(); i < n; i++)
			if(OBSTACLES.get(i).x == x && OBSTACLES.get(i).y == y)
				blocked = 1;

		if(x>0 && y>0 && x<=BOARD_SIZE && y<=BOARD_SIZE && blocked==0)
			return true;
		else 
			return false;
	}

	public static boolean checkGoal(myNode node)
	{
		if(node.Ax == node.Bx && node.Ay == node.By)
			return true;
		else
			return false;
	}

	public static void printoutputfile(){
		
		myNode current_node = new myNode();
		
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter("output.txt"));
			if(GOAL_REACHED==1)
			{
			//Print the shortest path for BFS
			out.println(BFS_moves);
			while (!BFSStack.isEmpty())
			{
				current_node = BFSStack.pop();
				out.println(current_node.Ax+" "+current_node.Ay);
			}
			
			//SPACE
			out.println();
			
			//Print the shortest path for UCS
			out.println(UCS_cost);
			while (!UCSStack.isEmpty())
			{
				current_node = UCSStack.pop();
				out.println(current_node.Ax+" "+current_node.Ay);
			}
			}
			else
				out.println(-1);
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}

class Position{
	int x, y;

	Position(){
		this(0,0);
	}
	Position(int x_value, int y_value) {
		this.x = x_value; 
		this.y = y_value;
	}
}

class myNode {

	int Ax,Ay,Bx,By;
	int cost;
	myNode parent;

	myNode()
	{
		this(0,0,0,0,0,null);			
	} 
	myNode(int Ax, int Ay, int Bx, int By,int cost, myNode p) {
		this.Ax=Ax;
		this.Ay=Ay;
		this.Bx=Bx;
		this.By=By;
		this.cost=cost;
		this.parent=p;
		
	}

}

class EdgeCostComparator implements Comparator<myNode>
{

	//@Override
	public int compare(myNode arg0, myNode arg1) {
		if (arg0.cost < arg1.cost)
			return -1;
		if (arg0.cost > arg1.cost)
			return 1;
		else
			return 0;
	}
}
