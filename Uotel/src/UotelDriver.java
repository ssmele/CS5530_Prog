import java.io.*;
import java.sql.Date;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;

public class UotelDriver {

	/**
	 * Starts the Uotel program and acts as the login page.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO: maybe created helper methods for logging in and
		// creating an account.
		System.out.println("        Welcome to the Uotel System     ");
		Connector con = null;
		String choice;
		User current_user = null;

		int c = 0;
		try {
			con = new Connector();
			Querys q = new Querys();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

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
					while ((login = in.readLine()) == null && login.length() == 0)
						;
					System.out.println("please enter a password:");
					while ((password = in.readLine()) == null && password.length() == 0)
						;
					User usr = q.loginUser(login, password, con.stmt);
					if (usr != null) {
						applicationDriver(con, in, usr);
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
						applicationDriver(con, in, user);
					}
					// TODO: handle the case for creating an account
				} else if (c == 3)
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
				}
			}
		}
	}

	/**
	 * This method drives the application once the user has established a
	 * connection and logged in.
	 * 
	 * @param usr
	 * @throws IOException
	 */
	public static void applicationDriver(Connector con, BufferedReader in, User usr) throws IOException {
		while (true) {
			displayOperations();
			String choice = null;
			while ((choice = in.readLine()) == null && choice.length() == 0)
				;
			int c = 100;
			try {
				c = Integer.parseInt(choice);
			} catch (Exception e) {
				c = 100;
			}
			// Case for statistics
			if (c <= 10 && c >= 8) {
				viewStatistics(c, con, in, usr);
			}
			switch (c) {
			case 0:
				// case for logout
				return;
			case 1:
				// Case for reservation
				break;
			case 2:
				// Case for listing
				handleListing(con, in, usr);
				break;
			case 3:
				// case for altering a listing
				handleListingChange(con, in, usr);
				break;
			case 4:
				// case for recording a stay
				break;
			case 5:
				// search for a house
				break;
			case 6:
				// view suggested houses
				break;
			case 7:
				// view similar users
				break;
			default:
				System.out.println("Please enter a valid option");
				continue;
			}
		}
	}

	/***
	 * Determines which statistics to print and how to print per category. This
	 * method prints all of those filtered categories.
	 * 
	 * @param choice
	 * @param con
	 * @param in
	 * @param usr
	 * @throws IOException
	 */
	public static void viewStatistics(int choice, Connector con, BufferedReader in, User usr) throws IOException {
		System.out.println("What is the max number of houses you would like displayed per category?");
		Querys q = new Querys();
		while (true) {
			String inNum = null;
			int num = -1;
			while ((inNum = in.readLine()) == null && inNum.length() == 0)
				;
			try {
				num = Integer.parseInt(inNum);
			} catch (Exception e) {
				System.out.println("Please input a valid option");
				continue;
			}
			ArrayList<TH> resultList = new ArrayList<TH>();
			switch (choice) {
			case 8:
				// choice for most popular
				resultList = limitCategoryNum(q.getMostPopular(con.stmt), num);
				viewTHs(resultList, con, in, usr, true);
				return;
			case 9:
				// choice for most expensive
				resultList = limitCategoryNum(q.getMostExpensive(con.stmt), num);
				viewTHs(resultList, con, in, usr, true);
				return;
			case 10:
				// choice for highest rated
				resultList = limitCategoryNum(q.getHighestRated(con.stmt), num);
				viewTHs(resultList, con, in, usr, true);
				return;
			}
		}
	}

	/***
	 * @param ths
	 * @param all
	 * @return The list of THs limited by number per category
	 */
	public static ArrayList<TH> limitCategoryNum(ArrayList<TH> ths, int max) {

		int count = 0;
		String currentCategory = "";
		ArrayList<TH> retList = new ArrayList<TH>();
		for (TH temp : ths) {
			if (!currentCategory.equals(temp.getCategory())) {
				currentCategory = temp.getCategory();
				count = 1;
				retList.add(temp);
			} else if (count >= max) {
				continue;
			} else {
				count++;
				retList.add(temp);
			}
		}
		return retList;
	}

