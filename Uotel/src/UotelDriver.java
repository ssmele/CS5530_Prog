import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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

		int c = 0;
		try {
			con = new Connector();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				displayLogin();
				while ((choice = in.readLine()) == null || choice.length() == 0)
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
					User usr = loginUser(in, con.stmt);
					if (usr != null) {
						applicationDriver(con, in, usr);
					}
					// Case for creating a new account
				} else if (c == 2) {
					User user = registerUser(in, con.stmt);
					if (user != null) {
						applicationDriver(con, in, user);
					}
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
		ArrayList<Reservation> visitCart = new ArrayList<>();
		ArrayList<ResPeriodPair> reservationCart = new ArrayList<>();
		while (true) {
			displayOperations(usr.isAdmin());
			String choice = null;
			while ((choice = in.readLine()) == null || choice.length() == 0)
				;
			int c = 100;
			try {
				c = Integer.parseInt(choice);
			} catch (Exception e) {
				c = 100;
			}
			// Case for statistics
			if (c <= 19 && c >= 7) {
				viewStatistics(c, con, in, usr, reservationCart);
			}
			switch (c) {
			case 0:
				//TODO: need to review reservations, and stays.
				handleLogOut(in, usr, con.con, visitCart, reservationCart);
				// case for logout
				return;
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
				handleStay(usr, in, con, visitCart);
				break;
			case 5:
				// case for browsing
				handleBrowsing(con, in, usr, reservationCart);
				break;
			case 6:
				// Determine two degree separation
				handleSeparation(con, in);
				break;
			case 10:
				// Most trusted.
				if (usr.isAdmin()) {
					handleMostTrusted(in, con.stmt);
					break;
				}
			case 11:
				// Most useful.
				if (usr.isAdmin()) {
					handleMostUseful(in, con.stmt);
					break;
				}
			default:
				System.out.println("Please enter a valid option");
				continue;
			}
		}
	}
	
	/**
	 * This is the handler for when ever a th is selected. Has various operations that a user can do on a th.
	 * @param th
	 * @param con
	 * @param in
	 * @param usr
	 * @param reservationCart
	 * @throws IOException
	 */
	public static void thSelected(TH th, Connector con, BufferedReader in, User usr, ArrayList<ResPeriodPair> reservationCart) throws IOException {
		System.out.println("Currently selected TH: " + th.toString());
		while (true) {
			displayHouseOptions();
			System.out.println("Select an option number or 0 to go back to the list");
			String input = null;
			while ((input = in.readLine()) == null || input.length() == 0)
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
			if (num == 1) {
				handleFavoriteTH(usr, th, in, con.stmt);
			}
			if (num == 2){
				handleViewFeedback(in, th, usr, con);
			}
			if (num == 3){
				handleGiveFeedback(in, th, usr, con.con);
			}
			if (num == 4){
				handleReservation(usr, th, in, con, reservationCart);
			}
			if (num == 5){
				handleMostUsefulFeedback(th, in, con.stmt);
			}

		}
	}

	/***
	 * This method prompts the user to give details of a new TH and creates a
	 * temporary house object out of that information.
	 * 
	 * @param toUpdate
	 * @param in
	 * @return The TH object holding the information of the new TH
	 */
	public static TH gatherUpdates(TH toUpdate, BufferedReader in, Connector con) {
		String response = "";
		String updateValue = null;
		Querys q = new Querys();
		try {
			while (!response.equals("Done")) {
				System.out.println("Updatable Fields:");
				System.out.println("0.Done (When you want to stop updating)");
				System.out.println("1.Category");
				System.out.println("2.Price");
				System.out.println("3.Year_Built");
				System.out.println("4.Name");
				System.out.println("5.Address");
				System.out.println("6.Url");
				System.out.println("7.Phone");
				System.out.println("9.Keywords");
				System.out.println("10. Availabilities");
				System.out.println("Please enter name or number of value you want to update.");
				response = in.readLine();
				switch (response) {
				case "1":
				case "Category":
					// TODO: They may need to select from a list of categories.
					System.out.println("Enter new Category");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 40) {
						System.out.println("Invalid response please try again.");
					}					
					toUpdate.setCategory(updateValue);
					break;
				case "2":
				case "Price":
					int new_price = promptForInt(in, "Enter new price", "Invalid response please try again", 0, Integer.MAX_VALUE, false);
					toUpdate.setPrice(new_price);
					break;
				case "3":
				case "Year_Built":
					System.out.println("Enter new Year_Built");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 
							|| updateValue.length() > 4 || !updateValue.matches("[0-9]+")) {
						System.out.println("Invalid response please try again.");
					}
				    toUpdate.setYear_built((updateValue));
					break;
				case "4":
				case "Name":
					System.out.println("Enter new Name");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 45) {
						System.out.println("Invalid response please try again.");
					}
					toUpdate.setName(updateValue);
					break;
				case "5":
				case "Address":
					System.out.println("Enter new Address");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 75) {
						System.out.println("Invalid response please try again.");
					}
					toUpdate.setAddress(updateValue);
					break;
				case "6":
				case "Url":
					System.out.println("Enter new URL");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 45) {
						System.out.println("Invalid response please try again.");
					}
					toUpdate.setUrl(updateValue);
					break;
				case "7":
				case "Phone":
					System.out.println("Enter new Phone");
					while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 10) {
						System.out.println("Invalid response please try again.");
					}
					toUpdate.setPhone(updateValue);
					break;
					//TODO: SHOULD THIS BE UPDATABLE.
