import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sun.security.ntlm.Client;

/*
 * Author: Vallath Nandakumar and the EE 422C instructors.
 * Data: April 20, 2020
 * This starter code assumes that you are using an Observer Design Pattern and the appropriate Java library
 * classes.  Also using Message objects instead of Strings for socket communication.
 * See the starter code for the Chat Program on Canvas.  
 * This code does not compile.
 */
public class Server extends Observable {

    static Server server;
    private static ArrayList<clientUser> users = new ArrayList<>();
    private ArrayList<ItemObject> itemsList;
    private Gson gson = new Gson();
    private static ArrayList<String> bidLog = new ArrayList<>();

    public static ArrayList<clientUser> getUserInfo(){
        return users;
    }

    public static boolean containsUser(clientUser user){
        if(users.contains(user)){
            return true;
        }
        return false;
    }
    public static void updateUserInfo(clientUser newUser){
        users.add(newUser);
    }

    public static void main (String [] args) {
        server = new Server();
        server.populateItems();
        server.SetupNetworking();
    }

    private void SetupNetworking() {
        int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while(true) {
                Socket clientSocket = ss.accept();
                //ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                Thread t = new Thread(new ClientHandler(clientSocket, writer, in));
                t.start();

                System.out.println("got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateItems(){
        try {
            JsonReader j  = new JsonReader(new FileReader(System.getProperty("user.dir") + "/src/initialItems.json"));
            itemsList = gson.fromJson(j, new TypeToken<ArrayList<ItemObject>>() {}.getType());
        } catch(Exception e){
        }
    }
    class ClientHandler implements Runnable {
        private ObjectInputStream reader;
        private ClientObserver writer;
        //private ClientObserver COwriter; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;

        public ClientHandler(Socket clientSocket, ClientObserver COwriter, ObjectInputStream reader) {
            this.writer = COwriter;
            this.reader = reader;
            this.clientSocket = clientSocket;
        }

        public void run() {
            clientUser currentUser = null;
            boolean setFlag = false;
			while(!setFlag){
                String type = null;
                try {
                    synchronized (reader){
                        type = (String)reader.readObject();
                        //writer.writeObject(true);
                        clientUser user = (clientUser) reader.readObject();
                        switch (type) {
                            case "login":
                                if (users.contains(user)) {
                                    synchronized (writer) {
                                        writer.writeObject("true");
                                    }
                                    currentUser = user;
                                    setFlag = true;
                                }
                                else{
                                    System.out.println("FUCK");
                                    synchronized (writer) {
                                        writer.writeObject("false");
                                    }
                                }
                                break;
                            case "signup":
                                if (users.contains(user)) {
                                    synchronized (writer) {
                                        writer.writeObject(true);
                                    }
                                } else {
                                    users.add(user);
                                    synchronized (writer){
                                        writer.writeObject(false);
                                    }
                                    //setFlag = true;
                                    //System.out.println("poopoo");

                                }
                                break;
                            case "guest":
                                setFlag = true;
                                currentUser = user;
                                break;
                            }
                        }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            addObserver(writer);
            System.out.println(countObservers());
            System.out.println("fuck yeah! Now for the rest of this thread logic bullcrap LOL");
            try {
                //setChanged();
                //notifyObservers(itemsList);
                writer.writeObject(itemsList);
                while(true){
                    String message = (String) reader.readObject();
                    if(!message.equals("Update")) {
                        System.out.println(message);
                        switch (message) {
                            case "Validate":
                                ItemObject toCheck = (ItemObject) reader.readObject();
                                Double value = (Double) reader.readObject();
                                //clientUser bidClient = (clientUser) reader.readObject();
                                //ArrayList<ItemObject> pleaseWork = new ArrayList<>();
                                synchronized(server) {
                                    for (int i = 0; i < itemsList.size(); i++) {
                                        if (itemsList.get(i).getItemName().equals(toCheck.getItemName())) {
                                            itemsList.get(i).updateBids();
                                            if(value >= toCheck.getInstantSellPrice()){
                                                itemsList.get(i).updateCurPrice(value);
                                                itemsList.get(i).setClosed("Sold to User: (" + currentUser.getUsername() + ") for $" + itemsList.get(i).getCurPrice());
                                                bidLog.add("User: (" + currentUser.getUsername() + ") bought " +itemsList.get(i).getItemName() + " for " + itemsList.get(i).getCurPrice());
                                            }
                                            else if(itemsList.get(i).getBidsTilClosed() == 0){
                                                itemsList.get(i).updateCurPrice(value);
                                                itemsList.get(i).setClosed("Sold to User: (" + currentUser.getUsername() + ") for $" + itemsList.get(i).getCurPrice());
                                                bidLog.add("User: (" + currentUser.getUsername() + ") bought " +itemsList.get(i).getItemName() + " for " + itemsList.get(i).getCurPrice());
                                            }
                                            else {
                                                itemsList.get(i).updateCurPrice(value);
                                                bidLog.add("User: (" + currentUser.getUsername() + ") bid on " + itemsList.get(i).getItemName() + " for $" + itemsList.get(i).getCurPrice());
                                            }
                                            System.out.println(bidLog);
                                            setChanged();
                                            notifyObservers("Update");
                                            setChanged();
                                            notifyObservers(itemsList);
                                            break;
                                        }
                                    }
                                }
                                System.out.println("Success?");
                                // if(value < )
                                break;
                            case "SignOut":
                                deleteObserver(writer);
                                System.out.println("Deleting Observer, left = " + countObservers());
                                break;

                            case "History":
                                synchronized(server) {
                                    writer.writeObject("HistoryUpdate");
                                    writer.writeObject(bidLog);
                                    break;
                                }

                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
            }
        }
    } // end of class ClientHandler


}
