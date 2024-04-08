package lab.poly.lab6_and103.models;

public class Users {
    private String _id, username, password, email, name, avartar;
    private Boolean available;

    public Users() {
    }

    public Users(String _id, String username, String password, String email, String name, String avartar, Boolean available) {
        this._id = _id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.avartar = avartar;
        this.available = available;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
