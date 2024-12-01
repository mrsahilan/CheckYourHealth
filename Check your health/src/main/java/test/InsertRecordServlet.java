package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class InsertRecordServlet
 */
public class InsertRecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertRecordServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String phoneNumber = request.getParameter("phone_number");
		String appointmentDate = request.getParameter("appointment_date");
		String patientType = request.getParameter("patient_type");

		String jdbcURL = "jdbc:oracle:thin:@localhost:1521:orcl"; 
		String dbUser = "Sahil"; 
		String dbPassword = "SAHIL"; 

		try {
			// Load Oracle JDBC Driver
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

			// SQL Insert Statement
			String sql = "INSERT INTO APPOINTMENTS (name, phone_number, appointment_date, patient_type) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, phoneNumber);
			statement.setString(3, appointmentDate);
			statement.setString(4, patientType);

			// Execute the insert operation
			int rowsInserted = statement.executeUpdate();
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			if (rowsInserted > 0) {
				out.println("<h2>A new appointment was inserted successfully!</h2>");
			} else {
				out.println("<h2>Failed to insert appointment.</h2>");
			}

			// Clean up
			statement.close();
			connection.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
		}
	}

}
