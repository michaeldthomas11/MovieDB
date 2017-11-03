import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.http.*;

public class MovieToolTip extends HttpServlet
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

		try
		{
			Connection connection = ConnectionMethods.initialize(out);
			
			out.println("<table border=\"1\">");
			
			//Movie details
			if(request.getParameter("movieID") != null)
			{
				String movieID = request.getParameter("movieID");
				Statement movieQuery = connection.createStatement();
				ResultSet movie = movieQuery.executeQuery("SELECT * FROM movies WHERE id = '" + movieID + "'");
				
				movie.next();
				boolean movieExists = false;
				if(movie.isFirst() && movie.isLast())
				{
					movieExists = true;
					out.println("Title: " + movie.getString("title"));
					out.println((movie.getString("banner_url") != null)?
							"<br><img id=\"movieImage\" src=" + movie.getString("banner_url") + " width=62 height=70>" : "");
					out.println("<br>ID: " + movie.getInt("id"));
					out.println("<br>Year Released: " + movie.getInt("year"));
					out.println("<br>Director: " + movie.getString("director") + "<br>");
				}
				else
				{
					out.println("<p>Movie does not exist!</td></p>");
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
					
					out.println("<br>Stars:");
					
					while(stars.next())
					{
						out.println("<br>" + stars.getString("first_name") + " " + stars.getString("last_name"));
					}
					stars.close();
					starQuery.close();
						
					Statement genreQuery = connection.createStatement();
					ResultSet genres = genreQuery.executeQuery(
							"SELECT name" +
							" FROM genres" +
							" WHERE genres.id IN (" +
								" SELECT genres_in_movies.genre_id" +
								" FROM genres_in_movies" +
								" WHERE genres_in_movies.movie_id = '" + movieID + "')");
					
					out.println("<br><br>Genres:");
					
					while(genres.next())
					{
						out.println("<br>" + genres.getString("name"));
					}
					
					out.println("<br>");
					genres.close();
					genreQuery.close();
				}
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
