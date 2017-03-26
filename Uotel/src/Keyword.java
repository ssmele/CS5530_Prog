
/**
 * Class that models our keyword structure from our database.
 * @author stone
 *
 */
public class Keyword {
	
	private int wid;
	private String word;
	
	public int getWid() {
		return wid;
	}
	public void setWid(int wid) {
		this.wid = wid;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public Keyword(int wid, String word) {
		super();
		this.wid = wid;
		this.word = word;
	}
}
