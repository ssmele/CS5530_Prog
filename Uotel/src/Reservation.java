import java.sql.Date;

public class Reservation {
	
	private int rid;
	private Date from;
	private Date to;
	private int price_per_night;
	private String login;
	private int hid;
	
	
	public Reservation(int rid, Date from, Date to, int price_per_night, String login, int hid) {
		super();
		this.rid = rid;
		this.from = from;
		this.to = to;
		this.price_per_night = price_per_night;
		this.login = login;
		this.hid = hid;
	}
	
	@Override
	public String toString() {
		return "Reservation [rid=" + rid + ", from=" + from + ", to=" + to + ", price_per_night=" + price_per_night
				+ ", login=" + login + ", hid=" + hid + "]";
	}

	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public int getPrice_per_night() {
		return price_per_night;
	}
	public void setPrice_per_night(int price_per_night) {
		this.price_per_night = price_per_night;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public int getHid() {
		return hid;
	}
	public void setHid(int hid) {
		this.hid = hid;
	}
}
