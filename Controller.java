


import java.awt.Point;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class Controller 
{
	private static CountDownLatch messageLatch;
	
	private static String SENT_MESSAGE = "";
	
	private static String publisherEndpoint = "ws://localhost:1990",
				   		  hunterEndpoint = "ws://localhost:1991",
				   		  preyEndpoint = "ws://localhost:1992";
	
	static Point hunterPos=new Point(0,0);
	static Point preyPos=new Point(230,200);
	static Player player;
	
	static int maxWalls;
	
	static int timetoWall;
	//  ArrayList<wall> myWalls = new ArrayList<wall>();
	    
    public static void main( String[] args )
    {   
    	
    	
    	String endPoint;
    	if (args[0].equals("H"))
    	{
    		player = new Hunter(timetoWall,maxWalls);
    	    endPoint =  hunterEndpoint;}
    	
    	else
    	{
    		player= new Prey();
    		 endPoint = preyEndpoint;}
    	
    	timetoWall = Integer.parseInt(args[1]);
    	maxWalls = Integer.parseInt(args[2]);
		Session session;
        try {
            messageLatch = new CountDownLatch(100);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            ClientManager client2 = ClientManager.createClient();
            
            Endpoint playersocket = new Endpoint() {


				@Override
				public void onOpen(Session session, EndpointConfig arg1) {
				//	try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            public void onMessage(String message) {
                                System.out.println("Received message: "+message);
                                process(message);
                               
                                messageLatch.countDown();
                            }
                        });

//                    }
					
				}
            };
            
            
            Endpoint publisher = new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig arg1) {
				//	try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            public void onMessage(String message) {
                                System.out.println("Received message: "+message);
                                //System.out.println("Prey.java pos:" + getpreypos(message));
                                messageLatch.countDown();
                            }
                        });
                      //  SENT_MESSAGE = getPositionsCommand();
                   //     session.getBasicRemote().sendText(SENT_MESSAGE);
                    //    SENT_MESSAGE = getWallsCommand();
                     //   session.getBasicRemote().sendText(SENT_MESSAGE);
                   // } catch (IOException e) {
                  //      e.printStackTrace();
                  //  }

				}
            };

            
         	    session=   client.connectToServer(playersocket, cec, new URI(endPoint));
           


            client2.connectToServer(publisher, cec, new URI(publisherEndpoint));
            messageLatch.await(100, TimeUnit.SECONDS);


			while (true)
			{
				SENT_MESSAGE = getPositionsCommand();
				try {
					session.getBasicRemote().sendText(SENT_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}

				SENT_MESSAGE = player. makeMove(hunterPos,preyPos);
				try {
					session.getBasicRemote().sendText(SENT_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}




            
        } catch (Exception e) {
            e.printStackTrace();
        }




        
    }
    
    
    private static void process(String message)
      
    {
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode actualObj=null;
    	try{
    	 actualObj = mapper.readTree(message);}
    	
    	catch (JsonProcessingException e)
    	{} 
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String cmd = actualObj.get("command").toString().replace("\"", "");
    	if (cmd.equals("P"))
    		{	Integer[] array;
				try {
					array = mapper.readValue(actualObj.get("hunter").toString(), Integer[].class);
					hunterPos.x=array[0];
		    		hunterPos.y=array[1];
		    		array = mapper.readValue(actualObj.get("prey").toString(), Integer[].class);
					preyPos.x=array[0];
		    		preyPos.y=array[1];	
		    		
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		
    		
		  	//    preyPos = actualObj.get("prey").toString();}
    
    		}
    	
    	else if (cmd.equals("NOT WORKING"))
    	{

			String wallsStr = actualObj.get("walls").toString();
			TypeFactory typeFactory = mapper.getTypeFactory();
			try {
				List<newWall> walls =
					    mapper.readValue( wallsStr, typeFactory.constructCollectionType(List.class, newWall.class));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			
			
			//  Wall[] walls = getWalls(wallsStr);
			player.updateWalls(wallsStr);
		
    	}	    		
    	
    }
       
    
    static Wall[] getWalls (String wallStr)
    
    {ObjectMapper mapper = new ObjectMapper();
    	String[] tmp = wallStr.split(",");
    	Wall[] walls = new Wall[tmp.length];
    	JsonNode actualObj=null;
    	for (int i=0;i<walls.length;i++)
    		
    	{
    	
    	
    	 try {
			actualObj =  mapper.readTree(tmp[i]);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	directions direction;
    	if(actualObj.get("direction").toString().equals("[-1,0]"))
    		direction = directions.W;
    	else if (actualObj.get("direction").toString().equals("[1,0]"))
    			direction = directions.E;
    	else if (actualObj.get("direction").toString().equals("[0,-1]"))
    			direction = directions.S;
    			else
    				direction = directions.N;

    	int length = Integer.parseInt(actualObj.get("length").toString());
    	Integer[] intarr;
    	
		try {
			intarr = mapper.readValue(actualObj.get("position").toString(), Integer[].class);
			Point position = new Point (intarr[0],intarr[1]);
	    	walls[i] = new Wall (direction, length, position);	
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    	}
    	return walls;
    }
    public static String getPositionsCommand() {
    	return runCommand("P");
    }
    
    public static String getWallsCommand() {
    	return runCommand("W");
    }
    
    
    public static String makeMoveCommand()
    {
    return runCommand("M");
    }
    
    public static String makeBuildCommand()
    {
     return runCommand("B");
    }
    public static String runCommand(String command) {
    	String action = "";
    	ObjectMapper mapper = new ObjectMapper();
    	ObjectNode node = mapper.createObjectNode();
        node.put("command", command); 
        try {
			action = mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
    }
}

class newWall {
public newWall(int[] direction, int length, int[] pos) {
		this.direction=direction;
		this.length=length;
		this.pos=pos;
	}
public int[] pos;
public int length;
public int[] direction; 
public int id;
}
