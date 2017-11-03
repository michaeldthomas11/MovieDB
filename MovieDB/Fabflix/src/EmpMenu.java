import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class EmpMenu extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet asks for employee information to login to the employee user management interface.";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try
		{
			Connection connection = ConnectionMethods.initialize(out);
			
			Statement empSelect = connection.createStatement();

			int successful = 0;
			HttpSession session = request.getSession(true);
			
			String name = "";
			
			if(request.getParameter("enter") != null)
			{
				ResultSet rs = empSelect.executeQuery(
						"SELECT *" +
						" FROM employees WHERE email = '" + request.getParameter("email") + "' AND" +
						" password = '" + request.getParameter("password") + "'"
				);
				
				while (rs.next())
				{
					successful++;
					if(successful == 1)
						name = rs.getString("fullname");
				}

				rs.close();
			}
			empSelect.close();
			
			if(successful == 1)
			{	
				session.setAttribute("empEmail", request.getParameter("email"));
				session.setAttribute("empName", name);
			}	
			
			if(session.getAttribute("empEmail") == null)
			{
				session.setAttribute("empName", null);

				out.println("<html>\n<head>\n\t<title>Fabflix - Login Failed</title>" + 
						"\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\">\n</head>\n" +
						"<body>" +
						//insert url links to other servlets
						"\n\t<a href=\"/Fabflix/employee/login\">Login</a>" +
						"\n\t<br><p>Login failed!</p><p>Incorrect email and/or password. Please try again.</p>");
			}
			else if(session.getAttribute("empEmail") != null &&
					request.getParameter("email") != null &&
					!session.getAttribute("empEmail").equals(request.getParameter("email")))
			{
				out.println("<html>\n<head>\n\t<title>Fabflix - Login Failed</title>" + 
						"\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\">\n</head>\n" +
						"<body>" +
						//insert url links to other servlets
						"\n\t<br><p>Login failed!</p><p>Current user has not yet logged out.</p><br>" +
						"\n\t<a href=\"/Fabflix/employee/menu\">Continue to menu as " + session.getAttribute("empName") + "</a><br>" +
						"\n\t<a href=\"/Fabflix/logout\">Logout</a>");
			}
			else
			{

				out.println(
						"<html><head><title>Fabflix - Employee Menu</title>" +
								"<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\"></head>" +
						"<body>\n<br><a href=\"/Fabflix/logout\">Logout</a>" +
							"<p>Welcome, " + session.getAttribute("empName") + "</p>" +
							"<h1>Fabflix Employee Menu</h1>" +
							/*"<form action=\"/Fabflix/employee/addMovie\" method=\"POST\">" +
								"<input type=\"submit\" value=\"Add New Movie\">" +
							"</form><br>" +
							"<form action=\"/Fabflix/employee/report\" method=\"POST\">" +
								"<input type=\"submit\" value=\"Print Report\">" +
							"</form><br>" +*/
							"<form action=\"/Fabflix/employee/userManagement\" method=\"POST\">" +
								"<input type=\"submit\" value=\"User Management\">" +
							"</form>" +
							"<form action=\"/Fabflix/employee/userPrivileges\" method=\"POST\">" +
								"<input type=\"submit\" value=\"User Privilege Management\">" +
							"</form>" +
						"</body></html>"
				);
			}
			
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch(java.lang.Exception ex)
		{
			out.println("<html>" +
					"<head><title>" +
					"Checkout: Error" +
					"</title></head>\n<body>" +
					"<p>Employee login error in doGet: " +
					ex.getMessage() + "</p></body></html>");
			return;
		}
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		doGet(request, response);
	}
}