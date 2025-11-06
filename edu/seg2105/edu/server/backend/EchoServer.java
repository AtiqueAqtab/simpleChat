package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  private final java.util.concurrent.ConcurrentMap<ConnectionToClient, String> clientToId = new java.util.concurrent.ConcurrentHashMap<>();
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    if(!(msg instanceof String s)) return;
    
    String currentId = (String) client.getInfo("loginID");
    String serverFrom = (currentId == null ? "null" : currentId);
    System.out.println("Message recieved: " + s + " from " + serverFrom + ".");
    
    if(currentId == null) {
    	if(s.startsWith("#login ")) {
    		String id = s.substring(7).trim();
    		if(id.isEmpty()) {
    			sendSafely(client, "ERROR: missing login id; disconnecting.");
    			closeSafely(client);
    			return;
    		}
    		client.setInfo("loginID", id);
    		client.setInfo("loginId", id);
    		clientToId.put(client, id);
    		
    		System.out.println(id + " has logged on.");
    		sendToAllClients(id + " has logged on.");
    		return;
    	} else {
    		sendSafely(client, "ERROR: first message must be #login <id>; disconnecting.");
    		closeSafely(client);
    		return;
    	}
    }
    
    if(s.startsWith("#login ")) {
    	sendSafely(client, "ERROR: already logged in; connection will close.");
    	closeSafely(client);
    	return;
    }
    
    String tagged = currentId + "> " + s;
    System.out.println("[MSG] " + tagged);
    sendToAllClients(tagged);
    
  }
  
  private void sendSafely(ConnectionToClient c, String m) {
	  try { c.sendToClient(m); } catch (Exception ignored) {}
	  
  }
  
  private void closeSafely(ConnectionToClient c) {
	  try { c.close(); } catch (Exception ignored) {}
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass. 
   * Called when a Client is connected.
   * 
   * @param client The connection from the client
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  
	  System.out.println("A new client has connected to the server.");
	  
  }
  
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String id = (String) client.getInfo("loginId");
	  
	  if(id == null) id = (String) client.getInfo("loginId");
	  if(id == null) id = clientToId.get(client);
	  if(id != null) {
		  System.out.println(id + " has disconnected.");
	  } else {
		  System.out.println("A client has disconnected");
	  }
	  clientToId.remove(client);
	  
  }
  
  @Override
  protected void clientException(ConnectionToClient client, Throwable exception) {
      
      System.out.println("[CLIENT ERROR] " + client + " -> " + exception);

      
      super.clientDisconnected(client);               
      System.out.println(client + " has disconnected");
  }

  
  
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    new ServerConsole(sv).accept();
  }
}
//End of EchoServer class
