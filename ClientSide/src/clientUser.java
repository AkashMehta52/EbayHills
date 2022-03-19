public class clientUser implements java.io.Serializable{
    private String username;
    private String password;

    public clientUser(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return username;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof clientUser){
            clientUser toCompare = (clientUser) o;
            if(this.username.equals(toCompare.getUsername()) && this.password.equals(toCompare.getPassword())){
                return true;
            }
        }
        return false;
    }

}
