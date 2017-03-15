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
	
	

}
