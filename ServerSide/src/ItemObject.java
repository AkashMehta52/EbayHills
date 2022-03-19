public class ItemObject implements java.io.Serializable {
    private final String itemName;
    private double minPrice;
    private final String description;
    private String closed;
    private int bidsTilClosed;
    private double instantSellPrice;
    private final String imageURL;

    public ItemObject(String itemName, double minPrice, String description, String closed, int bidsTilClosed, double instantSellPrice, String imageURL){
        this.itemName = itemName;
        this.minPrice = minPrice;
        this.description = description;
        this.closed = closed;
        this.bidsTilClosed = bidsTilClosed;
        this.instantSellPrice = instantSellPrice;
        this.imageURL = imageURL;
    }

    public String getImageURL(){return imageURL;}

    public double getInstantSellPrice(){return instantSellPrice;}

    public int getBidsTilClosed(){return bidsTilClosed;}

    public void updateBids(){bidsTilClosed -= 1;}

    public String getItemName(){
        return itemName;
    }

    public String getProdDescript(){
        return description;
    }

    public double getCurPrice(){
        return minPrice;
    }

    public void updateCurPrice(double bid){
        minPrice = bid;
    }

    public String getClosed(){ return closed; }

    public void setClosed(String val){closed = val;}

    @Override
    public String toString(){
        return itemName;
    }
}
