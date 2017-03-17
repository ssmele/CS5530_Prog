import java.sql.Date;

/**
 * 
 * @author Stone Mele and Jordan Hensley
 * 
 * Holds the info for the TH.
 *
 */
public class TH {
	
	private int hid;
	private String category;
	private int price;
	private String year_built;
	private String name;
	private String address;
	private String url;
	private String phone;
	private String login;
	private Date date_listed;
	
	public TH(){
		
	}
	
	public TH(int hid, String category, int price, String year_built, String name, String address, String url, String phone,
			String login, Date date_listed) {
		super();
		this.setHid(hid);
		this.category = category;
		this.price = price;
		this.year_built = year_built;
		this.name = name;
		this.address = address;
		this.url = url;
		this.phone = phone;
		this.login = login;
		this.date_listed = date_listed;
	}
	
	@Override
	public String toString() {
		return "TH [hid=" + hid + ", category=" + category + ", price=" + price + ", year_built=" + year_built
				+ ", name=" + name + ", address=" + address + ", url=" + url + ", phone=" + phone + ", login=" + login
				+ ", date_listed=" + date_listed + "]";
	}

	public String prettyString(){
		return "TH Category=" + category + ", price=" + price
				+ ", TH name=" + name + ", TH address=" + address 
				+ ", TH website=" + url + ", TH phone=" + phone + ", Owner=" + login
				+ ", Date Th was listed=" + date_listed + "]"; 
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getYear_built() {
		return year_built;
	}
	public void setYear_built(String year_built) {
		this.year_built = year_built;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Date getDate_listed() {
		return date_listed;
	}
	public void setDate_listed(Date date_listed) {
		this.date_listed = date_listed;
	}

	public int getHid() {
		return hid;
	}

	public void setHid(int hid) {
		this.hid = hid;
	}
}
