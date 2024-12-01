package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InsertRecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public InsertRecordServlet() {
		super();
	}

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
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

			// Check if phone number already exists
			String checkSql = "SELECT id, name FROM APPOINTMENTS WHERE phone_number = ?";
			PreparedStatement checkStatement = connection.prepareStatement(checkSql);
			checkStatement.setString(1, phoneNumber);

			ResultSet resultSet = checkStatement.executeQuery();
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");

			if (resultSet.next()) {
				out.println("<html>");
				out.println("<head>");
				out.println("<style>");
				out.println("body {");
				out.println("    background-color: yellow;");
				out.println("    color: red;");
				out.println("    font-family: Arial, sans-serif;");
				out.println("    text-align: center;");
				out.println("    padding: 50px;");
				out.println("}");
				out.println("h2 {");
				out.println("    font-size: 2.5em;");
				out.println("    margin-bottom: 20px;");
				out.println("}");
				out.println("p {");
				out.println("    font-size: 1.5em;");
				out.println("}");
				out.println("</style>");
				out.println("</head>");
				out.println("<body>");
				out.println("<h2>Invalid Details: Phone Number Already Exists.</h2>");
				int existingId = resultSet.getInt("id");
				String existingName = resultSet.getString("name");
				out.println("<p>Existing Appointment Number = <strong>" + existingId + "</strong>, Name = <strong>"
						+ existingName + "</strong></p>");
				out.println("</body>");
				out.println("</html>");
			} else {
				// Insert new record
				String sql = "INSERT INTO APPOINTMENTS (name, phone_number, appointment_date, patient_type) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
				PreparedStatement insertStatement = connection.prepareStatement(sql, new String[] { "ID" });
				insertStatement.setString(1, name);
				insertStatement.setString(2, phoneNumber);
				insertStatement.setString(3, appointmentDate);
				insertStatement.setString(4, patientType);

				int rowsInserted = insertStatement.executeUpdate();

				if (rowsInserted > 0) {
					ResultSet generatedKeys = insertStatement.getGeneratedKeys();
					if (generatedKeys.next()) {
						int newId = generatedKeys.getInt(1);
						out.println("<html>");
						out.println("<head>");
						out.println("<style>");
						out.println("body {");
						out.println("    background-color: blue;");
						out.println("    color: yellow;");
						out.println("    font-family: Arial, sans-serif;");
						out.println("    text-align: center;");
						out.println("    padding: 50px;");
						out.println("}");
						out.println("h2 {");
						out.println("    font-size: 2.5em;");
						out.println("    margin-bottom: 20px;");
						out.println("}");
						out.println("p {");
						out.println("    font-size: 1.5em;");
						out.println("}");
						out.println("</style>");
						out.println("</head>");
						out.println("<body>");
						out.println("<h2>Appointment Booked Successfully!</h2>");
						out.println("<p>Appointment Number: <strong>" + newId + "</strong></p>");
						out.println("<p>Name: <strong>" + name + "</strong></p>");
						out.println("</body>");
						out.println("</html>");
					}
				} else {
					out.println("<h2>Failed to insert appointment.</h2>");
				}

				insertStatement.close();
			}

			checkStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
		}
	}
}
