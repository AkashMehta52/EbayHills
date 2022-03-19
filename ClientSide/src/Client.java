import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Author: Akash Mehta
 * This code is my blood sweat and tears. It not only compiles, but works. I am so proud of this code.
 */
public class Client extends Application {

    ClientLoginController controller;
    ClientMainController mainController;
    ObjectInputStream reader;
    ObjectOutputStream writer;
    private static ArrayList<clientUser> userInfo = new ArrayList<>();
    private static Stage stg;

    public static ArrayList<clientUser> getUserInfo(){
        return userInfo;
    }

    public static boolean containsUser(clientUser user){
        if(userInfo.contains(user)){
            return true;
        }
        return false;
    }
    public static void updateUserInfo(clientUser newUser){
        userInfo.add(newUser);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stg = primaryStage;
        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("loginSplash.fxml").openStream());
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Log-In");
        primaryStage.setScene(new Scene(root, 763, 524));
        primaryStage.show();
        controller.myClient = this;
        //customer = new Customer("", "");
        connectToServer();
        controller.setIO(writer, reader);
    }

    public void changeScene(String fxml, clientUser user) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent pane = fxmlLoader.load(getClass().getResource(fxml).openStream());
        mainController = fxmlLoader.getController();
        mainController.setIO(writer, reader);
        Stage stage = (Stage) stg.getScene().getWindow();
        stage.setScene(new Scene(pane));
        stage.show();
        stg = stage;
        //stage.setUserData(user);
        mainController.setUserInfo(user);
        mainController.initializeItems();
        Thread readerThread = new Thread(new IncomingReader()); // see Canvas's Chat for IncomingReader class
        readerThread.start();
        //connectToServer();
    }

    void connectToServer () {
        int port = 5000;
        try {
            Socket sock = new Socket("localhost", port);
            writer = new ObjectOutputStream(sock.getOutputStream());
            reader = new ObjectInputStream(sock.getInputStream());
            System.out.println("networking established");
        } catch (IOException e) {}
    }

    //ClientLoginController getController () { return controller; }

    public static void main(String[] args){
        launch(args);
    }

    class IncomingReader implements Runnable {
        public void run() {

            //mainController.setIO(writer, reader);
            ArrayList<ItemObject> updateItems = new ArrayList<>();
            String message;
            try {
                while(true){
                    message = (String) reader.readObject();
                    switch(message){
                        case "Update":
                            updateItems.addAll((ArrayList<ItemObject>) reader.readObject());
                            System.out.println(updateItems);

                            Platform.runLater(new Runnable(){
                                @Override public void run(){
                                    mainController.updateitems(updateItems);
                                }
                            });
                            break;

                        case "HistoryUpdate":
                            //Popup popup = new Popup();
                            ArrayList<String> bidLog = (ArrayList<String>) reader.readObject();
                            System.out.println(bidLog);
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            Parent pane = fxmlLoader.load(getClass().getResource("popUpWindow.fxml").openStream());
                            popUpController cont = fxmlLoader.getController();


                            Platform.runLater(new Runnable(){
                                @Override public void run(){
                                    Stage stage = new Stage();
                                    stage.setTitle("Bidding Log");
                                    stage.setScene(new Scene(pane));
                                    cont.setBidLogList(bidLog);
                                    stage.showAndWait();
                                }
                            });


                    }
                        //mainController.refreshScreen();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

