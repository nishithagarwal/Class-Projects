import java.io.*;
import java.util.*;

public class agentGameAStar {

	private static final int OPERATION_COST = 2;
	int BOARD_SIZE;
	Position POSITION_A;
	Position POSITION_B;
	LinkedList<Position> OBSTACLES;
	
	Stack<myNode> EStack;
	Stack<myNode> MStack;
	Stack<myNode> CStack;
	
	int[] expanded_count;
	
	public agentGameAStar(){
		BOARD_SIZE=0;
		POSITION_A=new Position();
		POSITION_B=new Position();
		OBSTACLES = new LinkedList<Position>();
		
		EStack = new Stack<myNode>();
		MStack = new Stack<myNode>();
		CStack = new Stack<myNode>();
		expanded_count=new int[3];
	}

	public agentGameAStar(int board_size, Position position_a, Position position_b, LinkedList<Position> obstacles,
			Stack<myNode> estack, Stack<myNode> mstack, Stack<myNode> cstack, int[] cost) {
		this.BOARD_SIZE=board_size;
		this.POSITION_A=position_a;
		this.POSITION_B=position_b;
		this.OBSTACLES=obstacles;
		this.EStack=estack;
		this.MStack=mstack;
		this.CStack=cstack;
		this.expanded_count=cost;
	}

	public static void main(String[] args) {

		// Read values from input file
		ArrayList<agentGameAStar> obj_list  = readinputfile();
		
		for(int i=0;i<obj_list.size();i++)
		{
		agentGameAStar agentObject = obj_list.get(i);	
		//Initialize the Priority Queue for A*
		Comparator<myNode> comparator = new EdgeCostComparator();
        PriorityQueue<myNode> pqe = new PriorityQueue<myNode>(1, comparator);
		
		//Defining the ROOT node: Initial start point for players A & B
		myNode root = new myNode(agentObject.POSITION_A.x,agentObject.POSITION_A.y,agentObject.POSITION_B.x,agentObject.POSITION_B.y,0,0,null);
		//Call A* 
		agentObject.AStar(pqe,root,1);
		agentObject.AStar(pqe,root,2);
		agentObject.AStar(pqe,root,3);
		}
		
		//Print output
		printoutputfile(obj_list);

	}

