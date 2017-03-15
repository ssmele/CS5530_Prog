import java.sql.Date;

public class Feedback {
	
	private int fid;
	private String text;
	private Date date;
	private int score;
	private String login;
	private int hid;
	
	@Override
	public String toString() {
		return "Feedback [fid=" + fid + ", text=" + text + ", date=" + date + ", score=" + score + ", login=" + login
				+ ", hid=" + hid + "]";
	}
	
	public Feedback(int fid, String text, Date date, int score, String login, int hid) {
		super();
		this.fid = fid;
		this.text = text;
		this.date = date;
		this.score = score;
		this.login = login;
		this.hid = hid;
	}
	
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
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
