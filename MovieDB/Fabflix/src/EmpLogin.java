import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class EmpLogin extends HttpServlet
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
			
			out.println(
					"<html><head><title>Fabflix - Employee Login</title>" +
							"<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\"></head>" +
					"<body><p>Not an employee? <a href=\"/Fabflix/Login.html\">Sign in as a customer.</a></p>" +
						"<h1>Fabflix Employee Login</h1>" +
						"<form action=\"/Fabflix/employee/menu\" method=\"POST\">" +
							"Email: <input type=\"text\" name=\"email\" placeholder=\"Email\" autofocus required><br>" +
							"Password: <input type=\"password\" name=\"password\" placeholder=\"Password\" required><br>" +
								"<input type=\"submit\" value=\"Login\">" +
								"<input type=\"hidden\" name=\"enter\" value=\"true\">" +
								"<input type=\"hidden\" name=\"emp\" value=\"true\">" +
						"</form>"
			);
			
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