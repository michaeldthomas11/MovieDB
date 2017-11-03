import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Confirmation extends HttpServlet
{
	/**
	 * 
	 */
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
			
			if(request.getParameter("firstName") != null && request.getParameter("lastName") != null &&
					request.getParameter("ccid") != null && request.getParameter("expiration") != null)
			{
				if((request.getParameter("firstName") + " " + request.getParameter("lastName")).equals(
						request.getSession().getAttribute("userName")))
				{
					Statement checkQuery = connection.createStatement();
					ResultSet customer = checkQuery.executeQuery(
							"SELECT * FROM creditcards" +
							" WHERE id = '" + request.getParameter("ccid") + "' AND" +
									" first_name = '" + request.getParameter("firstName") + "' AND" +
									" last_name = '" + request.getParameter("lastName") + "' AND" +
									" expiration = '" + request.getParameter("expiration") + "'"
					);
					
					customer.next();
					if(customer.isFirst() && customer.isLast())
					{
						Statement selectCart = connection.createStatement();
						ResultSet cart = selectCart.executeQuery(
								"SELECT movie_id, quantity FROM shopping_cart" +
								" WHERE customer_id = '" + request.getSession().getAttribute("userID") + "'" 
						);
						
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date();
						
						PreparedStatement insertSales = connection.prepareStatement(
								"INSERT INTO sales(customer_id, movie_id, sale_date)" +
								" VALUES('" + request.getSession().getAttribute("userID") + "', ?, '" + df.format(date) + "')" 
						);
						
						out.println("<h1>Order Confirmation</h1><table>");
						
						while(cart.next())
						{
							int quan = cart.getInt("quantity");
							while(quan > 0)
							{
								insertSales.clearParameters();
								insertSales.setInt(1, cart.getInt("movie_id"));
								insertSales.executeUpdate();
								quan--;
							}
							
							out.println("<tr><td><br>" + cart.getInt("quantity") + "of <a href=\"/Fabflix/details?movie=" +
									cart.getString("movie_id") + "\">" + cart.getString("movie_id") + "</a><br></td></tr>");
						}
						insertSales.close();
						cart.close();
						selectCart.close();
						
						out.println("</table>");
						
						Statement deleteQuery = connection.createStatement();
						deleteQuery.executeUpdate(
								"DELETE FROM shopping_cart" +
								" WHERE customer_id = '" + request.getSession().getAttribute("userID") + "'"
						);
						deleteQuery.close();
					}
					else
					{
						out.println(
								"<p>Error: No credit card information matches for " +
								request.getSession().getAttribute("userName") + ".</p>" +
								"<p>Please make sure credit card information is correct and try again.</p>"
						);
					}	
				}
				else
				{
					out.println("<p>Error: Incorrect credit card used for the current user.</p>");
				}
			}
			else
			{
				out.println("<p>Cannot process order for " + request.getSession().getAttribute("userName") + ".</p>" +
						"\n<p>Please enter the correct name for the credit card holder, the credit card number, and its expiration date.");
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