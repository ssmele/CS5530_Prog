import java.sql.ResultSet;
import java.sql.Statement;

public class Querys {
	public Querys() {
	}

	public String newUser(String login, String name, String password, String address, String phone, Boolean user_type,
			Statement stmt) {

		// Construct beautiful insert statement.
		String sql = "INSERT INTO user (login, name, password, address, phone, user_type)" + "VALUES (" + "','" + login
				+ "','" + name + "','" + password + "','" + address + "','" + phone + "','" + user_type + "')";

		String output = "";
		ResultSet rs = null;
		System.out.println("executing " + sql);
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				output += rs.getString("name") + "   " + rs.getString("password") + "\n";
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("cannot execute the query");
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();
			} catch (Exception e) {
				System.out.println("cannot close resultset");
			}
		}
		return output;

	}

	public boolean loginUser(String login, String password, Statement stmt) {
		// Construct sql select statement.
		String sql = "Select * from user where user.login = '" + login + "' and user.password '" + password + "'";
		ResultSet rs = null;
		System.out.println("executing " + sql);
		try {
			rs = stmt.executeQuery(sql);
			int count = 0;
			while (rs.next()) {
				// Need to also get information out the this user to be able see
				// what type of user, and information
				// we will later also need to query.
				count++;
			}
			rs.close();

			if (count == 1) {
				System.out.println("Correct! You are now signed in as " + login + " !");
				return true;
			} else {
				System.out.println("Login, and password do not match.");
				return false;
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
		return false;

	}

}
