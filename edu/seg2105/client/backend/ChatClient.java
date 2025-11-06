// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private final String loginId;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginId) 
    
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
  }
  
  public String getLoginId() { return loginId; }

  
  //Instance methods ************************************************
    
  /**
	 * Implemented hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try { 
			sendToServer("#login " + loginId);
		} catch (Exception e) {
			clientUI.display("Failed to send login: " + e.getMessage());
		}
	}
	
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if(message.startsWith("#")) {
  		handleCommand(message);
  		return;
	  }
    try
    {
    	sendToServer(message);
      
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) {
	  String[] parts = command.split("\\s+");
	  String cmd = parts[0];
	  
	  if(cmd.equals("#quit")) {
		  
		  quit();
		  
	  } else if (cmd.equals("#logoff")) {
		  
		  try {
			closeConnection();
		  } catch (IOException ignored) {
			  
		  }
		  
		  
	  } else if (cmd.equals("#sethost")) {
		  
		  if (isConnected()) {
              clientUI.display("ERROR: sethost only allowed while logged off.");
          } else if (parts.length < 2) {
        	  
              clientUI.display("Usage: #sethost <host>");
              
          } else {
        	  
              setHost(parts[1]);
              clientUI.display("Host set to " + getHost());
              
          }
		  
		  
	  } else if (cmd.equals("setPort")) {
		  
		  if (isConnected()) {
			  
			  clientUI.display("ERROR: setport only allowed while logged off.");
			  
		  } else if (parts.length < 2) {
			  
			  clientUI.display("Usage: #setport <port>");
			  
		  } else {
			  
			  try {
				  int p = Integer.parseInt(parts[1]);
				  setPort(p);
				  clientUI.display("Port set to " + getPort());
			  } catch (NumberFormatException e) {
				  clientUI.display("ERROR: port must be an integer.");
			  }
			  
		  }
		  
	  } else if (cmd.equals("#login")) {
		  if(isConnected()) {
			  clientUI.display("Error: you are already connected");
		  } else {
			  try {
				  openConnection();
				  clientUI.display("Connected to " + getHost() + ":" + getPort());
				  
			  } catch (Exception e) {
				  clientUI.display("Error: could not connect: " + e);
			  }
		  }
		  
		  
		  
	  } else if (cmd.equals("#gethost")) {
		  
		  clientUI.display("Host: " + getHost());
		  
	  } else if (cmd.equals("#getport")) {
		  
		  clientUI.display("Port: " + getPort());
		  
	  } else {
		  
		  clientUI.display("Unknown command: " + command);
		  
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		
		clientUI.display("The server is shut down");
		quit();
		
	}
  	
	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		
  		clientUI.display("Connection closed");
  		
	}
}
//End of ChatClient class
