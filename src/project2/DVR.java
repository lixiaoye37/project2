package project2;

import java.net.*;
import java.util.*;
import java.io.*;


public class DVR {
    
    public static void main(String[] args) {
        boolean start = false;
        boolean checker = true;
        
        Scanner input = new Scanner(System.in);
        String args1 = input.nextLine();
        args = args1.split(" ") ;
        
        
        String init = "";
        String dashTArg = args[1];
        String filename = args[2];
        System.out.print(filename);
        Server.routingTables = new HashMap<>();
        
        Server.readFile(filename);
        int myPort = Integer.parseInt(Server.neighbors.get(Server.myID).port);
        
        String dashIArg = args[3];
        int routUpd = Integer.parseInt(args[4]);
        
        do {
            if (args[0].equals("server")) {
                try {
                    if (!dashTArg.equals("-t")) {
                        checker = false;
                    } else if (!dashIArg.equals("-i")) {
                        checker = false;
                    } else if (routUpd <= 0) {
                        checker = false;
                    }
                    
                    if (checker == true) {
                        start = true;
                    }
                } catch (Exception e) {
                    System.out.println(
                                       "Please enter command \"server -t <topology-file-name> -i <routing-update-interval>\" to begin.");
                }
            } else {
                System.out.println(
                                   "Please enter command \"server -t <topology-file-name> -i <routing-update-interval>\" to begin.");
            }
        } while (!start);
        
        Server server = new Server(myPort);
        String command = "";
        server.setupListeningSocket();
        
        Server.interval(routUpd);
        
        
        do {
            boolean success = false;
            
            System.out.print("\nProvide a command: ");
            
            Scanner secondInput = new Scanner(System.in);
            command = secondInput.nextLine();
            
            String[] values = command.split(" ");
            
            
            if (command.contains("update")) { 
                System.out.println("value is length: " + values.length + " for string '" + command + "'");
                if(values.length < 4) {
                    System.out.println("Enter the right amount of arguments");
                }
                else {
                    try {
                        int cost = 0;
                        int server1 = Integer.parseInt(values[1]);
                        int server2 = Integer.parseInt(values[2]);
                        if(values[3].equals("inf") ){
                            cost = Integer.MAX_VALUE;
                        } else{
                            cost = Integer.parseInt(values[3]); 
                        }
                        Server.update(server1, server2, cost);
                        success = true;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (command.equals("step")) { 
                Server.updateAll();
                Server.packets++;
                success = true;
            } else if (command.equals("packets")) { 
                System.out.println("You have received " + Server.packets + " packets since the last update.");
                Server.packets = 0;
                success = true;
            } else if (command.equals("display")) {
                Server.display();
                success = true;
            } else if (command.contains("disable")) { 
                if(values.length < 2) {
                    System.out.println("Enter the right amount of arguments");
                }
                else {
                    int disabledServer = Integer.parseInt(values[1]);
                    Server.disable(disabledServer);
                    success = true;
                }
                
            } else if (command.contains("crash")) { 
                for(int neigh : Server.edges.keySet() ){
                    Server.edges.put(neigh, Integer.MAX_VALUE);
                }
                Server.updateAll();
                
                Server.crash();
                Server.isCrashed = true;
                success = true;
            } else if (command.contains("exit")) { 
                Server.crash();
                success = true;
                System.out.println("Program now exiting...");
                
            } else {
                System.out.println("Command '" + values[0] + "' not recognized!");
            }
            
            if(success == false){
                System.out.println(values[0] + ": FAIL!");
            }
            else{
                System.out.println(values[0] + ": SUCCESS!");
            }
            
            
        } while (!command.equals("exit"));
        if (command.equals("exit")) { 
            System.exit(0);
        }
    }
}
