import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Details extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet displays the information of a single movie or star.";
	}
	
	// Use http GET
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		try
		{
			out.println("<html><head><title>Fabflix - Details</title>"); 
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Details.css\"></head>");
			
			HTMLMethods.popUpScript(out);
			
			out.println("<body>" +
					//insert url links to other servlets
					"<a href=\"/Fabflix/cart\">Checkout</a><br>\n" +
					"<a href=\"/Fabflix/search\">Continue browsing</a><br>\n" +
					"<a href=\"/Fabflix/logout\">Logout</a><br><br>"
			);
			
			Connection connection = ConnectionMethods.initialize(out);
			
			out.println("<table border=\"1\" style=\"table-layout:fixed; width: 100%; border: 1px solid black; padding: 15px;\">");
			
			//Movie details
			if(request.getParameter("movie") != null)
			{
				String movieID = request.getParameter("movie");
				Statement movieQuery = connection.createStatement();
				ResultSet movie = movieQuery.executeQuery("SELECT * FROM movies WHERE id = '" + movieID + "'");
				
				movie.next();
				boolean movieExists = false;
				if(movie.isFirst() && movie.isLast())
				{
					movieExists = true;
					out.println(
							"<th>\n <a href=\"/Fabflix/details?movie=" + movie.getString("id") +
								"\" onmouseover=\"ajaxGetDetails(" + movie.getString("id") + ")\" class=\"title\">" + movie.getString("title") + "</a>" +
							((movie.getString("banner_url") != null)?
									"<br>\n<img src=" + movie.getString("banner_url") + " width=62 height=70>" : ""));
					out.println((movie.getString("trailer_url") != null)?
									"<br>\n<a href=\"" + movie.getString("trailer_url") + "\">Trailer</a>" : "");
					out.println("</th><th>Stars</th><th>Genres</th>" + 
							"\n<tr><td>" +
							"\n<br>ID: " + movie.getInt("id") +
							"\n<br>Year Released: " + movie.getInt("year") +
							"\n<br>Director: " + movie.getString("director") + 
							"\n</td>"
					);	
				}
				else
				{
					out.println("<tr><td>Movie does not exist!</td></tr>");
				}
				movie.close();
				movieQuery.close();
				
				if(movieExists)
				{	
					Statement starQuery = connection.createStatement();
					ResultSet stars = starQuery.executeQuery(
							"SELECT first_name, last_name, stars.id" +
							" FROM stars" +
							" WHERE stars.id IN (" +
								" SELECT stars_in_movies.star_id" +
								" FROM stars_in_movies" +
								" WHERE stars_in_movies.movie_id = '" + movieID + "')");
					
					out.println("<td>");
					
					while(stars.next())
					{
						out.println(
								"\n\t<a href=\"/Fabflix/details?star=" + stars.getString("id") + "\">" +
								stars.getString("first_name") + " " + stars.getString("last_name") + "</a><br>"
						);
					}
					stars.close();
					starQuery.close();
					
					out.println("</td>");
					
					
					Statement genreQuery = connection.createStatement();
					ResultSet genres = genreQuery.executeQuery(
							"SELECT name" +
							" FROM genres" +
							" WHERE genres.id IN (" +
								" SELECT genres_in_movies.genre_id" +
								" FROM genres_in_movies" +
								" WHERE genres_in_movies.movie_id = '" + movieID + "')");
					
					out.println("<td>");
					
					while(genres.next())
					{
						out.println(
								"\n\t<a href=\"/Fabflix/movielist?genre=" + genres.getString("name") + "\">" +
								genres.getString("name") + "</a><br>"
						);
					}
					genres.close();
					genreQuery.close();
				}
				
				out.println("</td></tr></table>");
				
				if(movieExists)
				{
					out.println("\n\t<br><form action=\"/Fabflix/cart\" method=\"POST\">" +
							"\n\t\t<input type=\"hidden\" name=\"add\" value=\"" + movieID + "\">" +
							"\n\t\t<input type=\"submit\" value=\"Add to Cart\">" +
						"\n\t</form>");
				}
				
			}
			//Star details
			else if(request.getParameter("star") != null)
			{
				String starID = request.getParameter("star");
				Statement starQuery = connection.createStatement();
				ResultSet star = starQuery.executeQuery("SELECT * FROM stars WHERE id = '" + starID + "'");
				
				star.next();
				boolean starExists = false;
				if(star.isFirst() && star.isLast())
				{
					starExists = true;
					out.println(
							"<th>" + star.getString("first_name") + " " + star.getString("last_name") +
							((star.getString("photo_url") != null)?
									"<br><img src=" + star.getString("photo_url") + ">" : "") +
							"<br>ID: " + star.getInt("id")  + 
							((star.getDate("dob") != null)?
									"<br>Date of Birth: " + star.getDate("dob") + "</th>" : "</th>")
					);	
				}
				else
				{
					out.println("<tr><td>Star does not exist!</td></tr>");
				}
				star.close();
				starQuery.close();
				
				if(starExists)
				{
					Statement movieQuery = connection.createStatement();
					ResultSet movie = movieQuery.executeQuery(
							"SELECT title, id" +
							" FROM movies" +
							" WHERE movies.id IN (" +
								" SELECT stars_in_movies.movie_id" +
								" FROM stars_in_movies" +
								" WHERE stars_in_movies.star_id = '" + starID + "')");
					
					out.println("<td>");
					
					while(movie.next())
					{
						out.println(
								"\n\t<a href=\"/Fabflix/details?movie=" + movie.getInt("id") + "\">" +
								movie.getString("title") + "</a><br>"
						);
					}
					
					movie.close();
					movieQuery.close();
				}
				
				out.println("</td></table>");
			}
			//Error
			else
			{
				out.println("<p>Error: No movie or star was selected.</p>");
			}
			
			HTMLMethods.popUpDetail(out);
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			out.println("<html>" +
					"<head><title>" +
					"Details: Error" +
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