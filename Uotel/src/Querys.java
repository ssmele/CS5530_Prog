import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.PreparedStatement;

import java.sql.Connection;

public class Querys {
	public Querys() {
	}

	public void insertFeedback(User usr, TH th, String text, int score, Date date, Connection con) {

		// First need to do checks to ensure user does nto own the th they are
		// reviewing, and that they have not review this th before.
		String ownershipCheck = "select * from th where th.hid = " + Integer.toString(th.getHid()) + " and th.login = '"
				+ usr.getLogin() + "'";
		String alreadyCheck = "select * from feedback where feedback.login = '" + usr.getLogin()
				+ "' and feedback.hid = " + Integer.toString(th.getHid()) + ";";

		// Checking for ownership.
		ResultSet rs = null;
		int count = 0;
		try {
			rs = con.createStatement().executeQuery(ownershipCheck);
			while (rs.next()) {
				count++;
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + ownershipCheck);
			return;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		if (count > 0) {
			System.out.println("You own this TH you cant leave it feedback.");
			return;
		}

		// Checking to see if this user has already left a review.
		rs = null;
		count = 0;
		try {
			rs = con.createStatement().executeQuery(alreadyCheck);
			while (rs.next()) {
				count++;
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + ownershipCheck);
			return;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		if (count > 0) {
			System.out.println("You have already left feedback at this TH.");
			return;
		}

		// Finally insert the feedback if it has passed all the tests.
		try {
			PreparedStatement insertRating = con.prepareStatement(
					"insert into feedback (text, date, score, login, hid) " + "values (?, ?, ?, ?, ?)");

			insertRating.setString(1, text);
			insertRating.setDate(2, date);
			insertRating.setInt(3, score);
			insertRating.setString(4, usr.getLogin());
			insertRating.setInt(5, th.getHid());

			insertRating.executeUpdate();

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Youve already given feedback !");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}

		System.out.println("Succesfully left feedback on " + th.getName());
	}

	/**
	 * Insert a new ava for a given Th.
	 * 
	 * @param th
	 * @param period
	 * @param con
	 */
	public void insertAvailability(TH th, Period period, Connector con) {
		// First insert period
		// Finally insert the feedback if it has passed all the tests.
		int pid = -1;
		try {
			PreparedStatement insertPeriod = con.con.prepareStatement(
					"insert into period (period.from, period.to) " + "values (?, ?);", Statement.RETURN_GENERATED_KEYS);

			insertPeriod.setDate(1, period.getFrom());
			insertPeriod.setDate(2, period.getTo());

			insertPeriod.executeUpdate();

			ResultSet rs = insertPeriod.getGeneratedKeys();
			rs.next();
			pid = rs.getInt(1);

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("This date overlaps!");
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the period query.");
			return;
		}

		// Make sure we have a valid pid.
		if (pid == -1) {
			System.out.println("Something went wrong please try again.");
			return;
		}

		// Insert into ava the new availability.
		try {
			PreparedStatement insertAvailable = con.con
					.prepareStatement("insert into available " + "values (?, ?, ?);");

			insertAvailable.setInt(1, th.getHid());
			insertAvailable.setInt(2, pid);
			insertAvailable.setInt(3, period.getPrice());

			insertAvailable.executeUpdate();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Youve already declared availabiltiy for this time period.");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}
	}

	/**
	 * Method used to insert ratings into the database.
	 * 
	 * @param user
	 * @param fid
	 * @param rating
	 * @param con
	 */
	public void insertRating(User user, int fid, int rating, Connection con) {
		try {
			PreparedStatement insertRating = con.prepareStatement("insert into rate " + "values (?, ?, ?)");

			insertRating.setString(1, user.getLogin());
			insertRating.setInt(2, fid);
			insertRating.setInt(3, rating);

			insertRating.executeUpdate();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Youve already rated this feedback!");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}
	}

	/**
	 * Takes in given information and persists a TH in the database with given
	 * values.
	 * 
	 * @param category
	 * @param year_built
	 * @param name
	 * @param address
	 * @param url
	 * @param phone
	 * @param price
	 * @param current_user
	 * @param con
	 * @return
	 */
	public TH newTh(String category, String year_built, String name, String address, String url, String phone,
			int price, User current_user, Connection con) {
		try {
			PreparedStatement insertTH = con.prepareStatement(
					"insert into th (category, price, year_built, name, address, url, phone, login, date_listed) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			insertTH.setString(1, category);
			insertTH.setInt(2, price);
			insertTH.setString(3, year_built);
			insertTH.setString(4, name);
			insertTH.setString(5, address);
			insertTH.setString(6, url);
			insertTH.setString(7, phone);
			insertTH.setString(8, current_user.getLogin());
			// Taking system time so user cannot lie. Yes swag love it woo woo
			// swag.
			insertTH.setDate(9, Date.valueOf(LocalDate.now()));

			insertTH.executeUpdate();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something went wrong please try again.");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

		// Return new TH to the user.
		System.out.println("Congrats your TH has been added!");
		return new TH(-1, category, price, year_built, name, address, url, phone, current_user.getLogin(),
				Date.valueOf(LocalDate.now()));

	}

	/**
	 * Gets the current TH's associated with the given user.
	 * 
	 * @param login
	 * @param stmt
	 * @return
	 */
	public ArrayList<TH> getUsersTHs(String login, Statement stmt) {
		String sql = "Select * from th where login = '" + login + "';";

		System.out.println("executing: " + sql);
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();

		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return thList;
	}

	/**
	 * Takes the given TH and updates the TH with the same hid in the database
	 * to the new values given.
	 * 
	 * @param update
	 * @param con
	 * @return
	 */
	public TH updateTH(TH update, Connection con) {
		try {
			PreparedStatement updateTH = con.prepareStatement(
					"update th set category=?, price = ?, year_built = ?, name = ?, address = ?, url = ?, phone = ?, login = ?, date_listed = ?"
							+ "where hid = ?");

			updateTH.setString(1, update.getCategory());
			updateTH.setInt(2, update.getPrice());
			updateTH.setString(3, update.getYear_built());
			updateTH.setString(4, update.getName());
			updateTH.setString(5, update.getAddress());
			updateTH.setString(6, update.getUrl());
			updateTH.setString(7, update.getPhone());
			updateTH.setString(8, update.getLogin());
			updateTH.setDate(9, update.getDate_listed());
			updateTH.setInt(10, update.getHid());

			updateTH.executeUpdate();
			// TODO: DOnt really know what exceptions could get thrown here.
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something got messed up.");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

		return update;
	}

	/**
	 * This method takes the new user information and persists it into the
	 * database. Will then return the new User in a User object. Will return
	 * null if a user already has taken the login.
	 * 
	 * @param login
	 * @param name
	 * @param password
	 * @param address
	 * @param phone
	 * @param user_type
	 * @param stmt
	 * @return
	 */
	public User newUser(String login, String name, String password, String address, String phone, boolean user_type,
			Statement stmt) {

		// Construct beautiful insert statement.
		String sql = "INSERT INTO user " + "VALUES (" + "'" + login + "','" + name + "','" + password + "','" + address
				+ "','" + phone + "'," + user_type + ");";

		// Try and add the user to the database. If an exception is thrown it is
		// most likely user with same login.
		try {
			stmt.executeUpdate(sql);
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("User with specified login already exists please try again!");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

		// Construct a user data object of new info and return it to the user.
		return new User(login, password, user_type);
	}

	/**
	 * This method attempts to find a column in the database where the login and
	 * password match. If it does not find one it will return null. If it does
	 * it will return a user object that has information representing the newly
	 * signed in user.
	 * 
	 * @param login
	 * @param password
	 * @param stmt
	 * @return
	 */
	public User loginUser(String login, String password, Statement stmt) {
		// Construct sql select statement.
		String sql = "select * from user where login = '" + login + "' and password = '" + password + "';";
		ResultSet rs = null;

		// Try and execute the login. If less than one result is returned it
		// means the login and password combination is not
		// present.
		try {
			rs = stmt.executeQuery(sql);
			int count = 0;
			User usr = null;
			while (rs.next()) {
				usr = new User(rs.getString("login"), rs.getString("password"), rs.getInt("user_type") == 1);
				count++;
			}
			rs.close();

			if (count == 1) {
				System.out.println("Correct! You are now signed in as " + login + "!");
				return usr;
			} else {
				System.out.println("Login, and password do not match.");
				return null;
			}
		} catch (Exception e) {
			System.out.println("Couldnt log you in due to connection error. Please try again.");
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return null;
	}

	/**
	 * This method inserts column into trusts relationship table. This
	 * represents that a user trusts or does not trust another user.
	 * 
	 * @param trustee
	 * @param truster
	 * @param trust
	 * @param stmt
	 */
	public void trustUser(String trustee, String truster, boolean trust, Statement stmt) {
		String sql = "insert into trust VALUES (" + "'" + trustee + "','" + truster + "', " + trust + ")";

		try {
			stmt.executeUpdate(sql);
			System.out.println(truster + " now " + (trust ? "does trust " : "doesn't trust") + trustee);
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("You have already given a trust rating on this user.");
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the query.");
			return;
		}
	}

	/**
	 * This method insert column into favorite relationship table. This
	 * represents that a user favorites the given th.
	 * 
	 * @param th
	 * @param login
	 * @param fv_date
	 * @param stmt
	 */
	public void favoriteTH(TH th, String login, Date fv_date, Statement stmt) {
		String sql = "insert into favorite VALUES (" + Integer.toString(th.getHid()) + ",'" + login + "', '"
				+ fv_date.toString() + "')";

		// Execute the insert for the favorites table.
		try {
			stmt.executeUpdate(sql);
			System.out.println(login + " now favorites " + "TH with values " + th.prettyString()
					+ "\n---------------------------------");
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("You already favorite this place. \n---------------------------------");
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the query.");
			return;
		}
	}

	/**
	 * This method takes in a keyword, and hid to associate it with. First adds
	 * the keyword to the database. It will attempt the add every time even if
	 * its already in their as we don't know if its in it already or not. It
	 * will then query for the keyword to get the appropriate wid from it. It
	 * then adds the relationship between keyword and hid to the has_keyword
	 * table.
	 * 
	 * @param keyword
	 * @param hid
	 * @param stmt
	 */
	public void addKeywordToHID(String keyword, int hid, Statement stmt) {
		String insert_keyword_sql = "Insert into keyword (word) VALUES ('" + keyword + "');";
		String select_sql = "Select * from keyword where word = '" + keyword + "';";

		// First make keyword if it doesnt already exist.
		try {
			stmt.executeUpdate(insert_keyword_sql);
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			// System.out.println("keyword already in database.");
		} catch (Exception e) {
			System.out.println("Keyword already exisits query could not be preformed.");
			return;
		}

		// Get the wid now by querying for keyword that equals word.
		ResultSet rs = null;
		int wid = -1;
		// Next key keyword
		try {
			rs = stmt.executeQuery(select_sql);
			while (rs.next()) {
				wid = rs.getInt("wid");
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + select_sql);
			return;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		// First make keyword if it doesn't already exist.
		String insert_has_keyword_sql = "Insert into has_keyword VALUES (" + Integer.toString(hid) + ", "
				+ Integer.toString(wid) + ");";
		try {
			stmt.executeUpdate(insert_has_keyword_sql);
			System.out.println("Successfully added keyword to th. \n-------------------------------------------");
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println(
					"Current TH already has this keyword associated to it. \n-------------------------------------------");
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the query.");
			return;
		}

	}

	/**
	 * This method returns a list of the most trusted users in descending order.
	 * The query used here is quite beautiful and should be admired.
	 * 
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<String> mostTrusted(int limit, Statement stmt) {
		ArrayList<String> loginList = new ArrayList<>();
		String sql = "select login, " + "count(if(is_trusted = True, True, Null)) - "
				+ "count(if(is_trusted = False, True, Null)) as trust_score " + "from user " + "left outer join trust "
				+ "on user.login = trust.trustee_id " + "group by login " + "order by trust_score desc " + "limit "
				+ Integer.toString(limit) + ";";

		// Execute the most trusted user query. For each entry get the login and
		// add it to the list.
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				loginList.add(rs.getString("login"));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return loginList;
	}

	/***
	 * This method takes in the user and the TH they just reserved and finds
	 * suggested THs by finding ones where other visitors have visited it and
	 * the reserved TH. This method returns that list in order of most number of
	 * visits per TH.
	 * 
	 * @param stmt
	 * @param reserved
	 * @param usr
	 * @return an array of suggested THs sorted by number of visits
	 */
	public ArrayList<TH> getSuggestedTHS(Statement stmt, TH reserved, User usr) {
		String sql = "SELECT * FROM th t, " + "(SELECT v2.h_id, COUNT(v2.h_id) AS 'cnt' "
				+ "FROM (SELECT * FROM visit v " + "NATURAL JOIN reserve r " + "WHERE r.login != '" + usr.getLogin()
				+ "') AS v1, " + "(SELECT * FROM visit v " + "NATURAL JOIN reserve r " + "WHERE r.login != '"
				+ usr.getLogin() + "') AS v2 " + "WHERE v1.login = v2.login AND v1.h_id = " + reserved.getHid() + " "
				+ "AND v1.h_id != v2.h_id " + "GROUP BY v2.h_id) AS v " + "WHERE t.hid = v.h_id "
				+ "ORDER BY v.cnt DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return thList;
	}

	/***
	 * @param max
	 * @param stmt
	 * @return A list of the most popular THs grouped by category and ordered by
	 *         the number of visits in descending order.
	 */
	public ArrayList<TH> getMostPopular(Statement stmt) {
		String sql = "select t.category, t.hid, t.name, t.price, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed, "
				+ "COUNT(t.hid) from visit v, reserve r, th t " + "WHERE v.rid = r.rid AND t.hid = h_id "
				+ "GROUP BY t.category, t.hid, t.name, t.price, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed " + "ORDER BY t.category, COUNT(t.hid) DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return thList;
	}

	/***
	 * @param max
	 * @param stmt
	 * @return A list of the most expensive THs grouped by category and ordered
	 *         by price of the TH in descending order.
	 */
	public ArrayList<TH> getMostExpensive(Statement stmt) {
		String sql = "select t.category, t.hid, t.name, t.price, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed " + "from th t "
				+ "ORDER BY t.category, t.price DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return thList;
	}

	/***
	 * 
	 * @param max
	 * @param stmt
	 * @return A list of the highest rated THs grouped by category and ordered
	 *         by the average of all the ratings of the TH in descending order.
	 */
	public ArrayList<TH> getHighestRated(Statement stmt) {
		String sql = "select t.category, t.hid, t.name, t.price, t.address, AVG(f.score), "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed " + "from th t, feedback f "
				+ "where t.hid = f.hid " + "group by t.category, t.hid, t.name, t.price, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed " + "ORDER BY t.category, AVG(f.score) DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return thList;
	}

	/**
	 * This method returns a list of the most useful users in descending order.
	 * The query used here is ok and should be glanced at.
	 * 
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<String> mostUsefulUser(int limit, Statement stmt) {
		ArrayList<String> loginList = new ArrayList<>();
		String sql = "select login, sum(score) " + "from feedback " + "group by login " + "order by sum(score) "
				+ "desc limit " + Integer.toString(limit) + ";";

		// Execute the most trusted user query. For each entry get the login and
		// add it to the list.
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				loginList.add(rs.getString("login"));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return loginList;
	}

	/***
	 * This method provides functionality for a user to browse THs by a price
	 * range and/or city and/or state and/or categories and/or keywords. The
	 * user then will have the option to sort by price, average rating, or
	 * average rating by trusted users.
	 * 
	 * @param stmt
	 * @param max
	 * @param min
	 * @param city
	 * @param state
	 * @param keyword
	 * @param category
	 * @param sort
	 * @return the returned list of THs matching the parameter constraints
	 */
	public ArrayList<TH> browse(Statement stmt, ArrayList<KeyValuePair> params, ArrayList<String> operations,
			int sort) {
		String sql = "SELECT DISTINCT(t.hid), t.category, t.price, t.name, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed " + "FROM th t LEFT OUTER JOIN has_keyword hk "
				+ "ON (t.hid = hk.hid) LEFT OUTER JOIN keyword k " + "ON (hk.wid = k.wid) LEFT OUTER JOIN ";
		// Change query for sorting with only trusted users
		if (sort == 3) {
			sql += "(select * from feedback f, trust tr where f.login = tr.trustee_id AND tr.is_trusted = 1) as f ";
		} else {
			sql += " feedback f ";
		}

		sql += "ON (t.hid = f.hid) ";
		if (params.size() != 0)
			sql += "WHERE ";

		int opIndex = 0;
		for (KeyValuePair param : params) {

			// user wants a max price
			switch (param.getKey()) {
			case "max price":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "t.price <= " + param.getValue() + " ";
				break;

			// user wants to set a min
			case "min price":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "t.price >= " + param.getValue() + " ";
				break;

			// user wants specific city
			case "city":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "t.address LIKE '%" + param.getValue() + "%' ";
				break;
			// user wants specific state
			case "state":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "t.address LIKE '%" + param.getValue() + "%' ";
				break;

			// user wants specific keyword
			case "keyword":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "k.word LIKE '" + param.getValue() + "' ";
				break;

			// user wants specific category
			case "category":
				if (params.indexOf(param) != 0) {
					sql += operations.get(opIndex) + " ";
					opIndex++;
				}
				sql += "t.category LIKE '" + param.getValue() + "' ";
				break;
			default:
				continue;
			}
		}
		sql += "GROUP BY t.hid, t.category, t.price, t.name, t.address, "
				+ "t.year_built, t.url, t.phone, t.login, t.date_listed ";

		// user wants to sort by price
		if (sort == 1) {
			sql += "ORDER BY t.price DESC ";
		}
		// user wants to sort by score (only by trusted handled above with
		// joining
		// a nested sql statement
		else if (sort == 2 || sort == 3) {
			sql += "ORDER BY AVG(f.score) DESC";
		}
		sql += ";";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"), rs.getString("url"),
						rs.getString("phone"), rs.getString("login"), rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return thList;
	}

	/***
	 * This method determines if two users are 2-degree separated.
	 * 
	 * @param stmt
	 * @param log1
	 * @param log2
	 * @return boolean (true if 2-degree separated, false if otherwise)
	 */
	public boolean twoDegreeSeparated(Statement stmt, String user1, String user2) {
		boolean retVal = false;
		String sql = "select f1.hid " + "from favorite f1, favorite f2, favorite f3, favorite f4 "
				+ "WHERE f1.login = '" + user1 + "' " + "AND f4.login = '" + user2 + "' " + "AND f1.hid = f2.hid "
				+ "AND f3.hid = f4.hid " + "AND f2.login = f3.login " + "AND not exists (select f1.hid from "
				+ "favorite f1, favorite f2 " + "WHERE f1.hid = f2.hid " + "AND f1.login = '" + user1 + "' "
				+ "AND f2.login = '" + user2 + "');";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			retVal = rs.next();
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return retVal;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return retVal;
	}

	/***
	 * @param stmt
	 * @param login
	 * @return true if the login belongs to an existing user, false if
	 *         otherwise.
	 */
	public boolean isValidLog(Statement stmt, String login) {
		String sql = "SELECT * FROM user WHERE login = '" + login + "';";
		ResultSet rs = null;
		boolean retVal = false;
		try {
			rs = stmt.executeQuery(sql);
			retVal = rs.next();
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return retVal;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return retVal;
	}

	/**
	 * Method used to retrieve most useful feedback for a particular th.
	 * 
	 * @param selected
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<Feedback> mostUsefulFeedback(TH selected, int limit, Statement stmt) {
		ArrayList<Feedback> feedbackList = new ArrayList<>();
		String sql = "select f.fid, f.date, f.text, f.score, f.login, f.hid from feedback f "
				+ "left outer join rate on rate.fid = f.fid where hid = " + Integer.toString(selected.getHid())
				+ " group by f.fid, f.date, f.text, f.score, f.login, f.hid order by avg(rate.rating) desc limit "
				+ Integer.toString(limit) + ";";

		// Execute the most useful query and then add each feedback
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				feedbackList.add(new Feedback(rs.getInt("fid"), rs.getString("text"), rs.getDate("date"),
						rs.getInt("score"), rs.getString("login"), rs.getInt("hid")));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return feedbackList;
	}

	public void deleteKeyword(TH th, Keyword keyword, Statement stmt) {
		String sql = "delete from has_keyword where has_keyword.wid = " + Integer.toString(keyword.getWid())
				+ " and has_keyword.hid = " + Integer.toString(th.getHid());

		try {
			stmt.executeUpdate(sql);
			System.out.println("Th no longer has '" + keyword.getWord() + "' keyword!");
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Couldnt execute query" + sql);
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the query.");
			return;
		}
	}

	public ArrayList<Keyword> getKeywords(TH th, Statement stmt) {
		ArrayList<Keyword> keywodList = new ArrayList<>();
		String sql = "select * from has_keyword, keyword where has_keyword.hid = " + Integer.toString(th.getHid())
				+ " and has_keyword.wid = keyword.wid;";

		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				keywodList.add(new Keyword(rs.getInt("wid"), rs.getString("word")));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return keywodList;
	}

	/***
	 * Attempts to delete an entry in available.
	 * 
	 * @param stmt
	 * @param hid
	 * @param pid
	 * @return
	 */
	public boolean removeAvailable(Statement stmt, int hid, int pid) {
		String sql = "delete from available where hid = " + hid + " and pid = " + pid + ";";
		try {
			stmt.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public ArrayList<Period> getAvailability(TH th, Statement stmt) {
		ArrayList<Period> periodList = new ArrayList<>();
		String sql = "select * from available, period where available.hid = " + th.getHid()
				+ " and period.pid = available.pid and period.from > sysdate();";

		// Execute the most useful query and then add each feedback
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				periodList.add(new Period(rs.getInt("pid"), rs.getDate("from"), rs.getDate("to"),
						rs.getInt("price_per_night")));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return periodList;
	}

	/**
	 * This method inserts a new reservation into the database with the values
	 * given. Gets hid from the TH, price, from, and to from the Period, and
	 * login from user.
	 * 
	 * @param user
	 * @param th
	 * @param p
	 * @param con
	 * @return
	 */
	public Reservation insertReservation(User user, TH th, Period p, Connection con) {
		try {
			PreparedStatement insertRes = con
					.prepareStatement("insert into reserve (reserve.from, reserve.to, price_per_night, login, h_id) "
							+ "values (?, ?, ?, ?, ?)");

			insertRes.setDate(1, p.getFrom());
			insertRes.setDate(2, p.getTo());
			insertRes.setInt(3, p.getPrice());
			insertRes.setString(4, user.getLogin());
			insertRes.setInt(5, th.getHid());

			insertRes.executeUpdate();
			// TODO: DOnt really know what exceptions could get thrown here need
			// to do more experimenting.
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something went wrong pleas try again.");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

		return new Reservation(-1, p.getFrom(), p.getTo(), p.getPrice(), user.getLogin(), th.getHid());
	}

	public void insertReservations(User user, ArrayList<ResPeriodPair> pairs, Connection con) {
		try {
			PreparedStatement insertRes = con
					.prepareStatement("insert into reserve (reserve.from, reserve.to, price_per_night, login, h_id) "
							+ "values (?, ?, ?, ?, ?)");

			// Uses batches to add all the reservation period pairs.
			for (ResPeriodPair pair : pairs) {
				insertRes.setDate(1, pair.getPeriod().getFrom());
				insertRes.setDate(2, pair.getPeriod().getTo());
				insertRes.setInt(3, pair.getPeriod().getPrice());
				insertRes.setString(4, user.getLogin());
				insertRes.setInt(5, pair.getReservation().getHid());

				insertRes.addBatch();
			}
			insertRes.executeBatch();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something went wrong pleas try again.");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}

		return;
	}

	public void insertVisit(User user, Reservation res, Connection con) {
		try {
			PreparedStatement insertRes = con
					.prepareStatement("insert into visit (from, to, rid) " + "values (?, ?, ?)");

			insertRes.setDate(1, res.getFrom());
			insertRes.setDate(2, res.getTo());
			insertRes.setInt(3, res.getRid());

			insertRes.executeUpdate();
			// TODO: DOnt really know what exceptions could get thrown here need
			// to do more experimenting.
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something went wrong pleas try again.");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}
	}

	public ArrayList<Reservation> insertVisits(User user, ArrayList<Reservation> res, Connection con) {

		ArrayList<Reservation> failed = new ArrayList<Reservation>();
		PreparedStatement insertRes;
		try {
			insertRes = con.prepareStatement("insert into visit (visit.from, visit.to, rid) " + "values (?, ?, ?)");
			// Add all the visits.
			for (Reservation r : res) {
				insertRes.setDate(1, r.getFrom());
				insertRes.setDate(2, r.getTo());
				insertRes.setInt(3, r.getRid());
				try {
					insertRes.executeUpdate();
				} catch (Exception e) {
					failed.add(r);
				}
			}
			return failed;
		} catch (SQLException e1) {
			System.out.println("Something went wrong");
			return null;
		}
	}

	public ArrayList<Reservation> getUnstayedReservation(User user, Statement stmt) {
		String sql = "select * from reserve where rid not in (select rid from visit) and login = '" + user.getLogin()
				+ "'";
		ArrayList<Reservation> resList = new ArrayList<>();

		// Execute the most useful query and then add each feedback
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				resList.add((new Reservation(rs.getInt("rid"), rs.getDate("from"), rs.getDate("to"),
						rs.getInt("price_per_night"), rs.getString("login"), rs.getInt("rid"))));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return resList;
	}

	public ArrayList<Feedback> getFeedbackTH(TH th, Statement stmt) {
		String sql = "select * from feedback where feedback.hid = " + Integer.toString(th.getHid()) + ";";

		ArrayList<Feedback> feedbackList = new ArrayList<>();

		// Execute the most useful query and then add each feedback
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				feedbackList.add((new Feedback(rs.getInt("fid"), rs.getString("text"), rs.getDate("date"),
						rs.getInt("score"), rs.getString("login"), rs.getInt("hid"))));
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute query: " + sql);
			return null;
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}

		return feedbackList;

	}

	/**
	 * This method will take all the pids and delete any periods with those
	 * pids.
	 * 
	 * @param pidList
	 * @param con
	 */
	public void deletePeriods(ArrayList<Integer> pidList, Connection con) {
		try {
			PreparedStatement deletePids = con.prepareStatement("delete from period where pid = ?;");

			// Make a batch statement that will delete all the pids.
			for (Integer pid : pidList) {
				deletePids.setInt(1, pid);

				deletePids.addBatch();
			}

			deletePids.executeBatch();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something went wrong pleas try again.");
			return;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return;
		}
	}
}