	public static void thSelected(TH th, Connector con, BufferedReader in, User usr) throws IOException {
		System.out.println(th.toString());
		displayHouseOptions();
		System.out.println("Select an option number or 0 to go back to the list (only back currently works)");
		while (true) {
			String input = null;
			while ((input = in.readLine()) == null && input.length() == 0)
				;
			int num = -1;
			try {
				num = Integer.parseInt(input);
			} catch (Exception e) {
				System.out.println("Please enter a valid option");
				continue;
			}
			if (num == 0)
				return;
		}
	}

	/**
	 * This method is called whenever a list of THs are displayed to the user.
	 * This will allow the user to select one of the THs displayed to them to
	 * either leave a review, make a reservation, or document a stay.
	 * 
	 * @param ths
	 * @param con
	 * @param in
	 * @param usr
	 * @throws IOException
	 */
	public static void viewTHs(ArrayList<TH> ths, Connector con, BufferedReader in, User usr, boolean catOrder)
			throws IOException {
		while (true) {
			System.out.println("      List of Houses      ");
			String category = "";
			int count = 1;
			for (TH th : ths) {
				if (catOrder) {
					if (!category.equals(th.getCategory())) {
						category = th.getCategory();
						System.out.println();
						System.out.println("  " + category);
					}
				}
				System.out.println(count + ". " + th.getName() + " " + th.getPrice());
				count++;
			}
			System.out.println("Enter a house number to view it or 0 to go back");
			while (true) {
				String inNum = null;
				while ((inNum = in.readLine()) == null && inNum.length() == 0)
					;
				int num = -1;
				try {
					num = Integer.parseInt(inNum);
				} catch (Exception e) {
					System.out.println("Please selected a valid house");
					continue;
				}
				if (num == 0)
					return;
				if (num <= 0 || num > ths.size()) {
					System.out.println("Please selected a valid house");
					continue;
				} else {
					thSelected(ths.get(num - 1), con, in, usr);
					break;
				}
			}
		}
	}

