
/**
 * Very simple tuple class used in various places around this project.
 * @author stone
 *
 */
public class KeyValuePair {
	public String key;
	public String value;
	
	public KeyValuePair(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return key;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void setValue(String value){
		this.value = value;
	}
}
