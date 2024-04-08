package lab.poly.lab6_and103.models;

public class Fruit {

    private String _id, name;
    private int price;
    private String image;


    public Fruit() {
    }

    public Fruit(String _id, String name, int price, String image) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