	//READ INPUT FROM input.txt
	public static ArrayList<agentGameAStar> readinputfile() {

		File aFile = new File ("input.txt");
		ArrayList<agentGameAStar> obj_list = new ArrayList<agentGameAStar>();
		try {
			
			if(!aFile.exists()){
				System.out.println("FILE NOT FOUND!");
				return null;
			}
			
			if(!aFile.canRead()){
				System.out.println("CAN'T READ FILE!");
				return null;
			}

			BufferedReader input =  new BufferedReader(new FileReader(aFile));

			String line;
			while ((line=input.readLine())!= null){
				
				agentGameAStar agentObject = new agentGameAStar();
				agentObject.BOARD_SIZE = Integer.parseInt(line);
				
				String delims = "[ ]+";
				String[] tokens = input.readLine().split(delims);
				agentObject.POSITION_A.x = Integer.parseInt(tokens[0]);
				agentObject.POSITION_A.y = Integer.parseInt(tokens[1]);

				tokens = input.readLine().split(delims);
				agentObject.POSITION_B.x = Integer.parseInt(tokens[0]);
				agentObject.POSITION_B.y = Integer.parseInt(tokens[1]);

				Position p;
				line = input.readLine();
				while ( line!=null && line.trim().length()!= 0){
					tokens = line.split(delims);
					p = new Position();
					p.x = Integer.parseInt(tokens[0]);
					p.y = Integer.parseInt(tokens[1]);	
					agentObject.OBSTACLES.add(p);
					line = input.readLine();
				}
				obj_list.add(agentObject);
			}
			input.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		return obj_list;
	}

	public void AStar(PriorityQueue<myNode> qe,myNode root, int heuristic_type){
		myNode current_node=new myNode();
		myNode next_node = new myNode();
		qe.add(root);
		int moves=-1;
		int goal_reached=0;
		double heuristic_cost=0;
		while (!qe.isEmpty() && moves<1000){
			current_node = (myNode) qe.remove();
			moves++;
			if(checkGoal(current_node))
			{
				goal_reached=1;
				break;
			}
			//Operation 1 - A goes UP, B goes RIGHT
			if(isValid(current_node.Ax,current_node.Ay-1) && isValid(current_node.Bx+1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay-1,current_node.Bx+1,current_node.By,OPERATION_COST,0,current_node);
				heuristic_cost=heuristics(next_node,heuristic_type);
				next_node.path_cost+=current_node.path_cost;
				next_node.astar_cost=next_node.path_cost+heuristic_cost;
				qe.add(next_node);
			}
			//Operation 2 - A goes RIGHT, B goes DOWN
			if(isValid(current_node.Ax+1,current_node.Ay) && isValid(current_node.Bx,current_node.By+1))
			{
				next_node = new myNode(current_node.Ax+1,current_node.Ay,current_node.Bx,current_node.By+1,OPERATION_COST,0,current_node);
				heuristic_cost=heuristics(next_node,heuristic_type);
				next_node.path_cost+=current_node.path_cost;
				next_node.astar_cost=next_node.path_cost+heuristic_cost;
				qe.add(next_node);
			}
			//Operation 3 - A goes DOWN, B goes LEFT
			if(isValid(current_node.Ax,current_node.Ay+1) && isValid(current_node.Bx-1,current_node.By))
			{
				next_node = new myNode(current_node.Ax,current_node.Ay+1,current_node.Bx-1,current_node.By,OPERATION_COST,0,current_node);
				heuristic_cost=heuristics(next_node,heuristic_type);
				next_node.path_cost+=current_node.path_cost;
				next_node.astar_cost=next_node.path_cost+heuristic_cost;
				qe.add(next_node);
			}
			//Operation 4 - A goes LEFT, B goes UP
			if(isValid(current_node.Ax-1,current_node.Ay) && isValid(current_node.Bx,current_node.By-1))
			{
				next_node = new myNode(current_node.Ax-1,current_node.Ay,current_node.Bx,current_node.By-1,OPERATION_COST,0,current_node);
				heuristic_cost=heuristics(next_node,heuristic_type);
				next_node.path_cost+=current_node.path_cost;
				next_node.astar_cost=next_node.path_cost+heuristic_cost;
				qe.add(next_node);
			}

		}
		
		//System.out.println("MOVES: "+moves);
		
			if(heuristic_type==1)
			{
				if(goal_reached==1){
					expanded_count[0] = moves;
					//EStack.add(current_node);

					while (current_node.parent!=null)
					{
						EStack.add(current_node);
						current_node = current_node.parent;
						//System.out.println(current_node.Ax+" "+current_node.Ay);
						//EStack.add(current_node);
					}
				}
				else
				{
					expanded_count[0] = 1000;
				}
			}
			if(heuristic_type==2)
			{
				if(goal_reached==1){
					expanded_count[1] = moves;

					while (current_node.parent!=null)
					{
						MStack.add(current_node);
						current_node = current_node.parent;
					}
				}
				else
				{
					expanded_count[1] = 1000;
				}
			}
			if(heuristic_type==3)
			{
				if(goal_reached==1){
					expanded_count[2] = moves;

					while (current_node.parent!=null)
					{
						CStack.add(current_node);
						current_node = current_node.parent;
					}
				}
				else
				{
					expanded_count[2] = 1000;
				}
			}
		
		qe.clear();
		
	}

	private double heuristics(myNode node, double heuristic_type) {
		double heuristic_cost=0;

		if(heuristic_type==1)
		{
			heuristic_cost = Math.sqrt(Math.pow(node.Ax-node.Bx,2)+Math.pow(node.Ay-node.By,2));
		}
		if(heuristic_type==2)
		{
			heuristic_cost = Math.abs(node.Ax-node.Bx)+Math.abs(node.Ay-node.By);
		}
		if(heuristic_type==3)
		{
			heuristic_cost = Math.max(Math.abs(node.Ax-node.Bx),Math.abs(node.Ay-node.By));
		}
		return heuristic_cost;
	}

	public boolean isValid(int x, int y)
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
	
	public static int getMinIndex(int[] numbers){  
		  int minValue = numbers[0];
		  int minIndex=0;
		  for(int i=1;i<numbers.length;i++){  
		    if(numbers[i] < minValue){  
		      minValue = numbers[i];  
		      minIndex = i;
		    }  
		  }  
		  //return minIndex;  
		  return 1;
		}  
	
	public static void printoutputfile(ArrayList<agentGameAStar> obj_list){
		
		myNode current_node = new myNode();
		
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter("output.txt"));
			out.println("\t\t\t\tNodes Expanded");
			out.println("\t\tEuclidean\tManhattan\tChessboard");
			for(int i=0;i<obj_list.size();i++)
			{
				out.println("Input"+(i+1)
						+"\t\t"+(obj_list.get(i).expanded_count[0]==1000?"-":obj_list.get(i).expanded_count[0])
								+"\t\t"+(obj_list.get(i).expanded_count[1]==1000?"-":obj_list.get(i).expanded_count[1])
										+"\t\t"+(obj_list.get(i).expanded_count[2]==1000?"-":obj_list.get(i).expanded_count[2]));
			}
			
			out.println("\nOptimal Path Solution:");
			for(int i=0;i<obj_list.size();i++)
			{
				out.print("Input"+(i+1)+": ");
				int minIndex = getMinIndex(obj_list.get(i).expanded_count);
				if (minIndex==0)
				{
					if(!obj_list.get(i).EStack.isEmpty())
					{
						while (!obj_list.get(i).EStack.isEmpty())
						{
							current_node = obj_list.get(i).EStack.pop();
							out.print("("+current_node.Ax+","+current_node.Ay+") ");
						}
					}
					else
						out.println(-1);
				}
				if (minIndex==1)
				{
					if(!obj_list.get(i).MStack.isEmpty())
					{
						while (!obj_list.get(i).MStack.isEmpty())
						{
							current_node = obj_list.get(i).MStack.pop();
							out.print("("+current_node.Ax+","+current_node.Ay+") ");
						}
					}
					else
						out.println(-1);
				}
				if (minIndex==2)
				{
					if(!obj_list.get(i).CStack.isEmpty())
					{
						while (!obj_list.get(i).CStack.isEmpty())
						{
							current_node = obj_list.get(i).CStack.pop();
							out.print("("+current_node.Ax+","+current_node.Ay+") ");
						}
					}
					else
						out.println(-1);
				}
				out.println("");
			}
			
			out.close();
			
		} catch (IOException e) {
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
	int path_cost;
	double astar_cost;
	myNode parent;

	myNode()
	{
		this(0,0,0,0,0,0,null);			
	} 
	myNode(int Ax, int Ay, int Bx, int By,int path_cost, int astar_cost, myNode p) {
		this.Ax=Ax;
		this.Ay=Ay;
		this.Bx=Bx;
		this.By=By;
		this.path_cost=path_cost;
		this.astar_cost=astar_cost;
		this.parent=p;
		
	}

}

class EdgeCostComparator implements Comparator<myNode>
{
	public int compare(myNode arg0, myNode arg1) {
		if (arg0.astar_cost < arg1.astar_cost)
			return -1;
		if (arg0.astar_cost > arg1.astar_cost)
			return 1;
		else
			return 0;
	}
}
