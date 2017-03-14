import java.lang.*;
import java.sql.*;
import java.io.*;

public class testdriver2 {
	/**
	 * @param args
	 */
	public static void displayMenu() {
		System.out.println("Signed in as " + "Insert Username here" + "!!!!");
		System.out.println("1. search a course by cname and dname:");
		System.out.println("2. enter your own query:");
		System.out.println("3. exit:");
		System.out.println("pleasse enter your choice:");
		// Here is a change
	}

	public static void displayLogin() {
		System.out.println("        Welcome to the Uotel System     ");
		System.out.println("1. Sign in with existing account");
		System.out.println("2. Register with a new account");
		System.out.println("Please enter your choice:");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Example for cs5530");
		Connector con = null;
		String choice;
		String cname;
		String dname;
		String sql = null;
		boolean loggedIn = false;

		int c = 0;
		try {
			// remember to replace the password
			con = new Connector();
			Querys Q = new Querys();
			Q.newUser("stone", "stone", "swag", "ues", "sone", true, con.stmt);
			System.out.println("Database connection established");

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				if (!loggedIn) {
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
					if (c == 1) {
						System.out.println("please enter a cname:");
						while ((cname = in.readLine()) == null && cname.length() == 0)
							;
						System.out.println("please enter a dname:");
						while ((dname = in.readLine()) == null && dname.length() == 0)
							;
						Course course = new Course();
						System.out.println(course.getCourse(cname, dname, con.stmt));
					} else if (c == 2) {
						System.out.println("please enter your query below:");
						while ((sql = in.readLine()) == null && sql.length() == 0)
							System.out.println(sql);
						ResultSet rs = con.stmt.executeQuery(sql);
						ResultSetMetaData rsmd = rs.getMetaData();
						int numCols = rsmd.getColumnCount();
						while (rs.next()) {
							// System.out.print("cname:");
							for (int i = 1; i <= numCols; i++)
								System.out.print(rs.getString(i) + "  ");
							System.out.println("");
						}
						System.out.println(" ");
						rs.close();
					} else {
						System.out.println("EoM");
						con.stmt.close();

						break;
					}
				} else {
					displayMenu();
					while ((choice = in.readLine()) == null && choice.length() == 0)
						;
					try {
						c = Integer.parseInt(choice);
					} catch (Exception e) {
						continue;
					}
					if (c < 1 | c > 3)
						continue;
					if (c == 1) {
						System.out.println("please enter a cname:");
						while ((cname = in.readLine()) == null && cname.length() == 0)
							;
						System.out.println("please enter a dname:");
						while ((dname = in.readLine()) == null && dname.length() == 0)
							;
						Course course = new Course();
						System.out.println(course.getCourse(cname, dname, con.stmt));
					} else if (c == 2) {
						System.out.println("please enter your query below:");
						while ((sql = in.readLine()) == null && sql.length() == 0)
							System.out.println(sql);
						ResultSet rs = con.stmt.executeQuery(sql);
						ResultSetMetaData rsmd = rs.getMetaData();
						int numCols = rsmd.getColumnCount();
						while (rs.next()) {
							// System.out.print("cname:");
							for (int i = 1; i <= numCols; i++)
								System.out.print(rs.getString(i) + "  ");
							System.out.println("");
						}
						System.out.println(" ");
						rs.close();
					} else {
						System.out.println("EoM");
						con.stmt.close();

						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Either connection error or query execution error!");
		} finally {
			if (con != null) {
				try {
					con.closeConnection();
					System.out.println("Database connection terminated");
				}

				catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
}
