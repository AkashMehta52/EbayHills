import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;


public class ClientMainController {
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    public Client myClient;
    public ArrayList<ItemObject> items;
    public ArrayList<String> itemNames;
    //public clientUser curClient;

    public void setIO(ObjectOutputStream writer, ObjectInputStream reader){
        this.writer = writer;
        this.reader = reader;
    }

    public void initializeItems() throws IOException, ClassNotFoundException {
        itemNames = new ArrayList<>();
        synchronized (reader){
            items = (ArrayList<ItemObject>) reader.readObject();
        }
        for(ItemObject item: items){
            itemNames.add(item.getItemName());
        }
        System.out.println(itemNames);
        ObservableList<ItemObject> itemName = FXCollections.observableList(items);
        itemSelector.setItems(itemName);

    }

    public void updateitems(ArrayList<ItemObject> sent){
        items = sent;
        ItemObject selectedVal = (ItemObject) itemSelector.getValue();
        if(selectedVal == null){return;}
        ObservableList<ItemObject> itemName = FXCollections.observableList(items);
        itemSelector.setItems(itemName);
        for(int i = 0 ; i < sent.size(); i++){
            if(selectedVal.getItemName().equals(sent.get(i).getItemName())){
                selectedVal = sent.get(i);
            }
        }
        itemImage.setImage(new Image(selectedVal.getImageURL()));
        itemSelector.setValue(selectedVal);
        highestBidVal.setText(String.valueOf(selectedVal.getCurPrice()));
        minBidVal.setText(String.valueOf(selectedVal.getCurPrice()));
        itemDescription.setText(selectedVal.getProdDescript());
        curItemLabel.setText(selectedVal.getItemName());
        if(selectedVal.getClosed() != null){
            soldLabel.setText(selectedVal.getClosed());
        }
        instantBuyField.setText(Double.toString(selectedVal.getInstantSellPrice()));
    }
    public void setUserInfo(clientUser user){
        userInfo.setText("Hello User: " + user.getUsername());
    }

    @FXML
    public void handleSelectButton(ActionEvent event){
       ItemObject selectedVal = (ItemObject) itemSelector.getValue();
       if(selectedVal == null){
           return;
       }
       if(selectedVal.getClosed() != null){
            soldLabel.setText(selectedVal.getClosed());
       }
       else{
           soldLabel.setText(null);
       }

       itemImage.setImage(new Image(selectedVal.getImageURL()));
       instantBuyField.setText(Double.toString(selectedVal.getInstantSellPrice()));
       highestBidVal.setText(String.valueOf(selectedVal.getCurPrice()));
       minBidVal.setText(String.valueOf(selectedVal.getCurPrice()));
       itemDescription.setText(selectedVal.getProdDescript());
       curItemLabel.setText(selectedVal.getItemName());
       ObservableList<ItemObject> itemName = FXCollections.observableList(items);
       itemSelector.setItems(itemName);
    }


    @FXML
    public void handleItemSelector(MouseEvent action){
//        initializeItems();
          ObservableList<ItemObject> itemName = FXCollections.observableList(items);
          itemSelector.setItems(itemName);
    }

    @FXML
    public void handlePlaceButton(ActionEvent event) throws IOException {
        if(itemSelector.getValue() == null){
            bidValidation.setText("Please select an item!");
        }
        else{
            ItemObject selectedVal = (ItemObject) itemSelector.getValue();
            if(selectedVal.getClosed() != null){
                bidValidation.setText("Item is sold, cannot bid!");
            }
            else {
                try {
                    Double val = Double.parseDouble(userBidVal.getText());
                    if (val <= selectedVal.getCurPrice()) {
                        bidValidation.setText("Bid must be higher than current!");
                    } else {
                        if (val >= selectedVal.getInstantSellPrice()) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Congratulations!");
                            alert.setHeaderText(null);
                            alert.setContentText("Congratulations! You've won the bid for this item!");
                            alert.showAndWait();
                        } else if (selectedVal.getBidsTilClosed() - 1 == 0) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Congratulations!");
                            alert.setHeaderText(null);
                            alert.setContentText("Congratulations! You've won the bid for this item!");
                            alert.showAndWait();
                        }
                        bidValidation.setText("Success!");
                        writer.writeObject("Validate");
                        writer.writeObject(selectedVal);
                        writer.writeObject(val);
                    }

                } catch (NumberFormatException | IOException e) {
                    bidValidation.setText("Please enter numerical value");
                }
            }
        }
    }
    @FXML
    public void handleSignOut(ActionEvent event) throws IOException {
        writer.writeObject("SignOut");
        System.exit(0);
    }

    @FXML
    public void handleHistoryButton(ActionEvent event) throws IOException {
        writer.writeObject("History");

    }
    @FXML
    private ComboBox<ItemObject> itemSelector;

    @FXML
    private Button selectItemButton;

    @FXML
    private TextField highestBidVal;

    @FXML
    private TextField userBidVal;

    @FXML
    private TextField curItemLabel;
    @FXML
    private Button placeButton;

    @FXML
    private Label bidValidation;

    @FXML
    private TextField minBidVal;

    @FXML
    private Button historyButton;

    @FXML
    private Button signOutButton;

    @FXML
    private Label userInfo;

    @FXML
    private TextArea itemDescription;

    @FXML
    private Label soldLabel;

    @FXML
    private TextField instantBuyField;

    @FXML
    private ImageView itemImage;

}
