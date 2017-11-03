import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class UMGui extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet connects to MySQL database and allows user to management privileges.";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		Connection connection = null;

		try
		{
			connection = ConnectionMethods.initialize(out);
			HttpSession session = request.getSession();
			
			
			//Check for create user privilege
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(
					"SELECT *" +
					" FROM information_schema.user_privileges" +
					" WHERE privilege_type = 'CREATE USER' AND grantee = \"'" + session.getAttribute("empName") + "'@'localhost'\"");
			boolean hasGrant = false;
			resultSet.next();
			if(resultSet.isFirst() && resultSet.isLast())
				hasGrant = true;
			resultSet.close();
			stmt.close();
			
			out.println("<html><head><title>Fabflix - User Management</title></head><body>\n" +
					//insert url links to other servlets
					"<a href=\"/Fabflix/employee/menu\">Return to Menu</a><br>\n" + 
					"<a href=\"/Fabflix/logout\">Logout</a><br><br>" +
					"\n<br><h1>User Management</h1>");
			
			// If user has no granting privileges
			if(!hasGrant)
			{
				out.println("<p>Sorry! You do not have the necessary privileges to enter.</p>");
			}
			else
			{
				if(request.getParameter("user") !=  null)
				{
					if(request.getParameter("user").equals("add") && request.getParameter("email") != null &&
							request.getParameter("password") != null && request.getParameter("fullName") != null)
					{
						String email = request.getParameter("email");
						String password = request.getParameter("password");
						String fullName = request.getParameter("fullName");
						
						Statement insertQuery = connection.createStatement();
						int rowInserted = insertQuery.executeUpdate(
								"INSERT INTO moviedb.employees" +
								" SELECT '" + email + "' AS email, '" + password + "' AS password, '" + fullName + "' AS fullName" +
								" FROM moviedb.employees" +
								" WHERE (email = '" + email + "' AND password = '" + password + "' AND fullName = '"+ fullName + "')" +
								" HAVING COUNT(*) = 0;"
						);
						insertQuery.close();
						
						if(rowInserted == 1)
						{
							insertQuery = connection.createStatement();
							insertQuery.execute("CREATE USER '" + fullName + "'@localhost IDENTIFIED BY '" + password + "'");
							
							out.println("<p>" + fullName + " added as a new employee.</p><br>");
						}
						else if (rowInserted == 0)
							out.println("<p>Error: Unable to add employee. Employee already exists.</p><br>");
						else
							out.println("<p>Error: Unknown error occurred.</p><br>");
					}
					else if(request.getParameter("user").equals("remove"))
					{
						Statement deleteQuery = connection.createStatement();
						int rowDeleted = deleteQuery.executeUpdate(
								"DELETE FROM moviedb.employees" +
								" WHERE fullName = '" + request.getParameter("drop") + "'"
						);
						deleteQuery.close();
						
						if(rowDeleted == 1)
						{
							deleteQuery = connection.createStatement();
							deleteQuery.execute("DROP USER '" + request.getParameter("drop") + "'@localhost");
							
							out.println("<p>" + request.getParameter("drop") + " removed from employees.</p><br>");
						}
						else if (rowDeleted == 0)
							out.println("<p>Error: Unable to remove employee. Employee does not exist.</p><br>");
						else
							out.println("<p>Error: Unknown error occurred.</p><br>");
					}
				}
				
				//USER FORM
				out.println("<table border=\"0\"><tr>");
				out.println("<td><form action=\"/Fabflix/employee/userManagement\" method=\"POST\">" +
						"<fieldset><legend>Create New Employee:</legend><br>" +
						"Email: <input type=\"email\" name=\"email\" placeholder=\"user@email.com\" required><br><br>" +
						"Password: <input type=\"password\" name=\"password\" placeholder=\"Password\" required><br><br>" +
						"FullName: <input type=\"text\" name=\"fullName\" placeholder=\"Jane Doe\" required><br><br>" +
						"</fieldset><input type=\"hidden\" name=\"user\" value=\"add\">" + 
						"<input type=\"submit\" value=\"Add New Employee\"><br>" +
						"</form></td>");
				
				// USER LIST
				out.println("<td><form action=\"/Fabflix/employee/userManagement\" method=\"POST\">" +
						"<fieldset><legend>Remove Employee:</legend><select name=\"drop\" size=\"10\">");
				stmt = connection.createStatement();
				resultSet = stmt.executeQuery("SELECT fullname, email from moviedb.employees");
				while(resultSet.next())
				{
					out.println("<option value=\"" + resultSet.getString(1) + "\">" +
							resultSet.getString(1) + "  :  " + resultSet.getString(2) + "<br>");
				}
				resultSet.close();
				stmt.close();
				out.println("</select></fieldset>" +
						"<input type=\"hidden\" name=\"user\" value=\"remove\">" + 
						"<input type=\"submit\" value=\"Remove Employee\"><br>" +
						"</form></td></tr></table>");
			}
			
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch(Exception ex)
		{
			out.println("<html>" +
					"<head><title>" +
					"Search and Browse: Error" +
					"</title></head>\n<body>" +
					"<p>Error in doGet: " +
					ex.getMessage() + "</p></body></html>");
			return;
		}
		finally
		{
			try
			{
				out.close();
				if(connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				out.println("<html>" +
						"<head><title>" +
						"Search and Browse: Error" +
						"</title></head>\n<body>" +
						"<p>Error in doGet: " +
						e.getMessage() + "</p></body></html>");
				return;
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		doGet(request, response);
	}
}