import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

public class SearchAjax extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet confirms success or failure of checkout.";
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
			{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		/*
		 * Ajax code
		Calendar c = Calendar.getInstance ();


		res.setContentType("text/html");

		PrintWriter out = res.getWriter();
		out.println(c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));

		//out.println("Hello World" + Calendar.getInstance().get(Calendar.SECOND));
		 */

		try
		{
			Connection connection = ConnectionMethods.initialize(out);

			out.println(
					"<html>\n<head>\n\t<title>Fabflix - Order Confirmation</title>" + 
							"<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Details.css\">\n</head>\n" +
							"<body>\n" +
							//insert url links to other servlets
							"\n<a href=\"/Fabflix/search\">Continue browsing</a>" +
							"\n<br><a href=\"/Fabflix/cart\">Return to Shopping Cart</a>" +
							"\n<br><a href=\"/Fabflix/logout\">Logout</a><br><br>"
					);

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
					"Checkout: Error" +
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
