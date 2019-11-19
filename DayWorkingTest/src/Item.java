import java.math.BigDecimal;

public class Item {
	private String name;
    private int qty;
    private int price;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public Item(String name, int qty, int price) {
		super();
		this.name = name;
		this.qty = qty;
		this.price = price;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Item [name=" + name + ", qty=" + qty + ", price=" + price + "]";
	}
    
}
