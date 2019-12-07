package gameNet;

import java.net.*;
import java.io.*;


class GamePlayerProcess2 extends Thread {
    GameServer mom;
    Socket sock;
    ObjectInputStream in = null;
    int myIndex;

    GamePlayerProcess2(Socket s, GameServer m, int index)
    {
        sock = s;
        mom =m;
        myIndex = index;
    }
 
    

    public void run()
    {
       // Read from socket and put the string read into all message queues for
       // all conversations.
       
       try {
            
            in = new ObjectInputStream(sock.getInputStream());
            Object inputObj;          
   
            while ((inputObj = in.readObject()) != null) // Read from socket 
            {
                    mom.putInputMsgs(inputObj);             
            } // end of while loop           
           } 
       catch (ClassNotFoundException e)
       {
           System.out.println("GamePlayerProcess2.run Class Not Found Err: " + e);
           e.printStackTrace(System.out);
       }
       catch (Exception e) 
           {
                System.out.println("GamePlayerProcess2.run Err: " + e);
                //e.printStackTrace();
           }
       try
       { // I'm annoyed that I need try ... catch to keep the compiler happy here
                
                in.close();
                sock.close(); 
                System.out.println("GamePlayerProcess2.run terminating: " +myIndex);
                
                mom.removeMe(myIndex); // just remove me
       } catch (Exception e)
       {
    	   e.printStackTrace();
       }    
             
        
    }// end of run routine
}


