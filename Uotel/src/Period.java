import java.sql.Date;

/**
 * This class is kind of a combination of our Period, and Availability models. It adds one field to the period model that is price.
 * Has standard getters and setters.
 * @author stone
 *
 */
public class Period {
	
	private int pid;
	private Date from;
	private Date to;
	private int price;
	
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Period(int pid, Date from, Date to, int price) {
		super();
		this.pid = pid;
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

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public String toString(){
		return "From: " + this.getFrom() + "\tTo: " + this.getTo();
	}
	
	@Override
	public boolean equals(Object o){
		Period p = (Period)o;
		return this.pid == p.pid;
	}
	
	@Override
	public int hashCode() {
	    return 79*this.pid;
	}

}
