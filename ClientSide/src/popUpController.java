import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class popUpController {
    @FXML private ListView<String> bidLogList;

    public void setBidLogList(ArrayList<String> bids){
        ObservableList<String> bidLogs = FXCollections.observableList(bids);
        bidLogList.setItems(bidLogs);
    }
}