//				case "Date_Listed":
//				case "8":
//					toUpdate.setDate_listed(promptForDate(in));
//					break;
				case "9":
				case "Keywords":
					System.out.println("Current keywords assocaited with this TH:");
					ArrayList<Keyword> keywordList = q.getKeywords(toUpdate, con.stmt);
					System.out.println("0. Back");
					int i;
					for (i = 0; i < keywordList.size(); i++) {
						System.out.println(Integer.toString(i + 1) + ". " + keywordList.get(i).getWord());

					}
					System.out.println(Integer.toString(i+1) + ". Add keyword!");

					// If its empty report to the user that there's nothing for them to do
					// here.
					if (keywordList.isEmpty()) {
						System.out.println("This TH has no keywords assocaited with it yet.");
						break;
					}
					
					int keywordNum = promptForInt(in, "Please enter a valid option.", "Invalid number", 0, keywordList.size() + 1, false);
					
					if(keywordNum == 0){
						break;
					}else if(keywordNum >= 1 && keywordNum < i+1){
						q.deleteKeyword(toUpdate, keywordList.get(--keywordNum), con.stmt);
						break;
					}else if(keywordNum == i+1){
						System.out.println("Please enter a keyword to add to your TH");
						while ((updateValue = in.readLine()) == null || updateValue.length() == 0 || updateValue.length() > 50) {
							System.out.println("Please provide a valid keyword");
						}
						
						// Add keyword given by the user.
						q.addKeywordToHID(updateValue, toUpdate.getHid(), con.stmt);
						break;
					}
				case "10":
				case "Availabilities":
					ArrayList<Period> periods = q.getAvailability(toUpdate, con.stmt);
					System.out.println("0. back");
					i = 0;
					for (i = 0; i < periods.size(); i++){
						System.out.println(i+1 + ". Delete period: " + periods.get(i).toString());
					}
					System.out.println(i + 1 + ". Add new date");
					String prompt = "Go back, select a date to delete, or add new date";
					String error = "Please enter a valid option.";
					int option = promptForInt(in, prompt, error, 0, i+1, false);
					if (option == 0)
						continue;
					else if (option == i + 1){
						System.out.println("Please provide a start and end of this availability.");
						Date from = promptForDate(in, "Start of availability? (YYYY-MM-DD)");
						Date to = promptForDate(in, "End of availability?  (YYYY-MM-DD)");
						int price_per_night = promptForInt(in, "Please provide a price_per_night during this availability", "Invalid entry please try again!", 
													   1,Integer.MAX_VALUE, false);
						Period new_period = new Period(-1, from, to, price_per_night);
						q.insertAvailability(toUpdate, new_period, con);
					}
					else{
						boolean b = q.removeAvailable(con.stmt, toUpdate.getHid(), periods.get(option - 1).getPid());
						if (b)
							System.out.println("You have successfully removed the period: " + periods.get(option - 1).toString());
						else
							System.out.println("Something went wrong processing your request");
					}
					break;
				case "0":
				case "Done":
					System.out.println("Any changes have been updated!");
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
	 * Method used to log users in. Make use of the Querys class to validate login and password.
	 * @param in
	 * @param stmt
	 * @return
	 * @throws IOException 
	 */
	public static User loginUser(BufferedReader in, Statement stmt) throws IOException{
		String login, password;
		System.out.println("please enter login:");
		while ((login = in.readLine()) == null || login.length() == 0)
			;
		System.out.println("please enter a password:");
		while ((password = in.readLine()) == null || password.length() == 0)
			;
		
		Querys q = new Querys(); 
		return q.loginUser(login, password, stmt);
	}
	
	/**
	 * Method used to register a user.
	 * @param in
	 * @param stmt
	 * @return
	 * @throws IOException
	 */
	public static User registerUser(BufferedReader in, Statement stmt) throws IOException{
		//Gathering information 
		String login, password, name, address, phone;
		System.out.println("please enter login:");
		while ((login = in.readLine()) == null || login.length() == 0)
			;
		System.out.println("please enter a password:");
		while ((password = in.readLine()) == null || password.length() == 0)
			;

		System.out.println("please enter your name:");
		while ((name = in.readLine()) == null || name.length() == 0)
			;

		System.out.println("please enter your address:");
		while ((address = in.readLine()) == null || address.length() == 0)
			;

		System.out.println("please enter your phone:");
		while ((phone = in.readLine()) == null || phone.length() == 0)
			;
		
		//Add the user to the database.
		Querys q = new Querys();
		return q.newUser(login, name, password, address, phone, false, stmt);
	}
	
	public static void handleLogOut(BufferedReader in, User user, Connection con, ArrayList<Reservation> visitCart, ArrayList<ResPeriodPair> reservationCart) throws IOException{
		//First show all reservations
		// If they want to remove then remove it from the list. Also have to
		// know remove visit from the list too.
		// Ask user which one
		Querys q = new Querys();
		
		
		if (!reservationCart.isEmpty()) {
			// Purge any bad reservations.
			System.out.println("Do you want to get rid of any reservations before checkout?");
			while (true) {
				int count = 1;
				System.out.println("Reservation # | Reservation information");
				for (ResPeriodPair pair : reservationCart) {
					System.out.println(Integer.toString(count) + ".       | " + pair.getReservation().toString());
					count++;
				}
				
				if(count == 0){
					System.out.println("No more reservations left, lets move onto visits.");
					break;
				}

				int value = promptForInt(in, "Type number of reservation you want to get rid of. If none press 0.",
						"Try again inavlid input", 0, reservationCart.size(), false);
				if (value == 0) {
					break;
				} else {
					reservationCart.remove(--value);
				}
			}
			
			//Gather all the pids from the periods user is making reservation for.
			ArrayList<Integer> pidList = new ArrayList<>();
			for(ResPeriodPair pair: reservationCart){
				pidList.add(pair.getPeriod().getPid());
			}
			
			//Inserting reservations, and deleting available periods so other users can reserve th at same time.
			q.deletePeriods(pidList, con);
			q.insertReservations(user, reservationCart, con);
		}else{
			System.out.println("No reservations to review!");
		}
		
		if (!visitCart.isEmpty()) {
			// Purge any bad visits.
			System.out.println("Do you want to get rid of any visits before checkout?");
			while (true) {
				int count = 1;
				System.out.println("Visit # | Visit information");
				for (Reservation visit : visitCart) {
					System.out.println(Integer.toString(count) + ".       | " + visit.toString());
					count++;
				}
				
				if(count == 0){
					System.out.println("No more visits left, lets finish the checkout.");
					break;
				}
				
				int value = promptForInt(in, "Type number of visit you want to get rid of. If none press 0.",
						"Try again inavlid input", 0, visitCart.size(), false);
				if (value == 0) {
					break;
				} else {
					visitCart.remove(--value);
				}
			}
			
			//Inserting visits
			ArrayList<Reservation> failed = q.insertVisits(user, visitCart, con);
			if (failed.size() > 0){
				System.out.println("The following visits were not successfully documented:");
				for (Reservation res : failed){
					System.out.println(res.toString());
				}
			}
			else if (!visitCart.isEmpty()){
				System.out.println("You have successfully recorded all visists");
			}
			
		}else{
			System.out.println("No visits to review!");
		}
		
		System.out.println("Thank you for using Uotel " + user.getLogin() + "!");
	}
	
	/**
	 * This method retrieves the most trusted users with the help of the querys
	 * class, and then displays them to the admin user.
	 * 
	 * @param in
	 * @param stmt
	 */
	public static void handleMostTrusted(BufferedReader in, Statement stmt) {
		int limit;
		while (true) {
			try {
				// Check to see if the user wants to go back.
				System.out.println("Please provide a limit on the amout of users you want returned.");
				limit = Integer.parseInt(in.readLine());
				if (limit < 1) {
					System.out.println("Please try again limit must be 1 or greater.");
					continue;
				}
				break;
			} catch (Exception e) {
				System.out.println("Please try again with a valid number.");
			}
		}

		Querys q = new Querys();
		ArrayList<String> loginList = q.mostTrusted(limit, stmt);
		System.out.println("Most trusted users:");
		int count = 1;
		for (String login : loginList) {
			System.out.println(Integer.toString(count) + "." + login);
			count++;
		}
		System.out.println("-----------------------------");
		return;
	}

	/**
	 * This method retrieves the most useful users with the help of the querys
	 * class, and then displays them to the admin user.
	 * 
	 * @param in
	 * @param stmt
	 */
	public static void handleMostUseful(BufferedReader in, Statement stmt) {
		int limit;
		System.out.println("What is the max number of houses you would like displayed?");
		while (true) {
			try {
				// Check to see if the user wants to go back.
				limit = Integer.parseInt(in.readLine());
				if (limit < 1) {
					System.out.println("Please try again limit must be 1 or greater.");
					continue;
				}
				break;
			} catch (Exception e) {
				System.out.println("Please try again with a valid number.");
			}
		}

		Querys q = new Querys();
		ArrayList<String> loginList = q.mostUsefulUser(limit, stmt);
		System.out.println("Most useful users:");
		int count = 1;
		for (String login : loginList) {
			System.out.println(Integer.toString(count) + "." + login);
			count++;
		}
		System.out.println("-----------------------------");
		return;
	}
	
	/***
	 * This method obtains two user logins from the user and prints yes or no
	 * if the two users have a 2-degree separation.
	 * @param con
	 * @param in
	 * @throws IOException 
	 */
	public static void handleSeparation(Connector con, BufferedReader in) throws IOException{
		String user1 = null;
		String user2 = null;
		String prompt;
		String error;
		Querys q = new Querys();
		System.out.println("Enter 0 at any time to go back");
		// Get the first user
		while (true){
			prompt = "Please enter the login of the first user";
			error = "Please enter a valid login";
			user1 = promptForString(in, prompt, error, false).trim();
			try{
				if (Integer.parseInt(user1) == 0)
					return;
			}
			catch (Exception e){
			}
			if (q.isValidLog(con.stmt, user1))
				break;
			System.out.println("The login you entered is not in our system!");
			continue;
		}
		// Get the second user
		while (true){
			prompt = "Please enter the login of the second user";
			error = "Please enter a valid login";
			user2 = promptForString(in, prompt, error, false).trim();
			try{
				if (Integer.parseInt(user2) == 0)
					return;
			}
			catch (Exception e){
			}
			if (q.isValidLog(con.stmt, user2))
				break;
			System.out.println("The login you entered is not in our system!");
			continue;
		}
		if (q.twoDegreeSeparated(con.stmt, user1, user2)){
			System.out.println("Yes, " + user1 + " and " + user2 + " are 2-degree separated!");
		}
		else{
			System.out.println("No, " + user1 + " and " + user2 +  " are not 2-degree separated.");
		}
		prompt = "Enter 0 to go back to home, or 1 to try again";
		error = "Please enter either 0 or 1";
		int out = promptForInt(in, prompt, error, 0, 1, false);
		if (out == 0)
			return;
		else{
			handleSeparation(con, in);
			return;
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
	public static void viewStatistics(int choice, Connector con, BufferedReader in, User usr, ArrayList<ResPeriodPair> reservationCart) throws IOException {
		System.out.println("What is the max number of houses you would like displayed per category?");
		Querys q = new Querys();
		while (true) {
			String inNum = null;
			int num = -1;
			while ((inNum = in.readLine()) == null || inNum.length() == 0)
				;
			try {
				num = Integer.parseInt(inNum);
			} catch (Exception e) {
				System.out.println("Please input a valid option");
				continue;
			}
			ArrayList<TH> resultList = new ArrayList<TH>();
			switch (choice) {
			case 7:
				// choice for most popular
				resultList = limitCategoryNum(q.getMostPopular(con.stmt), num);
				viewTHs(resultList, con, in, usr, true, reservationCart);
				return;
			case 8:
				// choice for most expensive
				resultList = limitCategoryNum(q.getMostExpensive(con.stmt), num);
				viewTHs(resultList, con, in, usr, true, reservationCart);
				return;
			case 9:
				// choice for highest rated
				resultList = limitCategoryNum(q.getHighestRated(con.stmt), num);
				viewTHs(resultList, con, in, usr, true, reservationCart);
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
	
	//TODO: implement this thing WOO WOO SWAG!
	public static void handleGiveFeedback(BufferedReader in, TH th, User usr, Connection con) throws IOException{
		// Get the max price value
		String prompt, error, text;
		prompt = "Please enter text describing your stay.";
		error = "Please enter a valid option";
		text = promptForString(in, prompt, error, false);
		
		//Get date for the 
		//TODO: Should this be user inputed?
		Date date = Date.valueOf(LocalDate.now());
		
		int score;
		prompt = "Please enter a score (0 = terrible, 10 = excellent)";
		score = promptForInt(in, prompt, error, 0, 10, false);
	
		//Once we have gather the info send off the request to insert this feedback in the table. 
		Querys q = new Querys();
		q.insertFeedback(usr, th, text, score, date, con);
	}
	
	public static void handleViewFeedback(BufferedReader in, TH th, User usr, Connector con) throws IOException{
		//Gather all feedback for that th.
		Querys q = new Querys();
		ArrayList<Feedback> feedbackList = new ArrayList<>();
		feedbackList = q.getFeedbackTH(th, con.stmt);

		
		while(true){
			// Ask user which one
			int count = 1;
			System.out.println("Feedback # | Feedback information");
			for (Feedback feed : feedbackList) {
				System.out.println(Integer.toString(count) + ".       | " + feed.toString());
				count++;
			}

			// If its empty report to the user that theres nothing for them to do
			// here.
			if (feedbackList.isEmpty()) {
				System.out.println("This TH has no feed back assocaited with it yet.");
				return;
			}
			
			int feedbackNum = promptForInt(in, "If you want to rate one of feedbacks, or trust a user below type number. If you want to continue type 0:", "Invalid number", 0, feedbackList.size(), false);
			
			if(feedbackList.get(--feedbackNum).getLogin() == usr.getLogin()){
				System.out.println("You cannot rate your own feedback!");
				continue;
			}
			
			if(feedbackNum == 0){
				return;
			}
			
			System.out.println("Rate feedback, or Trust user:");
			System.out.println("0. Rating feedback");
			System.out.println("1. Trusting user");
			int ratingOrFeedback = promptForInt(in, "Please provide a valid option", "Invalid choice.", 0, 1, false);
			if(ratingOrFeedback == 0){
				int rating = promptForInt(in, "Providing a rating please. 0-useless, 1-useful, 2-very useful.", "Invalid rating only 0-useless, 1-useful, 1-very useful", 0, 2, false);
				q.insertRating(usr, feedbackList.get(--feedbackNum).getFid(), rating, con.con);
			}else{
				System.out.println("Do you trust this user or not trust this user.");
				System.out.println("0. Not-Trusted");
				System.out.println("1. Trusted");
				int trustedOrNot = promptForInt(in, "Please provide a valid option", "Invalid choice.", 0, 1, false);
				q.trustUser(feedbackList.get(--feedbackNum).getLogin(), usr.getLogin(), (trustedOrNot == 0) ? false : true, con.stmt);
			}
		}

	}

	public static void handleStay(User usr, BufferedReader in, Connector con, ArrayList<Reservation> visitCart) throws IOException{
		//Gather up the unstayedReservations for that particular user.
		Querys q = new Querys();
		ArrayList<Reservation> unstayedReservations = q.getUnstayedReservation(usr, con.stmt);

		// Ask user which one
		int count = 1;
		System.out.println("Reservation # | Reservation information");
		for (Reservation res : unstayedReservations) {
			System.out.println(Integer.toString(count) + ".       | " + res.toString());
			count++;
		}

		//If its empty report to the user that theres nothing for them to do here.
		if (unstayedReservations.isEmpty()) {
			System.out.println("You have no current unfulfilled reservations.");
			return;
		}

		//Get the reservation the user wants to record a stay for.
		int res_num = promptForInt(in, "What reservation do you want to record a stay for? ",
				                          "Invalid reservation number please try again.", 1, unstayedReservations.size(), false);
		Reservation visitedReservation = unstayedReservations.get(--res_num);
		
		visitCart.add(visitedReservation);
	}
	
//TODO: stone, test, 8, 3, 4, 4, 2, 0, 0 casued a bug.
	public static void handleReservation(User usr, TH th, BufferedReader in, Connector con, ArrayList<ResPeriodPair> reservationCart) throws IOException{
		Querys q = new Querys();
		
		//TODO: Still need to remove the availability. 
		//First get dates available
		ArrayList<Period> avaDates = q.getAvailability(th, con.stmt);
		
		//Gather all the pids within the reservation so we don't show periods already in the cart
		HashSet<Integer> pid_list = new HashSet<>();
		for(ResPeriodPair pair : reservationCart){
			pid_list.add(pair.getPeriod().getPid());
		}
		
		//Ask user which one
		int count = 1;
		System.out.println("Period # | From       | To         | Price per night. ");
	 	for(Period p : avaDates){
	 		//We are only going to do this if the pid is not within the reservation cart already.
			if (!pid_list.contains(p.getPid())) {
				System.out.println(Integer.toString(count) + ".       |" + " " + p.getFrom().toString() + " | "
						+ p.getTo().toString() + " | " + Integer.toString(p.getPrice()));
				count++;
			}
		}
		
		if(avaDates.isEmpty()){
			System.out.println("No availabilties for this TH please try another.");
			return;
		}
		
		//Get period user wants to make a reservation for.
		int period_num = promptForInt(in,
				        "What period number do you want to make a reservation for? (Type 0 to go back)",
					    "Period does not exist try again.", 0, avaDates.size(), false);
		if(period_num == 0){
			return;
		}
		Period intended_period = avaDates.get(--period_num);
		
		//Insert into cart.
		Reservation new_res = new Reservation(-1, intended_period.getFrom(), intended_period.getTo(),
				intended_period.getPrice(), usr.getLogin(), th.getHid()); 
		
		//Add it to the cart
		reservationCart.add(new ResPeriodPair(new_res, intended_period));
		
		//Let user know it was a success.
		System.out.println("Your reservation has been added to the cart at " + th.getName() + " during  " 
						  + new_res.getFrom().toString() 
						  + " to " 
				          + new_res.getTo().toString()
				          + " for " + Integer.toString(new_res.getPrice_per_night()) + " a night!");
		System.out.println("Here are a list of THs based on your reservation:");
		
		//Make sure we have suggestions to show.
		ArrayList<TH> suggestedList = q.getSuggestedTHS(con.stmt, th, usr);
		if(suggestedList.isEmpty()){
			System.out.println("No suggested TH's currently so lets continue!");
		}else{	
			viewTHs(suggestedList	, con, in, usr, false, reservationCart);
		}
	}
	
	/**
	 * Handler for when a user wants to favorite a TH.
	 * 
	 * @param usr
	 * @param th
	 * @param in
	 * @param stmt
	 */
	public static void handleFavoriteTH(User usr, TH th, BufferedReader in, Statement stmt) {
		Querys q = new Querys();
		q.favoriteTH(th, usr.getLogin(), Date.valueOf(LocalDate.now()), stmt);
	}

	/**
	 * THis method is used to get the most useful feed back for a certain th.
	 * Makes use of method in the Queries class to do so.
	 * 
	 * @param th
	 * @param in
	 * @param stmt
	 */
	public static void handleMostUsefulFeedback(TH th, BufferedReader in, Statement stmt) {
		int limit;
		System.out.println("What is the max number of Feedback you would like displayed?");
		while (true) {
			try {
				// Check to see if the user wants to go back.
				limit = Integer.parseInt(in.readLine());
				if (limit < 1) {
					System.out.println("Please try again limit must be 1 or greater.");
					continue;
				}
				break;
			} catch (Exception e) {
				System.out.println("Please try again with a valid number.");
			}
		}

		Querys q = new Querys();
		ArrayList<Feedback> feedbackList = q.mostUsefulFeedback(th, limit, stmt);
		System.out.println("Most useful feedback:");
		int count = 1;
		for (Feedback feed : feedbackList) {
			System.out.println(Integer.toString(count) + "." + feed.toString());
			count++;
		}

		if (feedbackList.isEmpty()) {
			System.out.println("No feedback for this particular TH.");
		}
		System.out.println("-----------------------------");
		return;
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
	public static void viewTHs(ArrayList<TH> ths, Connector con, BufferedReader in, User usr, boolean catOrder, ArrayList<ResPeriodPair> reservationCart)
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
				System.out.println(count + ".  name: " + th.getName() + "    price: $" + th.getPrice() + "    address: "
						+ th.getAddress());
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
					System.out.println("Please select a valid house");
					continue;
				}
				if (num == 0)
					return;
				if (num <= 0 || num > ths.size()) {
					System.out.println("Please select a valid house");
					continue;
				} else {
					thSelected(ths.get(num - 1), con, in, usr, reservationCart);
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
		Querys q = new Querys();
		String category, year_built, name, address, phone, url, string_price;
		int price = 0;

		System.out.println("Please enter th category:");
		while ((category = in.readLine()) == null || category.length() == 0 || category.length() > 40) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter year th was built:");
		while ((year_built = in.readLine()) == null || year_built.length() == 0 
				|| year_built.length() > 4 || !year_built.matches("[0-9]+")) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter name of th:");
		while ((name = in.readLine()) == null || name.length() == 0 || name.length() > 45) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter th phone:");
		while ((phone = in.readLine()) == null || phone.length() == 0 || phone.length() > 10) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter th address:");
		while ((address = in.readLine()) == null || address.length() == 0 || address.length() > 75) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter th url:");
		while ((url = in.readLine()) == null || url.length() == 0 || url.length() > 45) {
			System.out.println("Invalid response please try again.");
		}

		System.out.println("Please enter estimated price of th per night:");
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
	 * This method provides functionality for a user to browse THs by a price
	 * range and/or city and/or state and/or categories and/or keywords. The
	 * user then will have the option to sort by price, average rating, or
	 * average rating by trusted users.
	 * 
	 * @param con
	 * @param in
	 * @param usr
	 * @throws IOException
	 */
	public static void handleBrowsing(Connector con, BufferedReader in, User usr, ArrayList<ResPeriodPair> reservationCart) throws IOException {
		int maxPrice = -1;
		int minPrice = -1;
		int sort = -1;
		String city = null;
		String state = null;
		String keyword = null;
		String category = null;
		String prompt = null;
		String error = null;
		ArrayList<String> operations = new ArrayList<String>();
		ArrayList<KeyValuePair> params =new ArrayList<KeyValuePair>();
		ArrayList<KeyValuePair> origParams = new ArrayList<KeyValuePair>();
		System.out.println("We will ask for values and will then be able to reorder "
				+ "the parameters and specify an 'and' or 'or' operation\n");

		// Get the max price value
		prompt = "Please enter a maximum price [no preference]";
		error = "Please enter a valid option";
		maxPrice = promptForInt(in, prompt, error, 0, Integer.MAX_VALUE, true);
		if (maxPrice != -1)
			origParams.add(new KeyValuePair("max price", Integer.toString(maxPrice)));

		// Get the min price value
		prompt = "Please enter a minimum price [no preference]";
		minPrice = promptForInt(in, prompt, error, 0, maxPrice, true);
		if (minPrice != -1)
			origParams.add(new KeyValuePair("min price", Integer.toString(minPrice)));

		// Get the City
		prompt = "Please enter a city [no preference]";
		city = promptForString(in, prompt, error, true);
		if (city != null)
			origParams.add(new KeyValuePair("city", city));

		// Get the State
		prompt = "Please enter a state [no preference]";
		state = promptForString(in, prompt, error, true);
		if (state != null)
			origParams.add(new KeyValuePair("state", state));

		// Get a keyword
		prompt = "Please enter a keyword [no preference]";
		keyword = promptForString(in, prompt, error, true);
		if (keyword != null)
			origParams.add(new KeyValuePair("keyword", keyword));
		// Get a category
		prompt = "Please enter a category [no preference]";
		category = promptForString(in, prompt, error, true);
		if (category != null)
			origParams.add(new KeyValuePair("category", category));
		if (origParams.size() > 1) {
			if (origParams.size() > 2)
				params = getOrdering(in, origParams);
			else
				params = origParams;
			operations = getOperations(in, params);
		}
		// Get sort value
		displayHouseFilters();
		prompt = "Please pick a filter";
		error = "Please choose a valid option";
		sort = promptForInt(in, prompt, error, 1, 4, false);

		Querys q = new Querys();
		ArrayList<TH> retList = q.browse(con.stmt, params, operations, sort);
		viewTHs(retList, con, in, usr, false, reservationCart);
	}

	/***
	 * This method allows for a user to reorder the browsing parameters.
	 * 
	 * @param in
	 * @param original
	 * @return an arraylist of strings containing the users desired ordering of
	 *         browsing parameters.
	 * @throws IOException
	 */
	public static ArrayList<KeyValuePair> getOrdering(BufferedReader in, ArrayList<KeyValuePair> original) throws IOException {
		int count = 1;
		System.out.println("Please input the desired ordering of the parameters you specified (ex. 132)");
		for (KeyValuePair s : original) {
			System.out.println(count + ". " + s.getKey());
			count++;
		}
		while (true) {
			String input = null;
			while ((input = in.readLine()) == null && input.length() == 0)
				;
			input = input.trim();
			try {
				Integer.parseInt(input);
			} catch (Exception e) {
				System.out.println("Please input a valid option");
				continue;
			}
			if (input.length() != original.size()) {
				System.out.println("Too many numbers given. Please try again");
				continue;
			}
			try {
				ArrayList<Integer> retNums = new ArrayList<Integer>();
				char[] nums = input.toCharArray();
				for (char c : nums) {
					int next = c - 49;
					if (next < 0 || next >= original.size() || retNums.contains(next)) {
						System.out.println("Incorrect format. You may not have any "
								+ "duplicate or unspecified numbers. Please try again.");
						Exception e = new Exception();
						throw e;
					}
					retNums.add(next);
				}
				ArrayList<KeyValuePair> retList = new ArrayList<KeyValuePair>();
				for (int i : retNums){
					retList.add(original.get(i));
				}
				return retList;
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	/***
	 * @param in
	 * @param params
	 * @return an array list containing 'and's and/or 'or's
	 * @throws IOException 
	 */
	public static ArrayList<String> getOperations(BufferedReader in, ArrayList<KeyValuePair> params) throws IOException{
		ArrayList<String> ops = new ArrayList<String>();
		for(int i = 0; i < params.size() - 1; i++){
			String prompt = "Which operation would you like between " + params.get(i).getKey() 
			+ " and " + params.get(i+1).getKey() + "? Select 1 or 'AND' and 2 for 'OR'";
			String error = "Please select either 1 or 2";
			int input = promptForInt(in, prompt, error, 1, 2, false);
			if (input == 1)
				ops.add("AND");
			else
				ops.add("OR");
		}
		return ops;
	}

	/**
	 * This method prompts the user for an int using the prompt parameter you
	 * send it. It ensures the value obtained is within a range and not null
	 * (unless specified) or prints the provided the error message and tries
	 * again.
	 * 
	 * @param in
	 * @throws IOException
	 */
	public static int promptForInt(BufferedReader in, String prompt, String error, int min, int max, boolean nullable)
			throws IOException {
		System.out.println(prompt);
		while (true) {
			String tempString = null;
			while ((tempString = in.readLine()) == null)
				;
			if (tempString.trim().length() == 0 && nullable)
				return -1;
			else {
				int retVal = -1;
				try {
					retVal = Integer.parseInt(tempString);
					if (retVal >= min && retVal <= max) {
						return retVal;
					} else {
						System.out.println(error);
						continue;
					}
				} catch (Exception e) {
					System.out.println(error);
					continue;
				}
			}
		}
	}

	/***
	 * Prompts the user for a string value with the provided prompt. If nullable
	 * is specified, a user may enter nothing. If nullable is false and an input
	 * string is empty, gives the user the provided error and tries again.
	 * 
	 * @param in
	 * @param prompt
	 * @param error
	 * @param nullable
	 * @return the input string from the user
	 * @throws IOException
	 */
	public static String promptForString(BufferedReader in, String prompt, String error, boolean nullable)
			throws IOException {
		System.out.println(prompt);
		while (true) {
			String tempString = null;
			while ((tempString = in.readLine()) == null)
				;
			if (tempString.trim().length() == 0 && nullable)
				return null;
			else if (tempString.trim().length() == 0) {
				System.out.println(error);
				continue;
			}
			return tempString;
		}
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

		//Display current TH's to the user.
		System.out.println("Current TH's you have listed:");
		int count = 1;
		for (TH th : currentUsersTH) {
			System.out.println("TH number:" + count + " | " + th.prettyString());
			count++;
		}

		// Get thg user wants to update.
		int index = promptForInt(in, "Please type in the number of the th you want to update: ", 
									 "Invalid selection please try again!", 1, currentUsersTH.size(), false);

		// Get th to update.
		TH thToBeUpdated = currentUsersTH.get(--index);
		System.out.println("Current values of TH you are updating: " + thToBeUpdated.toString());

		thToBeUpdated = gatherUpdates(thToBeUpdated, in, con);
		q.updateTH(thToBeUpdated, con.con);
	}

	/**
	 * Method that gets a proper date out of the user.
	 * @param in
	 * @return
	 */
	public static Date promptForDate(BufferedReader in, String prompt){
		Date userDate = null;
		System.out.println(prompt);

		while (true) {
			try {
				userDate = Date.valueOf(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println("Bad format please try again.");
			}
		}
		return userDate;
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
	public static void displayOperations(boolean isAdmin) {
		System.out.println("      Home     ");
		System.out.println("0. logout and review");
		System.out.println("2. Create a listing");
		System.out.println("3. Alter a listing");
		System.out.println("4. Record a stay");
		System.out.println("5. Search for a house");
		System.out.println("6. Determine two degree seperation");
		System.out.println("7. View most popular houses by category");
		System.out.println("8. View most expensive by category");
		System.out.println("9. View highest rated by category");
		if (isAdmin) {
			System.out.println("10. Top m 'trusted' users.");
			System.out.println("11. Top m 'useful' users.");
		}
	}

	/**
	 * Options for a way to filter search results
	 */
	public static void displayHouseFilters() {
		System.out.println("       Select a Sort       ");
		System.out.println("1. Sort by price");
		System.out.println("2. Highest rated");
		System.out.println("3. Highest rated by trusted users");
		System.out.println("4. No filter");
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
		System.out.println("5. Get most useful feedback");
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
