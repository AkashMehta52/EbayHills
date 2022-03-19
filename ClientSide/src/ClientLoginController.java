import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ClientLoginController {
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    public Client myClient;

//    public ClientLoginController(ObjectInputStream reader, ObjectOutputStream writer){
//        this.reader = reader;
//        this.writer = writer;
//    }
    public void setIO(ObjectOutputStream writer, ObjectInputStream reader){
        this.writer = writer;
        this.reader = reader;
    }
    @FXML
    private Button loginButton;

    @FXML
    private Label wrongLogin;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button signUpButton;

    @FXML
    private Button guestSignIn;

    @FXML
    public void handleGuestSignIn(ActionEvent event) throws Exception{
        Client m = new Client();
        writer.writeObject("guest");
        //writer.flush();
        clientUser user = new clientUser("guest", "guest");
        writer.writeObject(user);
        writer.flush();
        wrongLogin.setText("Success!");
        myClient.changeScene("mainPage.fxml", user);
    }

    @FXML
    public void handleLoginButton(ActionEvent event) throws Exception {
        writer.writeObject("login");
        writer.flush();
        //SceneBuilderClient m = new SceneBuilderClient();
        clientUser user = new clientUser(username.getText(), password.getText());
        writer.writeObject(user);
        //reader.readObject();
        String success = null;
        synchronized (reader){
            success = (String) reader.readObject();
        }
        if(success.equals("true")){
            wrongLogin.setText("Success!");
            myClient.changeScene("mainPage.fxml", user);
        }
        else{
            wrongLogin.setText("Incorrect Username or Password");
        }
    }

    @FXML
    public void handleSignUpButton(ActionEvent event) throws Exception {
        writer.writeObject("signup");
        clientUser user = new clientUser(username.getText(), password.getText());
        writer.writeObject(user);
        writer.flush();
        synchronized(reader) {
            if ((boolean) reader.readObject()) {
                wrongLogin.setText("Account already exists! Please login!");
            } else {
                wrongLogin.setText("Account made, sign in now!");
            }
        }
        //writer.flush();
    }



}


