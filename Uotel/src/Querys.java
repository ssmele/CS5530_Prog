import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.PreparedStatement;

import java.sql.Connection;

public class Querys {
	public Querys() {
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
			insertTH.setDate(9, Date.valueOf(LocalDate.now()));

			insertTH.executeUpdate();
			// TODO: DOnt really know what exceptions could get thrown here need
			// to do more experimenting.
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Pleas provide a valid login.");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

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

		System.out.println("executing " + sql);
		try {
			stmt.executeUpdate(sql);
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("User with specified login already exists please try again!");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

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
		System.out.println("executing " + sql);
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
		System.out.println("Executing:" + sql);

		try {
			stmt.executeUpdate(sql);
			System.out.println(truster + " now " + (trust ? "does " : "dosn't ") + trustee);
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("One of the user logins provided does not exist.");
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
		// System.out.println("Executing:" + sql);

		try {
			stmt.executeUpdate(sql);
			System.out.println(login + " now favorites " + "TH with values " + th.toString()
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
	public ArrayList<TH> browse(Statement stmt, int max, int min, String city, String state, String keyword,
			String category, int sort) {
		boolean hasWhere = false;
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
		// user wants to set a max
		if (max != -1) {
			hasWhere = true;
			sql += "WHERE t.price <= " + max + " ";
		}
		// user wants to set a min
		if (min != -1) {
			if (!hasWhere) {
				hasWhere = true;
				sql += "WHERE t.price >= " + min + " ";
			} else
				sql += "AND t.price >= " + min + " ";
		}
		// user wants specific city
		if (city != null) {
			if (!hasWhere) {
				hasWhere = true;
				sql += "WHERE t.address LIKE '%" + city + "%' ";
			} else
				sql += "AND t.address LIKE '%" + city + "%' ";
		}
		// user wants specific state
		if (state != null) {
			if (!hasWhere) {
				hasWhere = true;
				sql += "WHERE t.address LIKE '%" + state + "%' ";
			} else
				sql += "AND t.address LIKE '%" + state + "%' ";
		}
		// user wants specific keyword
		if (keyword != null) {
			if (!hasWhere) {
				hasWhere = true;
				sql += "WHERE k.word LIKE '" + keyword + "' ";
			} else
				sql += "AND k.word LIKE '" + keyword + "' ";
		}
		// user wants specific category
		if (category != null) {
			if (!hasWhere) {
				hasWhere = true;
				sql += "WHERE t.category LIKE '" + category + "' ";
			} else
				sql += "AND t.category LIKE '" + category + "' ";
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
	 * Method used to retrieve most useful feedback for a particular th.
	 * 
	 * @param selected
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<Feedback> mostUsefulFeedback(TH selected, int limit, Statement stmt) {
		ArrayList<Feedback> feedbackList = new ArrayList<>();
		String sql = "select * " + "from feedback " + "where feedback.fid in (" + "select feedback.fid "
				+ "from feedback " + "left outer join rate " + "on rate.fid = feedback.fid  " + "where hid = "
				+ Integer.toString(selected.getHid()) + " " + "group by feedback.fid "
				+ "order by sum(rate.rating) desc) " + "limit " + Integer.toString(limit) + ";";

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
}
