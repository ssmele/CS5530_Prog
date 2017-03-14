import java.io.*;

public class testdriver2 {
	/**
	 * @param args
	 */
	public static void displayLogin() {
		System.out.println("        Welcome to the Uotel System     ");
		System.out.println("1. Sign in with existing account");
		System.out.println("2. Register with a new account");
		System.out.println("3. Exit");
		System.out.println("Please enter your choice:");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Uotel Login");
		Connector con = null;
		String choice;

		int c = 0;
		try {
			con = new Connector();
			Querys q = new Querys();
			//q.loginUser("stone", "test", con.stmt);
			//q.newUser("swag", "stone", "swag", "ues", "sone", true, con.stmt);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			while (true) {
				displayLogin();
				while ((choice = in.readLine()) == null && choice.length() == 0)
					;
				try {
					c = Integer.parseInt(choice);
				} catch (Exception e) {
					continue;
				}
				if (c < 1 | c > 3)
					continue;
				// Case for logging in
				if (c == 1) {
					String login, password;
					System.out.println("please enter login:");
					while ((login = in.readLine()) == null
							&& login.length() == 0)
						;
					System.out.println("please enter a password:");
					while ((password = in.readLine()) == null
							&& password.length() == 0)
						;
					User usr = q.loginUser(login, password, con.stmt);
					if (usr != null)
						handleUser(usr);

					// Case for creating a new account
				} else if (c == 2) {
					String login, password, name, address, phone;
					System.out.println("please enter login:");
					while ((login = in.readLine()) == null
							&& login.length() == 0)
						;
					System.out.println("please enter a password:");
					while ((password = in.readLine()) == null
							&& password.length() == 0)
						;
					
					System.out.println("please enter your name:");
					while ((name = in.readLine()) == null
							&& name.length() == 0);
					
					System.out.println("please enter your address:");
					while ((address = in.readLine()) == null
							&& address.length() == 0);
					
					System.out.println("please enter your phone:");
					while ((phone = in.readLine()) == null
							&& phone.length() == 0);
					
					User user = q.newUser(login, name, password, address, phone, false, con.stmt);
					if(user != null){
						handleUser(user);
					};
					//TODO: handle the case for creating an account
				}else if (c == 3)
					return;
				else {
					System.out.println("EoM");
					con.stmt.close();

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.closeConnection();
				}

				catch (Exception e) {
					/* ignore close errors */}
			}
		}
	}

	public static void handleUser(User usr) {
		// Here we will handle the user once they are logged in
	}
}
