package edu.seg2105.edu.server.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.ConnectionToClient;

public class ServerConsole implements ChatIF {

	private final EchoServer server;
	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public ServerConsole(EchoServer server) {
		this.server = server;
	}
	
	@Override
	public void display(String message) {
		
		System.out.println("SERVER MESSAGE> " + message);
		
	}
	
	public void accept() {
		while(true) {
			String line;
			try {
				line = in.readLine();
				if(line == null) break;
				
			} catch(IOException e) {
				display("Server console I/0 error: " + e.getMessage());
				break;
			}
			
			if(line.startsWith("#")) {
				handleServerCommand(line.trim());
			} else {
				display(line);
				server.sendToAllClients("SERVER MSG> " + line);
			}
		}
	}
	
	public void handleServerCommand(String command) {
		String[] parts = command.split("\\s+");
		String cmd = parts[0];
		
		if(cmd.equals("#quit")) {
			try {
				server.close();
			} catch(Exception ignored) { }
			
			display("Server exiting...");
			System.exit(0);
		} else if (cmd.equals("#stop")) {
			server.stopListening();
			
		} else if (cmd.equals("#close")) {
			try {
				server.close();
			} catch (Exception e) {
				display("ERROR closing server: " + e.getMessage());
			}
		} else if (cmd.equals("#setport")) {
			if (parts.length < 2) {
				display("Usage: #setport <port>");
			} else if (server.isListening() || server.getNumberOfClients() > 0) {
				display("Error: setport only allowed when server is closed.");
			} else {
				try {
					int p = Integer.parseInt(parts[1]);
					server.setPort(p);
					display("Port set to " + server.getPort());
				} catch (NumberFormatException e) {
					display("Error: port must be an integer.");
				}
			}
		} else if(cmd.equals("#start")) {
			if (server.isListening()) {
				display("Error: server already listening.");
			} else {
				try {
					server.listen();
					System.out.println("Server listening for connections on port " + server.getPort());
				} catch(Exception e) {
					display("Error: could not start listening: " + e);
				}
			}
		} else if(cmd.equals("getport")) {
			display("Port: " + server.getPort());
		} else display("Unknown server command: " + command);
		
	}
	
	
	

}
