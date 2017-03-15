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

		return new TH(-1, category, price, year_built, name, address, url, phone, current_user.getLogin(), Date.valueOf(LocalDate.now()));

	}
	
	
	public ArrayList<TH> getUsersTHs(String login, Statement stmt){
		String sql = "Select * from th where login = '" + login + "';";
		
		System.out.println("executing: " + sql);
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		
		try{
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH tempTH = new TH(rs.getInt("hid"), rs.getString("category"), rs.getInt("price"),
						rs.getString("year_built"), rs.getString("name"), rs.getString("address"),
						rs.getString("url"), rs.getString("phone"), rs.getString("login"),
						rs.getDate("date_listed"));
				thList.add(tempTH);
			}
			rs.close();
		}catch(Exception e){
			System.out.println("cannot execute query: " + sql);
			return null;
		}finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		
		return thList;
	}
	
	public TH updateTH(TH update, Connection con){
		try {
			PreparedStatement updateTH = con.prepareStatement(
					"update th set category=?, price = ?, year_built = ?, name = ?, address = ?, url = ?, phone = ?, login = ?, date_listed = ?" +
					"where hid = ?");
			
			//TODO: Still need to update values they want.
			
			
			updateTH.setString(1, update.getCategory());
			updateTH.setInt(2, update.getPrice());
			updateTH.setString(3, update.getYear_built());
			updateTH.setString(4, update.getName());
			updateTH.setString(5, update.getAddress());
			updateTH.setString(6, update.getUrl());
			updateTH.setString(7, update.getPhone());
			updateTH.setString(8, update.getLogin());
			updateTH.setDate(9, update.getDate_listed());
			updateTH.setInt(10,  update.getHid());

			updateTH.executeUpdate();
			// TODO: DOnt really know what exceptions could get thrown here need
			// to do more experimenting.
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Something got messed up.");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}

		return update;
	}
	
	public User newUser(String login, String name, String password, String address, String phone, boolean user_type,
			Statement stmt) {

		// Construct beautiful insert statement.
		String sql = "INSERT INTO user "
				+ "VALUES (" + "'" + login + "','" + name + "','" + password + "','" + address + "','" + phone + "'," + user_type + ");";
		
		
		System.out.println("executing " + sql);
		try {
			stmt.executeUpdate(sql);
		} catch (java.sql.SQLIntegrityConstraintViolationException e){
			System.out.println("User with specified login already exists please try again!");
			return null;
		} catch (Exception e) {
			System.out.println("cannot execute the query");
			return null;
		}
		
		return new User(login, password, user_type);
	}
	
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
	
	public void trustUser(String trustee, String truster, boolean trust, Statement stmt){
		String sql = "insert into trust VALUES (" + "'" + trustee + "','" + truster + "', " + trust + ")";
		System.out.println("Executing:" + sql);
		
		try{
			stmt.executeUpdate(sql);
			System.out.println(truster + " now " + (trust ? "does " : "dosn't ") + trustee);
		}catch(java.sql.SQLIntegrityConstraintViolationException e){
			System.out.println("One of the user logins provided does not exist.");
			return;
		}catch(Exception e){
			System.out.println("Cannot execute the query.");
			return;
		}
	}
	
	public void favoriteTH(int hid, String login, Date fv_date, Statement stmt){
		String sql = "insert into favorite VALUES (" + Integer.toString(hid) + ",'" + login + "', " + fv_date.toString() + ")";
		System.out.println("Executing:" + sql);
		
		try{
			stmt.executeUpdate(sql);
			System.out.println(login + " now favorites " + "TH with hid of " + Integer.toString(hid));
		}catch(java.sql.SQLIntegrityConstraintViolationException e){
			System.out.println("User login, or hid does not exist.");
			return;
		}catch(Exception e){
			System.out.println("Cannot execute the query.");
			return;
		}
	}
	
	/**
	 * This method takes in a keyword, and hid to associate it with. First adds the keyword to the database.
	 * It will attempt the add every time even if its already in their as we don't know if its in it already or not.
	 * It will then query for the keyword to get the appropriate wid from it.
	 * It then adds the relationship between keyword and hid to the has_keyword table.
	 * @param keyword
	 * @param hid
	 * @param stmt
	 */
	public void addKeywordToHID(String keyword, int hid,  Statement stmt){
		String insert_keyword_sql = "Insert into keyword (word) VALUES ('" + keyword + "');";
		String select_sql = "Select * from keyword where word = '" + keyword + "';";
		
		//First make keyword if it doesnt already exist.
		try{
			stmt.executeUpdate(insert_keyword_sql);
		}catch(java.sql.SQLIntegrityConstraintViolationException e){
			//System.out.println("keyword already in database.");
		}catch(Exception e){
			System.out.println("Keyword already exisits query could not be preformed.");
			return;
		}
		
		//Get the wid now by querying for keyword that equals word.
		ResultSet rs = null;
		int wid = -1;
		//Next key keyword
		try{
			rs = stmt.executeQuery(select_sql);
			while (rs.next()) {
				wid = rs.getInt("wid");
			}
			rs.close();
		}catch(Exception e){
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
		String insert_has_keyword_sql = "Insert into has_keyword VALUES (" + Integer.toString(hid) + ", " + Integer.toString(wid) + ");";
		try {
			stmt.executeUpdate(insert_has_keyword_sql);
			System.out.println("Successfully added keyword to th. \n-------------------------------------------");
		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Current TH already has this keyword associated to it. \n-------------------------------------------");
			return;
		} catch (Exception e) {
			System.out.println("Cannot execute the query.");
			return;
		}

	}
	
	/**
	 * This method returns a list of the most trusted users in descending order.
	 * The query used here is quite beautiful and should be admired.
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<String> mostTrusted(int limit, Statement stmt){
		ArrayList<String> loginList = new ArrayList<>();
		String sql = "select login, " 
				    + "count(if(is_trusted = True, True, Null)) - "
					+ "count(if(is_trusted = False, True, Null)) as trust_score "
					+ "from user " 
					+ "left outer join trust " 
					+ "on user.login = trust.trustee_id " 
				    + "group by login "
					+ "order by trust_score desc "
					+ "limit " + Integer.toString(limit) + ";";

		//Execute the most trusted user query. For each entry get the login and add it to the list.
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				loginList.add(rs.getString("login"));
			}
			rs.close();
		}catch(Exception e){
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
	 * @return A list of the most popular THs grouped by category 
	 * and ordered by the number of visits in descending order.
	 */
	public ArrayList<TH> getMostPopular(Statement stmt){
		String sql = "select t.category, t.hid, t.name, t.price, COUNT(t.hid) " +
							"from visit v, reserve r, th t " +
							"WHERE v.rid = r.rid AND t.hid = h_id " +
							"GROUP BY t.category, t.hid, t.name, t.price " +
							"ORDER BY t.category, COUNT(t.hid);";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try{
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH temp = new TH();
				temp.setCategory(rs.getString("category"));
				temp.setHid(rs.getInt("hid"));
				temp.setName(rs.getString("name"));
				temp.setPrice(rs.getInt("price"));
				thList.add(temp);
			}
			rs.close();
		}catch(Exception e){
			System.out.println("cannot execute query: " + sql);
			return null;
		}finally {
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
	 * @return A list of the most expensive THs grouped by category 
	 * and ordered by price of the TH in descending order.
	 */
	public ArrayList<TH> getMostExpensive(Statement stmt){
		String sql = "select t.category, t.hid, t.name, t.price " +
					 "from th t " +
					 "ORDER BY t.category, t.price DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try{
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH temp = new TH();
				temp.setCategory(rs.getString("category"));
				temp.setHid(rs.getInt("hid"));
				temp.setName(rs.getString("name"));
				temp.setPrice(rs.getInt("price"));
				thList.add(temp);
			}
			rs.close();
		}catch(Exception e){
			System.out.println("cannot execute query: " + sql);
			return null;
		}finally {
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
	 * @return A list of the highest rated THs grouped by category 
	 * and ordered by the average of all the ratings of the TH in 
	 * descending order.
	 */
	public ArrayList<TH> getHighestRated(Statement stmt){
		String sql = "select t.category, t.hid, t.name, t.price, AVG(f.score) " +
					 "from th t, feedback f " +
					 "where t.hid = f.hid " +
					 "group by t.category, t.hid, t.name, t.price " +
					 "ORDER BY t.category, AVG(f.score) DESC;";
		ResultSet rs = null;
		ArrayList<TH> thList = new ArrayList<TH>();
		try{
			rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				TH temp = new TH();
				temp.setCategory(rs.getString("category"));
				temp.setHid(rs.getInt("hid"));
				temp.setName(rs.getString("name"));
				temp.setPrice(rs.getInt("price"));
				thList.add(temp);thList.add(temp);
			}
			rs.close();
		}catch(Exception e){
			System.out.println("cannot execute query: " + sql);
			return null;
		}finally {
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
	 * @param limit
	 * @param stmt
	 * @return
	 */
	public ArrayList<String> mostUseful(int limit, Statement stmt){
		ArrayList<String> loginList = new ArrayList<>();
		String sql = "select login, sum(score) "
				   + "from feedback "
				   + "group by login "
				   + "order by sum(score) "
				   + "desc limit " + Integer.toString(limit) + ";";

		//Execute the most trusted user query. For each entry get the login and add it to the list.
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				loginList.add(rs.getString("login"));
			}
			rs.close();
		}catch(Exception e){
			System.out.println("cannot execute query: " + sql);
			return null;
		}finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		
		return loginList;
	}
}
