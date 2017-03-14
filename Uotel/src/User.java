
/**
 * 
 * @author Stone Mele and Jordan Hensley
 * 
 * Holds the necesssary info to track a user's credentials throughout
 * using uotel.
 *
 */
public class User {
	
	private String login;
	private String password;
	private boolean isAdmin;
	
	public User(String login, String password, boolean isAdmin){
		this.login = login;
		this.password = password;
		this.isAdmin = isAdmin;
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public boolean isAdmin(){
		return this.isAdmin;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public void setIsAdmin(boolean isAdmin){
		this.isAdmin = isAdmin;
	}
}
