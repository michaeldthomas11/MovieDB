import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ShoppingCart extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet displays current items in user's shopping cart.";
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
					"<html>\n<head>\n\t<title>Fabflix - Shopping Cart</title>" + 
						"<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Cart.css\">\n</head>\n" +
					"<body>\n" +
						//insert url links to other servlets
						"<a href=\"/Fabflix/search\">Continue browsing</a><br>\n" + 
						"<a href=\"/Fabflix/logout\">Logout</a><br><br>" +
						"\n<br><h1>Shopping Cart</h1>"
			);
			
			int userID = (int) request.getSession().getAttribute("userID");
			
			if(request.getParameter("movieToUpdate") != null && request.getParameter("quantity") != null)
			{
				int quan = Integer.valueOf(request.getParameter("quantity"));
				String movieID = request.getParameter("movieToUpdate");
				if(quan > 0)
				{
					Statement updateQuery = connection.createStatement();
					int rowUpdated = updateQuery.executeUpdate(
							"UPDATE shopping_cart" +
							" SET quantity='" + quan + "'" +
							" WHERE customer_id = '" + userID + "'" +
									" AND movie_id = '" + movieID + "'"
					);
					updateQuery.close();
					
					if(rowUpdated == 1)
					{
						out.println(
								"<p>Updated quantity of <a href=\"/Fabflix/details?movie=" +
								movieID + "\">" + movieID + "</a> to " + quan + ".</p>"
						);
					}
					else
					{
						out.println("<p>Error: <a href=\"/Fabflix/details?movie=" + movieID + "\">Movie</a>" +
								" was not properly updated in shopping cart or no such movie exists int eh shopping cart.</p>");
					}
				}
				else if (quan == 0)
				{
					Statement deleteQuery = connection.createStatement();
					int rowDeleted = deleteQuery.executeUpdate(
							"DELETE FROM shopping_cart" +
							" WHERE customer_id = '" + userID + "' AND movie_id = '" + movieID + "'"
					);
					deleteQuery.close();

					if(rowDeleted == 1)
					{
						out.println(
								"<p>Removed <a href=\"/Fabflix/details?movie=" +
								movieID + "\">" + movieID + "</a> from the shopping cart.</p>"
						);
					}
					else
					{
						out.println("<p>Error: <a href=\"/Fabflix/details?movie=" + movieID + "\">Movie</a>" +
								" was not properly removed from shopping cart or no such movie exists in the shopping cart.</p>");
					}
				}
				else
				{
					out.println("<p>Error: Cannot update quantity to a negative number.</p>");
				}
			}
			else if(request.getParameter("add") != null)
			{
				String movieID = request.getParameter("add");
				Statement insertQuery = connection.createStatement();
				int rowInserted = insertQuery.executeUpdate(
						"INSERT INTO shopping_cart" +
						" SELECT '" + userID + "' AS customer_id, '" + movieID + "' AS movie_id, '1' AS quantity" +
						" FROM shopping_cart" +
						" WHERE (movie_id = '" + movieID + "' AND customer_id = '" + userID + "')" +
						" HAVING COUNT(*) = 0;"
				);
				insertQuery.close();
				
				if(rowInserted == 1)
				{
					out.println(
							"<p><a href=\"/Fabflix/details?movie=" + movieID + "\">" +
							movieID + "</a> has been added to the shopping cart.</p>"
					);
				}
				else if (rowInserted == 0)
				{
					insertQuery = connection.createStatement();
					ResultSet quan = insertQuery.executeQuery(
							"SELECT quantity" +
							" FROM shopping_cart" +
							" WHERE customer_id = '" + userID + "'" +
								" AND movie_id = '" + movieID + "'"
					);
					
					quan.next();
					int quantity = quan.getInt("quantity");
					quan.close();
					insertQuery.close();
					
					insertQuery = connection.createStatement();
					insertQuery.executeUpdate(
							"UPDATE shopping_cart" +
							" SET quantity = '" + (quantity+1) + "'" +
							" WHERE customer_id = '" + userID + "'" +
								" AND movie_id = '" + movieID + "'"
					);
					
					out.println(
							"<p><a href=\"/Fabflix/details?movie=" + movieID + "\">" +
							movieID + "</a> already exists in shopping cart. Quantity has been increased.</p>"
					);
				}
				else
				{
					out.println("<p>Error: <a href=\"/Fabflix/details?movie=" + movieID + "\">Movie</a>" +
							" was not properly placed in shopping cart.</p>");
				}
			}
			
			
			Statement cartSelect = connection.createStatement();
			ResultSet items = cartSelect.executeQuery(
					"SELECT movies.title, movies.banner_url, movies.id" +
					" FROM movies" +
					" WHERE movies.id" +
					" IN (" +
						" SELECT shopping_cart.movie_id" +
						" FROM shopping_cart" +
						" WHERE customer_id = '" + userID +
					"')"
			);
			out.println("<table border=\"1\"><th>Items in Cart</th><th>Quantity</th>");
			Statement select = connection.createStatement();
			while(items.next())
			{
				String movieID = Integer.toString(items.getInt("id"));
				out.println(
					"<tr><td>" +
						((items.getString("banner_url") != null)?
						"<img src=" + items.getString("banner_url") + " width=62 height=70>" : "") +
						"<br><a href=\"/Fabflix/details?movie=" + movieID + "\">" +
						items.getString("title") + "</a></td>" +
					"<td>" + "Quantity: ");
				ResultSet resultSet = select.executeQuery("SELECT quantity FROM moviedb.shopping_cart where customer_id = '" + userID + "' and movie_id = '" + movieID + "'");
				while(resultSet.next())
				{
					out.println(resultSet.getInt("quantity"));
				}
				out.println("\n<form action=\"/Fabflix/cart\" method=\"POST\">" +
						"<input type=\"hidden\" name=\"movieToUpdate\" value=\"" + movieID + "\">" + 
						"<input type=\"text\" name=\"quantity\">" +
						"<input type=\"submit\" value=\"Update Quantity\">" +
					"</form></td>" +
					"</tr>"
				);
			}
			
			out.println("</table><br><form action=\"/Fabflix/checkout\" method=\"POST\"><input type=\"hidden\" name=\"proceed\" value=\"true\">" +
					"<input type=\"submit\" value=\"Proceed with Checkout\"></form>");
			
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			out.println("<html>" +
					"<head><title>" +
					"Shopping Cart: Error" +
					"</title></head>\n<body>" +
					"<p>Error in doGet: " +
					e.getMessage() + "</p></body></html>");
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