import java.sql.Date;

public class Period {
	
	private Date from;
	private Date to;
	private int price;
	
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Period(Date from, Date to, int price) {
		super();
		this.from = from;
		this.to = to;
		this.price = price;
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

}
