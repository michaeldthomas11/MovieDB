import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class MovieList extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet displays search AND browsing results.";
	}
	// Use http GET
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try
		{
			Connection connection = ConnectionMethods.initialize(out);
			HTMLMethods.printMovieListHeader(out);
			//insert url links to other servlets
			HTMLMethods.popUpScript(out);
			HTMLMethods.printMovieListWelcomeLabel(request, out);
			HTMLMethods.printMovieListShoppingCartLink(out);
			HTMLMethods.printMovieListLogOutLink(out);
			HTMLMethods.printMovieListSearchMovieForm(request, out);
			HTMLMethods.printMovieListSearchGenreForm(request, out, connection);
			HTMLMethods.printMovieListSearchTitleForm(request, out);
			HTMLMethods.printMovieListButtonsForm(request, out);
			HTMLMethods.printMovieListDetermineNumberOfMoviesToBeShownForm(request, out);
			HTMLMethods.printMovieListAscendingDescendingOrderLinks(request, out);
			//MovieList results
			MovieCollection movies = new MovieCollection(QueryMethods.createMovieList(connection, QueryMethods.buildQuery(request, QueryMethods.TITLE)), QueryMethods.createMovieList(connection, QueryMethods.buildQuery(request, QueryMethods.YEAR)));
			// Create Header Table of query results.
			HttpSession session = HTMLMethods.loadMovieListSessionInformation(request, out);
			if (session.getAttribute("MovieCollection") != null && request.getParameter("title") == null && request.getParameter("genre") == null && request.getParameter("movie") == null)
			{
				movies = (MovieCollection) session.getAttribute("MovieCollection");
			}
			session.setAttribute("MovieCollection", movies);
			if (session.getAttribute("MovieTitleAndYear").equals(HTMLMethods.ASC_TITLE))
			{
				HTMLMethods.printDataTable(out, movies.getForwardTraverseTitle((int) session.getAttribute("Current"), (int) session.getAttribute("numberOfMovies")));
			}
			else if (session.getAttribute("MovieTitleAndYear").equals(HTMLMethods.DESC_TITLE))
			{
				HTMLMethods.printDataTable(out, movies.getReverseTraverseTitle((int) session.getAttribute("Current"), (int) session.getAttribute("numberOfMovies")));
			}
			else if (session.getAttribute("MovieTitleAndYear").equals(HTMLMethods.DESC_YEAR))
			{
				HTMLMethods.printDataTable(out, movies.getForwardTraverseDate((int) session.getAttribute("Current"), (int) session.getAttribute("numberOfMovies")));
			}
			else if (session.getAttribute("MovieTitleAndYear").equals(HTMLMethods.ASC_YEAR))
			{
				HTMLMethods.printDataTable(out, movies.getReverseTraverseDate((int) session.getAttribute("Current"), (int) session.getAttribute("numberOfMovies")));
			}
			out.println("Position: " + (int) session.getAttribute("Current") + " out of " + movies.getSize());
			
			HTMLMethods.popUpDetail(out);
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			out.println("<html>" +
					"<head><title>" +
					"MovieList: Error" +
					"</title></head>\n<body>" +
					"<p>Error in doGet: " +
					e.getMessage() + "</p></body></html>");
			return;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		doGet(request, response);
	}
}