


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class Prey implements Player


{
	static int wallCounter=0;
	int maxMoves=4;
	Point oldHunterPos;
	Point newHunterPos = new Point(0,0);
	Point futureHunterPos;
	static int [][] board = new int [300][300];
	

	Point[] directions ={new Point(1,1),new Point(-1,-1),new Point(-1,1),new Point(1,-1),
			
	                     new Point(1,0),new Point(-1,0),  new Point(0,1),new Point(0,-1)};   
	
	

	
	
  public Prey()
  
  {
	for (int i=0;i<300;i++)
	for (int j=0;j<300;j++)
		board[i][j]=0;
	  
  }

  public String makeMove (Point hunterPos, Point preyPos)
  {

	//goal: maximize distance from [current position of hunter + direction x 10 steps ]

//	heuristic for leaf: number of available moves + distance from target
	  oldHunterPos = newHunterPos;
	  newHunterPos= hunterPos;
	  
	  Point direction= getdirection(oldHunterPos,newHunterPos);
	
	  futureHunterPos = translatePoint(hunterPos,direction,10);  
	String nextBestMove = nextBestMove(preyPos);
	  System.out.println(nextBestMove);
	  return nextBestMove;
	
  }

	private String nextBestMove(Point preyPos)
	
	{
		Point bestpos=null;
		int score=0;
		int bestscore=0;
		for (Point direction:directions)
		{
			Point newpos= translatePoint(preyPos,direction);
			score = scoreMove(newpos ,0,bestscore);
			if (score>bestscore)
			{
		      	bestscore=score;
		      	bestpos =  newpos;
		        	
			}
		}

	 return convertPointtoDireciton(bestpos);
	}
	
	private int scoreMove( Point pos,  int moveCount, int bestScore)

	{

		//check that pos is valid
	    if (!isMoveValid(pos))
	           return Integer.MIN_VALUE;
		int bestpos=0;
		int newbestscore = Math.max(bestScore,getdistance(pos,futureHunterPos));
		if (moveCount == maxMoves){
			return newbestscore;
		}
		int score;
	for (Point direction:directions)
		{
			Point newpos = translatePoint(pos,direction); 
			score = scoreMove(newpos, moveCount+1, newbestscore);
			if (score>newbestscore)
			{	newbestscore=score;
			}
		}

	return newbestscore; 

	}



	private boolean isMoveValid(Point pos)
	 {
		 return pos.x<300 && pos.x>=0 && pos.y<300 & pos.y>=0; // && board[pos.x][pos.y]==wallCounter;
	 }


	  
	  Point translatePoint(Point newPoint, Point direction)
	 
	  {
		return    translatePoint(newPoint, direction, 1);
	  }
	  Point translatePoint(Point newPoint, Point direction, int factor)
	  {
	   newPoint.translate(factor * direction.x, factor * direction.y);
	  
	  if (newPoint.y>299)
		 newPoint.y=299;
	  if (newPoint.y<0);
	      newPoint.y=0;
			 
	     if (newPoint.x>299)
	 	 newPoint.x=299;
	 	  if (newPoint.x<0);
	 	   newPoint.x=0;
	 			    
	 return newPoint;     
  
	  
  }
  
	 private Point getdirection(Point oldHunterPos,Point newHunterPos)
	  {
		int x,y;
		x=oldHunterPos.x< newHunterPos.x?1:-1;
		y=oldHunterPos.y< newHunterPos.y?1:-1;
		
		return new Point(x,y);
	  }
	
	  int getdistance(Point p1, Point p2)
	  
	  {
		  return Math.abs(p1.x-p2.x) + Math.abs(p1.y-p2.y);
       }
	  
	  
	  public void updateWalls (Object wallsObject)
	  {
		  Wall[] walls = (Wall[]) wallsObject;
		wallCounter++;
		
		 for (Wall wall:walls)
			 
		 {
		
			 Point w = wall.pos;
			
		  switch (wall.direction) {
			  case S:
				  for (int i=w.y;i<w.y+wall.length;i++)
					  board[w.x][i]=wallCounter;
			  break;
			  case N:
				  for (int i=w.y;i>w.y-wall.length;i--)
					  board[w.x][i]=wallCounter;
		      break;
			  case W:
				  for (int i=w.x;i>w.x-wall.length;i--)
					  board[i][w.y]=wallCounter;
			  break;
			  case E: 
				  for (int i=w.y;i<w.y+wall.length;i++)
					  board[w.x][i]=wallCounter;
			  break;
		  }
	  }
	  }

	
	private  String convertPointtoDireciton (Point dir)

	{
    String retVal="";

	if (dir.y==1)
		retVal+="S";
	else if (dir.y==-1)
		retVal+="N";

   if (dir.x==1)
		retVal+="E";
	else if (dir.x==-1)
		retVal+="W";

    return  "{\"command\": \"M\",\"direction\":\"" + retVal +"\"}";
	}
	  

}


class Wall {
public Wall(directions direction, int length, Point pos) {
		this.direction=direction;
		this.length=length;
		this.pos=pos;
	}
public Point pos;
public int length;
public directions direction; 

}