	/***
	 * This method handles a user inserting a new TH into the database.
	 * 
	 * @param con
	 * @param in
	 * @param usr
	 * @throws IOException
	 */
	public static void handleListing(Connector con, BufferedReader in, User usr) throws IOException {
		// Gather up information for a new TH. #SWAG
		// TODO: Get price instead of hardcoding it.
		Querys q = new Querys();
		String category, year_built, name, address, phone, url, string_price;
		int price = 0;

		System.out.println("Please enter th category:");
		while ((category = in.readLine()) == null || category.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter year th was built:");
		while ((year_built = in.readLine()) == null || year_built.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter name of TH:");
		while ((name = in.readLine()) == null || name.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter th phone:");
		while ((phone = in.readLine()) == null || phone.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter your address:");
		while ((address = in.readLine()) == null || address.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter your url:");
		while ((url = in.readLine()) == null || url.length() == 0) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter price of TH per night:");
		while ((string_price = in.readLine()) == null || string_price.length() == 0 || price <= 0) {
			try {
				price = Integer.parseInt(string_price);
				break;
			} catch (Exception e) {
				System.out.println("Please provide a number.");
				continue;
			}
		}

		q.newTh(category, year_built, name, address, url, phone, price, usr, con.con);
	}

	/***
	 * This method handles a user wanting to change a listing.
	 * 
	 * @param con
	 * @param in
	 * @param usr
	 * @throws IOException
	 */
	public static void handleListingChange(Connector con, BufferedReader in, User usr) throws IOException {
		// Gather up th's.
		Querys q = new Querys();
		ArrayList<TH> currentUsersTH = q.getUsersTHs(usr.getLogin(), con.stmt);

		// If user has no current th's.
		if (currentUsersTH.isEmpty()) {
			System.out.println("You currently have no TH's to modify please come back at another time.");
			return;
		}

		System.out.println("Current TH's you have listed:");
		int count = 1;
		for (TH th : currentUsersTH) {
			System.out.println("TH number:" + count);
			System.out.println("   With Values: " + th.toString());
			count++;
		}

		// Get thg user wants to update.
		System.out.println("Please type in the number of the th you want to update: ");
		int index = -1;
		while (true) {
			try {
				index = Integer.parseInt(in.readLine());
				if (index > currentUsersTH.size() || index < 1) {
					System.out.println("Please try again invalid th.");
					continue;
				}
				break;
			} catch (Exception e) {
				System.out.println("Please try again invalid th.");
			}
		}

		// Get th to update.
		TH thToBeUpdated = currentUsersTH.get(--index);
		System.out.println("Current values of TH you are updating: " + thToBeUpdated.toString());

		thToBeUpdated = gatherUpdates(thToBeUpdated, in, con.stmt);
		q.updateTH(thToBeUpdated, con.con);
	}

	/***
	 * This method prompts the user to give details of a new TH and creates a
	 * temporary house object out of that information.
	 * 
	 * @param toUpdate
	 * @param in
	 * @return The TH object holding the information of the new TH
	 */
	public static TH gatherUpdates(TH toUpdate, BufferedReader in, Statement stmt) {
		String response = "";
		String updateValue = null;
		Querys q = new Querys();
		try {
			while (!response.equals("Done")) {
				System.out.println("Updatable Fields:");
				System.out.println("1.Category");
				System.out.println("2.Price");
				System.out.println("3.Year_Built");
				System.out.println("4.Name");
				System.out.println("5.Address");
				System.out.println("6.Url");
				System.out.println("7.Phone");
				System.out.println("8.Date_Listed");
				System.out.println("9.Keywords");
				System.out.println("10.Done (When you want to stop updating)");
				System.out.println("Please enter name or number of value you want to update.");
				response = in.readLine();
				switch (response) {
				case "1":
				case "Category":
					// TODO: They may need to select from a list of categories.
					System.out.println("Enter new Category");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setCategory(updateValue);
					break;
				case "2":
				case "Price":
					System.out.println("Enter new Price");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setPrice(Integer.parseInt(updateValue));
					break;
				case "3":
				case "Year_Built":
					System.out.println("Enter new Year_Built");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					if (updateValue.length() < 5) {
						toUpdate.setYear_built((updateValue));
					} else {
						System.out.println("Year value cant be greater than 4 digits.");
					}
					break;
				case "4":
				case "Name":
					System.out.println("Enter new Name");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setName(updateValue);
					break;
				case "5":
				case "Address":
					System.out.println("Enter new Address");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setAddress(updateValue);
					break;
				case "6":
				case "Url":
					System.out.println("Enter new URL");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setUrl(updateValue);
					break;
				case "7":
				case "Phone":
					System.out.println("Enter new Phone");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					if (updateValue.length() < 11) {
						toUpdate.setPhone((updateValue));
					} else {
						System.out.println("Phone value cant be greater than 10  digits.");
					}
					break;
				case "Date_Listed":
				case "8":
					System.out.println("Enter new category");
					while ((updateValue = in.readLine()) == null && updateValue.length() == 0)
						;
					toUpdate.setDate_listed(Date.valueOf(updateValue));
					break;
				case "9":
				case "Keywords":
					System.out.println("Please enter a keyword to add to your TH");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0) {
						System.out.println("Please provide a valid keyword");
					}
					// Add keyword given by the user.
					q.addKeywordToHID(updateValue, toUpdate.getHid(), stmt);
					break;
				case "10":
				case "Done":
					System.out.println("Your changes will now be updated.");
					return toUpdate;
				default:
					System.out.print("Didnt match any updatable values. Please try again.");
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong updating values. Pleas try again.");
			return null;
		}
		return toUpdate;
	}

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
		System.out.println("0. logout");
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
		// TODO: last thing to add is admin abilities
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
	public static void displayHouseOptions() {
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
	public static void displayRatingOptions() {
		System.out.println("       Feedback Options       ");
		System.out.println("1. Trust this user");
		System.out.println("2. Rate this feedback");
		System.out.println("3. Back");
	}

	/**
	 * Option of actions to take on a selected user.
	 */
	public static void displayUserOptions() {
		System.out.println("       User Options        ");
		System.out.println("1. Trust this user");
		System.out.println("2. Back");
	}
}
