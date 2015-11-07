
import apple.laf.JRSUIConstants;

import java.awt.Point;

public class Hunter implements Player {
int TimeToWall;
Point prevHunterPos=new Point(0,0);
int counter=0;
int maxWalls;
int wallCount=0;
	directions direction;
	public Hunter(int timeToWall, int maxWalls)
	{ this.TimeToWall=timeToWall;
	  this.maxWalls = maxWalls;
	}

	public String makeMove(Point hunterPos, Point preyPos) {
		// TODO Auto-generated method stub
		counter++;

        //Get direction

		if (hunterPos.x>prevHunterPos.x && hunterPos.y>prevHunterPos.y )
		  direction = directions.SE;

		else if(hunterPos.x>prevHunterPos.x && hunterPos.y<prevHunterPos.y )
		  direction = directions.NE;

		else if(hunterPos.x<prevHunterPos.x && hunterPos.y<prevHunterPos.y )
		  direction = directions.NW;
		else
         direction = directions.SW;


		prevHunterPos= hunterPos;

		int distance =  (int )hunterPos.distance(preyPos);

    if (distance<100 && counter<=TimeToWall)
		{
		counter=0;

		if ((hunterPos.x>preyPos.x && hunterPos.y>preyPos.y && direction==directions.NW)
              ||
          (hunterPos.x<preyPos.x && hunterPos.y<preyPos.y && direction==directions.SE)
            ||
            (hunterPos.x<preyPos.x && hunterPos.y>preyPos.y && direction==directions.NE)
             ||
            (hunterPos.x>preyPos.x && hunterPos.y<preyPos.y && direction==directions.SE))
		{
		return getCreateWall("V");

		}

		else if ((hunterPos.y>preyPos.y && (direction==directions.NW  || direction==directions.NE )
			||

			(hunterPos.y<preyPos.y && (direction==directions.SW  || direction==directions.SE ))))
		{
						return getCreateWall("H");
		}






		 {
		 }
		}
		return "{\"command\":\"M\"}";
	}








    public String getCreateWall(String wallType) {
		this.wallCount++;
    	return "{\"command\":\"B\", \"wall\":  {\"direction\":\"" + wallType + "\"}}";
    }

	public void updateWalls(Object walls) {
		// TODO Auto-generated method stub

	}


}

