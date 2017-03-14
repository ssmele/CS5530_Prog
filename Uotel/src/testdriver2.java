import java.io.*;

public class testdriver2 {
	
	/**
	 * Prompt for login page
	 */
	public static void displayLogin() {
		System.out.println("1. Sign in with existing account");
		System.out.println("2. Register with a new account");
		System.out.println("3. Exit");
		System.out.println("Please enter your choice:");
	}
	
	/**
	 * Prompt for home page
	 */
	public static void displayOperations() {
		System.out.println("      Home     ");
		System.out.println("1. Make a reservation");
		System.out.println("2. Create a listing");
		System.out.println("3. Alter a listing");
		System.out.println("4. Record a stay");
		System.out.println("5. Search for a house");
		System.out.println("6. View suggested houses");
		System.out.println("7. View similar users");
		System.out.println("8. View most popular houses by category");
		System.out.println("9. View most expensive by category");
		System.out.println("10. View highest rated by category");
		//TODO: last thing to add is admin abilities
	}
	
	/**
	 * Options for a way to filter search results
	 */
	public static void displayHouseFilters() {
		System.out.println("       Select a Filter       ");
		System.out.println("1. Sort by price: high to low");
		System.out.println("2. Sort by price: low to high");
		System.out.println("3. Highest rated");
		System.out.println("4. Highest rated by trusted users");
		System.out.println("5. No filter");
		System.out.println("6. Back");
	}
	
	/**
	 * Options of actions to take on a selected temporary house.
	 */
	public static void displayHouseOptions(){
		System.out.println("       Listing Options       ");
		System.out.println("1. Mark as favorite");
		System.out.println("2. View feedback");
		System.out.println("3. Give feedback");
		System.out.println("4. Make a reservation");
		System.out.println("5. Record a Stay");
		System.out.println("6. Get most useful feedback");
		System.out.println("7. Back");
	}
	
	/**
	 * Options of actions to take on a selected feedback.
	 */
	public static void displayRatingOptions(){
		System.out.println("       Feedback Options       ");
		System.out.println("1. Trust this user");
		System.out.println("2. Rate this feedback");
		System.out.println("3. Back");
	}
	
	/**
	 * Option of actions to take on a selected user.
	 */
	public static void displayUserOptions(){
		System.out.println("       User Options        ");
		System.out.println("1. Trust this user");
		System.out.println("2. Back");
	}

	public static void main(String[] args) {
		System.out.println("        Welcome to the Uotel System     ");
		Connector con = null;
		String choice;
		User current_user = null;

		int c = 0;
		try {
			con = new Connector();
			Querys q = new Querys();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			while (true) {
				if (current_user == null) {
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
						while ((login = in.readLine()) == null && login.length() == 0)
							;
						System.out.println("please enter a password:");
						while ((password = in.readLine()) == null && password.length() == 0)
							;
						User usr = q.loginUser(login, password, con.stmt);
						if (usr != null){
							current_user = usr;
							handleUser(usr);
						}

						// Case for creating a new account
					} else if (c == 2) {
						String login, password, name, address, phone;
						System.out.println("please enter login:");
						while ((login = in.readLine()) == null && login.length() == 0)
							;
						System.out.println("please enter a password:");
						while ((password = in.readLine()) == null && password.length() == 0)
							;

						System.out.println("please enter your name:");
						while ((name = in.readLine()) == null && name.length() == 0)
							;

						System.out.println("please enter your address:");
						while ((address = in.readLine()) == null && address.length() == 0)
							;

						System.out.println("please enter your phone:");
						while ((phone = in.readLine()) == null && phone.length() == 0)
							;

						User user = q.newUser(login, name, password, address, phone, false, con.stmt);
						if (user != null) {
							current_user = user;
							handleUser(user);
						}
						;
						// TODO: handle the case for creating an account
					} else if (c == 3)
						return;
					else {
						System.out.println("EoM");
						con.stmt.close();

						break;
					}
				} else {
					displayOperations();
					while ((choice = in.readLine()) == null && choice.length() == 0)
						;
					try {
						c = Integer.parseInt(choice);
					} catch (Exception e) {
						continue;
					}
					if (c < 1 | c > 10)
						continue;
					if (c == 2) {
						//Gather up information for a new TH. #SWAG
						//TODO: Get price instead of hardcoding it.
						String category, year_built, name, address, phone, url, price;
						System.out.println("please enter th category:");
						while ((category = in.readLine()) == null && category.length() == 0)
							;
						System.out.println("please enter year th was built:");
						while ((year_built = in.readLine()) == null && year_built.length() == 0)
							;

						System.out.println("please enter name of th.:");
						while ((name = in.readLine()) == null && name.length() == 0)
							;

						System.out.println("please enter th phone:");
						while ((phone = in.readLine()) == null && phone.length() == 0)
							;
						System.out.println("please enter your address:");
						while ((address = in.readLine()) == null && address.length() == 0)
							;

						System.out.println("please enter your phone:");
						while ((url = in.readLine()) == null && url.length() == 0)
							;
						
						q.newTh(category, year_built, name, address, url, phone, 120, current_user, con.con);
					}

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
