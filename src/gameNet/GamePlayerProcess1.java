package gameNet;

import java.net.*;
import java.io.*;
import java.util.*;


class GamePlayerProcess1 extends Thread {
    private Socket socket = null;
    int myIndex;
    boolean continueFlag = true;
    ObjectOutputStream out=null;
    GameServer mom;
    LinkedList<Object> msgObjects= new LinkedList<Object>(); 
    
    synchronized void stopGamePlayer()
    {
        continueFlag = false;
        notify(); // wake up thread waiting for a message
        try{
            socket.close();
            }catch (IOException e){}
    }
    
    // Add Object to list for this GameControl Player Process
    synchronized void put(Object ob)
    {
        msgObjects.add(ob);
        notify(); // wake up Thread waiting for this message
    }
    
    // Either return a String from the list or wait for 
    // a new message to enter the queue for this Conversation
    
    synchronized Object get()
    {
        Object retval;
        while (continueFlag)
        {
            if (msgObjects.size() > 0)
            {
                retval = msgObjects.removeFirst();
                return retval;
            }
            else
            {
                try 
                {
                    wait();
                }catch (InterruptedException e){}
            }
        }
        return null;
    }

    GamePlayerProcess1(Socket s, GameServer m, int me) 
    {
       socket = s;
       myIndex= me;
       mom =m; // Mother GameServer Task
    }

    public void run()
    {
        // Start up a thread to read from the socket and then
        //   put it into all of the Message Queues for all 
        //   Chat conversations. 
        
        GamePlayerProcess2 proc2 = new GamePlayerProcess2(socket, mom, myIndex);
        proc2.start();

       try {
            out = new ObjectOutputStream(socket.getOutputStream());          

            Object outputOb;
   
            // Loop to pull messages out of the Queue and then
            // write them to the Socket. 
            
            while ((outputOb = get()) != null) // Pull message from Queue 
            {
               out.writeObject(outputOb); 
               out.reset();

              // if (out.checkError()) break;
            }    
            socket.close();        
           } 
       catch (Exception e) 
           {
              System.out.println("GamePlayerProcess1 Err: " + e);
           }
       finally 
           {
             System.out.println("GamePlayerProcess1.run Terminating : " + myIndex);
             try
             { // I'm annoyed that I need try ... catch to keep the compiler happy here
                out.close();
                socket.close(); 
                                
             } catch (Exception e){
                System.out.println("GamePlayerProcess1.run Exception closing sockets :"+e);
             }          
           }
    }// end of run routine
}


