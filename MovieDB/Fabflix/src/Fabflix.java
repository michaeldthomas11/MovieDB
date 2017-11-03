import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Fabflix extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet connects to MySQL database, does user login, and allows user to browse/search.";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try
		{
			Connection connection = ConnectionMethods.initialize(out);
			
			Statement customerSelect = connection.createStatement();

			int successful = 0;
			HttpSession session = request.getSession(true);
			
			String userName = "";
			int userID = 0;
			
			if(request.getParameter("enter") != null)
			{
				String email = request.getParameter("email");
				String password = request.getParameter("password");

				ResultSet rs = customerSelect.executeQuery(
						"SELECT *" +
						" FROM customers WHERE email = '" + email + "' AND" +
						" password = '" + password + "'"
				);
				
				while (rs.next())
				{
					successful++;
					if(successful == 1)
					{
						userName = rs.getString("first_name") + " " + rs.getString("last_name");
						userID = rs.getInt("ID");
					}
				}

				rs.close();
			}
			customerSelect.close();
			
			if(successful == 1)
			{	
				session.setAttribute("userName", userName);
				session.setAttribute("userID", userID);
				session.setAttribute("userEmail", request.getParameter("email"));
			}	
			
			if(session.getAttribute("userEmail") == null)
			{
				session.setAttribute("userName", null);
				session.setAttribute("userID", null);

				out.println("<html>\n<head>\n\t<title>Fabflix - Login Failed</title>" + 
						"\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\">\n</head>\n" +
						"<body>" +
						//insert url links to other servlets
						"\n\t<a href=\"Login.html\">Login</a>" +
						"\n\t<br><p>Login failed!</p><p>Incorrect email and/or password. Please try again.</p>");
			}
			else if(session.getAttribute("userEmail") != null &&
					request.getParameter("email") != null &&
					!session.getAttribute("userEmail").equals(request.getParameter("email")))
			{
				out.println("<html>\n<head>\n\t<title>Fabflix - Login Failed</title>" + 
						"\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\">\n</head>\n" +
						"<body>" +
						//insert url links to other servlets
						"\n\t<br><p>Login failed!</p><p>Current user has not yet logged out.</p><br>" +
						"\n\t<a href=\"/Fabflix/search\">Continue browsing as " + session.getAttribute("userName") + "</a><br>" +
						"\n\t<a href=\"/Fabflix/logout\">Logout</a>");
			}
			else
			{
				HTMLMethods.printMovieListHeader(out);
							//insert url links to other servlets
							out.println("\n<br>Welcome, " + session.getAttribute("userName") + "!<br>" + 
							"\n<a href=\"/Fabflix/cart\">Checkout</a>" +
							"\n<br><a href=\"/Fabflix/logout\">Logout</a>" +
							"\n<br><h1>Search and Browse</h1>" + 
							"\n<br><br><br><table border=\"1\" style=\"table-layout:fixed; width: 100%; border: 1px solid black; padding: 15px;\">" +
							"<th>Search Movie</th><th>Browse by Genre</th><th>Browse by Title</th>" +
							"\n<tr><td><form " +  
								"\n\taction=\"/Fabflix/movielist\" method=\"GET\">" +
								"\n\t<label>Title:</label>\n\t\t<input id= \"textId\" list=\"text\" onkeyup=\"ajaxFunction(this.value)\" name=\"title\" value=\"\"/></BR>" +
								"\n\t<div id=\"datalistdiv\">" +
								"\n\t</div>" +
								"\n\t<label>Year:</label>\n\t\t<input type=\"text\" name=\"year\" value=\"\"><BR>" +
								"\n\t<label>Director:</label>\n\t\t<input type=\"text\" name=\"director\" value=\"\"><BR>" +
								"\n\t<label>Star First Name:</label>\n\t\t<input type=\"text\" name=\"starFirstName\" value=\"\"><BR>" +
								"\n\t<label>Star Last Name:</label>\n\t\t<input type=\"text\" name=\"starLastName\" value=\"\"><BR>" +
								"\n\t<input type=\"submit\" value=\"Search\"><BR>" +
							"\n</form></td>" +
							"\n<td><br>" +
							"\n<form \n\taction=\"/Fabflix/movielist\" method=\"GET\">\n\t<select name=\"genre\">"
				);
				
				//Genre select
				Statement genreSelect = connection.createStatement();
				ResultSet genres = genreSelect.executeQuery("" +
						"SELECT DISTINCT name" +
						" FROM genres" +
						" ORDER BY name ASC");
				
				out.println("\n\t\t<option value=\"\" selected> --- </option>");
				while(genres.next())
				{	
					out.println("\t\t<option name=\"" + genres.getString("name") + "\">" + genres.getString("name") + "</option>");
				}
				
				genres.close();
				genreSelect.close();
				
				//Title select
				out.println("\n\t</select>   <input type=\"submit\" value=\"Go\">\n</form></td>" +
						"\n<td><br>" +
						"\n<form \n\taction=\"/Fabflix/movielist\" method=\"GET\"><select name=\"movie\">" +
						"\n\t\t<option value=\"\" selected> --- </option>");
				
				char letter = '0';
				for(int i = 0; i < 10; i++)
				{	
					out.println("\t\t<option value=\"" + letter + "\">" + letter + "</option>");
					letter++;
				}
				
				letter = 'A';
				for(int i = 0; i < 26; i++)
				{	
					out.println("\n\t\t<option value=\"" + letter + "\">" + letter + "</option>");
					letter++;
				}
				out.println("\n\t</select>   <input type=\"submit\" value=\"Go\">\n</form></td></tr></table>");
			}
			
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch (SQLException ex) {
			while (ex != null) {
				System.out.println ("SQL Exception:  " + ex.getMessage ());
				ex = ex.getNextException ();
			}  // end while
		}  // end catch SQLException

		catch(java.lang.Exception ex)
		{
			out.println("<html>" +
					"<head><title>" +
					"Search and Browse: Error" +
					"</title></head>\n<body>" +
					"<p>SQL error in doGet: " +
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