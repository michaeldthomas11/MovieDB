import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class HTMLMethods
{
	private static final int	startingPageValue	= 5;
	private static final int	startingCurrentPosition	= 0;
	private static HttpServletRequest req;
	private static HttpSession ses;
	public static final String ASC_TITLE = "ASC_TITLE";
	public static final String DESC_TITLE = "DESC_TITLE";
	public static final String ASC_YEAR = "ASC_YEAR";
	public static final String DESC_YEAR = "DESC_YEAR";
	
	public static void printMovieListHeader(PrintWriter out)
	{
		out.println("<html>");
		out.println("<head>");
		javaScriptMethod(out);
		out.println("<title>Fabflix - Search and Browse</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/MovieList.css\">");
		out.println("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js\"></script>");
		out.println("</head>");
		out.println("<body>");
	}
	
	public static void popUpScript(PrintWriter out)
	{
		out.println("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
		out.println("<script type=\"text/javascript\" src=\"http://code.jquery.com/ui/1.10.1/jquery-ui.js\"></script>");
		
		out.println("<script>");
		out.println("$(document).ready(function() {");
		out.println("\t\t$(\"#movieDetails\").hide();");
		
		out.println("\t\t$(\".title\").hover(function (event) {");
		out.println("\t\t\t$(\"#movieDetails\").stop().fadeTo('fast',0.0);");
		out.println("\t\t\t$(\"#movieDetails\").css({");
		out.println("\t\t\t\ttop: event.pageY,");
		out.println("\t\t\t\tleft: event.pageX");
		out.println("\t\t\t});");
		out.println("\t\t\t$(this).hide();");
		out.println("\t\t\t$(\"#movieDetails\").stop();");
		out.println("\t\t});");
		
		out.println("\t\t$(\".title\").mouseout(function () {");
		out.println("\t\t\t$(\"#movieDetails\").fadeTo('fast',1);");
		out.println("\t\t\t$(this).show();");
		out.println("\t\t\t$(this).stop();");
		out.println("\t\t});");
		
		out.println("\t\t$(\"#movieDetails\").mouseout(function() {");
		out.println("\t\t\t$(\"#movieDetails\").hide();");
		out.println("\t\t});");
		
		out.println("});");
		out.println("</script>");
					
		out.println("<script>");
		out.println("\tfunction ajaxGetDetails(id) {");
		out.println("\t\tvar ajaxRequest;");
		out.println("\t\ttry { ajaxRequest = new XMLHttpRequest(); }");
		out.println("\t\t\tcatch (e) { try { ajaxRequest = new ActiveXObject (\"Msxml2.XMLHTTP\"); }");
		out.println("\t\t\tcatch (e) { try { ajaxRequest = new ActiveXObject(\"Microsoft.XMLHTTP\"); }");
		out.println("\t\t\tcatch (e) { alert(\"Your browser broke!\"); return false; } } }");
		out.println("\t\tajaxRequest.onreadystatechange = function() {");
		out.println("\t\tif(ajaxRequest.readyState == 4) {");
		out.println("\t\tdocument.getElementById(\"movieDetails\").innerHTML = ajaxRequest.responseText; } }");
		out.println("\t\tajaxRequest.open(\"GET\", \"/Fabflix/tooltip?movieID=\" + id, true);");
		out.println("\t\tajaxRequest.send(null);\n\t}");
		out.println("</script>");		
	}
	
	
	public static void javaScriptMethod(PrintWriter out)
	{
		out.println("\n\t<script src=\"http://code.jquery.com/jquery-1.9.1.js\"></script>"+
		"\n\t<script src=\"http://code.jquery.com/ui/1.10.1/jquery-ui.js\"></script>"+
		"\n\t<script>"+
		"\n\tfunction ajaxFunction(str){"+
			"\n\tvar ajaxRequest;  // The variable that makes Ajax possible!"+

			"\n\ttry{"+
				"\n\t// Opera 8.0+, Firefox, Safari"+
				"\n\tajaxRequest = new XMLHttpRequest();"+
				"\n\t} catch (e){"+
				"\n\t// Internet Explorer Browsers"+
				"\n\ttry{"+
					"\n\tajaxRequest = new ActiveXObject(\"Msxml2.XMLHTTP\");"+
					"\n\t} catch (e) {"+
					"\n\ttry{"+
						"\n\tajaxRequest = new ActiveXObject(\"Microsoft.XMLHTTP\");"+
						"\n\t} catch (e){"+
						"\n\t// Something went wrong"+
						"\n\talert(\"Your browser broke!\");"+
						"\n\treturn false;"+
						"\n\t}"+
					"\n\t}"+
				"\n\t}"+
			"\n\t// Create a function that will receive data sent from the server"+
			"\n\tajaxRequest.onreadystatechange = function(){"+
				"\n\tif(ajaxRequest.readyState == 4){"+
					"\n\tdocument.getElementById(\"datalistdiv\").innerHTML = ajaxRequest.responseText;"+
					"\n\t}"+
				"\n\t}"+
			"\n\tajaxRequest.open(\"GET\", \"/Fabflix/servlet/TextSearch?datalisttext=\"+str, true);"+
			"\n\tajaxRequest.send(null);"+
		"\n\t}"+
		"\n\t</script>" +
		
		"\n\t<script>"+
		"\n\tfunction fillTextFunction(str){"+
					"\n\tdocument.getElementById(\"textId\").value = str;"+
		"\n\t}"+
		"\n\t</script>");
	}
	
	
	
	public static HttpSession loadMovieListSessionInformation(HttpServletRequest request, PrintWriter out)
	{
		HttpSession session = request.getSession();
		ses = session;
		req = request;
		if (session.getAttribute("MovieTitleAndYear") == null)
		{
			if (request.getParameter("MovieTitleAndYear") == null)
			{
				session.setAttribute("MovieTitleAndYear", ASC_TITLE);
			}
			else
			{
				session.setAttribute("MovieTitleAndYear", request.getParameter("MovieTitleAndYear"));
			}
		}
		else
		{
			if (request.getParameter("MovieTitleAndYear") != null)
			{
				session.setAttribute("MovieTitleAndYear", request.getParameter("MovieTitleAndYear"));
			}
		}
		if (session.getAttribute("Current") == null)
		{
			session.setAttribute("Current", HTMLMethods.startingCurrentPosition);
		}
		
		if (request.getParameter("numberOfMovies") == null && session.getAttribute("numberOfMovies") == null)
		{
			session.setAttribute("numberOfMovies", HTMLMethods.startingPageValue);
		}
		else if (request.getParameter("numberOfMovies") != null)
		{
			if (request.getParameter("numberOfMovies").equals("") && session.getAttribute("numberOfMovies") == null)
			{
				session.setAttribute("numberOfMovies", HTMLMethods.startingPageValue);
			}
			else if (!request.getParameter("numberOfMovies").equals(""))
			{
				session.setAttribute("Current", HTMLMethods.startingCurrentPosition);
				session.setAttribute("numberOfMovies", Integer.parseInt(request.getParameter("numberOfMovies")));
			}
		}

		if (request.getParameter("Button") != null)
		{
			if (((int) session.getAttribute("Current") - (int) session.getAttribute("numberOfMovies")) >= 0 && request.getParameter("Button").equals("Previous"))
			{
				session.setAttribute("Current", ((int) session.getAttribute("Current") - (int) session.getAttribute("numberOfMovies")));
			}
			else if (request.getParameter("Button").equals("Previous"))
			{
				session.setAttribute("Current", HTMLMethods.startingCurrentPosition);
			}
			else if (request.getParameter("Button").equals("Next"))
			{
				session.setAttribute("Current", ((int) session.getAttribute("Current") + (int) session.getAttribute("numberOfMovies")));
			}
		}
		return session;
	}
	
	
	public static void printMovieListShoppingCartLink(PrintWriter out)
	{
		out.println("\n<a href=\"/Fabflix/cart\">Checkout</a>");
	}
	
	
	public static void printMovieListLogOutLink(PrintWriter out)
	{
		out.println("\n<br><a href=\"/Fabflix/logout\">Logout</a>\n<br><h1>Search and Browse</h1>");	
	}
	
	
	public static void printMovieListWelcomeLabel(HttpServletRequest request, PrintWriter out)
	{
		out.println("<br>Welcome, " + request.getSession().getAttribute("userName") + "!<br>");
	}
	
	
	public static void printMovieListSearchMovieForm(HttpServletRequest request, PrintWriter out)
	{
		out.println("<table border=\"0\" style=\"table-layout:fixed; width: 100%; border: 1px solid black; padding: 15px;\">");
		out.println("<th>Search Movie</th><th>Browse by Genre</th><th>Browse by Title</th>");
		out.println("<tr><td><form action=\"http://localhost:9080/proxy/Fabflix/movielist\" method=\"GET\">");
		out.println("<label>Title:</label><input list=\"text\" id= \"textId\" onkeyup=\"ajaxFunction(this.value)\" name=\"title\" value=\"\"/></BR>");
		out.println("<div id=\"datalistdiv\">");
		out.println("</div>");
		out.println("<label>Year:</label>				<input type=\"text\" 	name=\"year\"			value=\"\"><BR>");
		out.println("<label>Director:</label>			<input type=\"text\" 	name=\"director\"		value=\"\"><BR>");
		out.println("<label>Star First Name:</label> 	<input type=\"text\" 	name=\"starFirstName\"	value=\"\"><BR>");
		out.println("<label>Star Last Name:</label> 	<input type=\"text\" 	name=\"starLastName\"	value=\"\"><BR>");
		out.println("<input type=\"submit\" value=\"Search\"><BR>");
		out.println("</form></td>");
	}
	
	
	public static void printMovieListSearchGenreForm(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException
	{
		out.println("<br><td><br>");
		out.println("<form action=\"/Fabflix/movielist\" method=\"GET\"> <select name=\"genre\">");
		out.println("<option value=\"\" selected> --- </option>");
		Statement genreSelect = connection.createStatement();
		ResultSet genres = genreSelect.executeQuery("" +
				"SELECT DISTINCT name" +
				" FROM genres" +
				" ORDER BY name ASC");
		while(genres.next())
		{	
			out.println("<option name=\"" + genres.getString("name") + "\">" + genres.getString("name") + "</option>");
		}
		genres.close();
		genreSelect.close();
		out.println("</select> <input type=\"submit\" value=\"Go\"> </form></td>");
	}
	
	
	public static void printMovieListSearchTitleForm(HttpServletRequest request, PrintWriter out)
	{
		out.println("<br><td><br>");
		out.println("<form action=\"/Fabflix/movielist\" method=\"GET\">");
		out.println("<select name=\"movie\">");
		out.println("<option value=\"\" selected> --- </option>");
		char letter = '0';
		for(int i = 0; i < 10; i++)
		{	
			out.println("<option value=\"" + letter + "\">" + letter + "</option>");
			letter++;
		}
		
		letter = 'A';
		for(int i = 0; i < 26; i++)
		{	
			out.println("<option value=\"" + letter + "\">" + letter + "</option>");
			letter++;
		}
		
		out.println("</select> <input type=\"submit\" value=\"Go\"> </form></td></tr></table>");
	}
	
	
	public static void printMovieListDetermineNumberOfMoviesToBeShownForm(HttpServletRequest request, PrintWriter out)
	{
		out.println("<label>Number of Movies Per Page</label><br>");
		out.println("<form action=\"/Fabflix/movielist\" method=\"GET\"> <select name=\"numberOfMovies\">");
		out.println("<option value=\"\" selected> --- </option>");
		out.println("<option value=\"5\">5</option>");
		out.println("<option value=\"10\">10</option>");
		out.println("<option value=\"25\">25</option>");
		out.println("<option value=\"50\">50</option>");
		out.println("<option value=\"100\">100</option>");
		out.println("<option value=\"200\">200</option>");
		out.println("</select> <input type=\"submit\" value=\"Go\"> </form>");
	}


	public static void printMovieListButtonsForm(HttpServletRequest request,
			PrintWriter out)
	{
		out.println("<form action=\"/Fabflix/movielist\" method=\"get\">");
		out.println("<button name=\"Button\" type=\"submit\" value=\"Previous\">Previous</button>");
		out.println("<button name=\"Button\" type=\"submit\" value=\"Next\">Next</button>");
		out.println("</form>");
	}
	
	public static void printDataTable(PrintWriter out, ArrayList<Movie> movies)
	{	
		// Create the header of the table.
		out.println("<table border=\"1\" style=\"table-layout:fixed; width: 100%; border: 1px solid black; padding: 15px;\">");
		out.println("<tr>");
		out.println("<th>Movie</th>");
		out.println("<th>Stars</th>");
		out.println("<th>Genre</th>");
		out.println("</tr>");
		
		// Create the data for the table.
		for (int i = 0; i < movies.size(); i++)
		{
			out.println("\n<tr>\n\t<td>" +
					"\n\t<img src=" + movies.get(i).getBanner_url() + "width=62 height=70>" +
					"\n\t<br>ID: " + movies.get(i).getId() + 
					"\n\t<br>Title: " +
						"<a href=\"/Fabflix/details?movie=" + movies.get(i).getId()  +
						"\" onmouseover=\"ajaxGetDetails(" + movies.get(i).getId() + ")\" class=\"title\">" + movies.get(i).getTitle() + "</a>" +  
					"\n\t<br>Year Released: " + movies.get(i).getYear() + 
					"\n\t<br>Director: " + movies.get(i).getDirector()  +
					"\n\t<br><form action=\"/Fabflix/cart\" method=\"POST\">" +
						"\n\t\t<input type=\"hidden\" name=\"add\" value=\"" + movies.get(i).getId() + "\">" +
						"\n\t\t<input type=\"submit\" value=\"Add to Cart\">" +
					"\n\t</form>\n\t</td>"
			);
			
			out.println("<td>");
			for (int j = 0; j < movies.get(i).getStarSize(); j++)
			{
				out.println("<a href=\"/Fabflix/details?star=" + movies.get(i).getStar(j).getId() + "\">" + movies.get(i).getStars(j) + "</a> <br>");
			}
			out.println("</td>");
			out.println("<td>");
			for (int j = 0; j < movies.get(i).getGenreSize(); j++)
			{
				out.println(movies.get(i).getGenres(j) + "<br>");
			}
			out.println("</td>");
			out.println("</tr><br>\n");
		}
	}
	
	public static void printMovieListClosing(Connection connection, PrintWriter out) throws SQLException
	{
		connection.close();
		out.println("</body></html>");
		out.close();
	}


	public static void printMovieListAscendingDescendingOrderLinks(
			HttpServletRequest request, PrintWriter out)
	{
		if (request.getParameter("MovieTitleAndYear") == null)
		{
			out.print("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + ASC_TITLE + "\">ASCENDING TITLE</a> ");
			out.println("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + DESC_YEAR + "\">DESCENDING YEAR</a>");
		}
		else if (request.getParameter("MovieTitleAndYear").equals(ASC_TITLE))
		{
			out.print("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + DESC_TITLE + "\">DESCENDING TITLE</a> ");
			out.println("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + DESC_YEAR + "\">DESCENDING YEAR</a>");
		}
		else if (request.getParameter("MovieTitleAndYear").equals(DESC_TITLE))
		{
			out.print("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + ASC_TITLE + "\">ASCENDING TITLE</a> ");
			out.println("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + DESC_YEAR + "\">DESCENDING YEAR</a>");
		}
		else if (request.getParameter("MovieTitleAndYear").equals(ASC_YEAR))
		{
			out.print("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + ASC_TITLE + "\">ASCENDING TITLE</a> ");
			out.println("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + DESC_YEAR + "\">DESCENDING YEAR</a>");
		}
		else if (request.getParameter("MovieTitleAndYear").equals(DESC_YEAR))
		{
			out.print("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + ASC_TITLE + "\">ASCENDING TITLE</a> ");
			out.println("<a href=\"/Fabflix/movielist?MovieTitleAndYear=" + ASC_YEAR + "\">ASCENDING YEAR</a>");
		}
	}
	
	public static void popUpDetail(PrintWriter out)
	{
		out.println("<div id=\"movieDetails\"" +
				" style=\"position: absolute;" +
						" border: 2px solid #ccc;" +
						" background-color: #818181;" +
						" max-width: 300;" +
						" padding: 15px;\">"
		);
		out.println("</div>");
	}
}
