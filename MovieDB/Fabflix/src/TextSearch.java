import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextSearch extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getServletInfo()
	{
		return "Servlet used for text prefetch.";
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
			String text = request.getParameter("datalisttext");
			//text = text.trim().replaceAll(" ", "* ");
			/*String text2 = text.substring(0, text.length());
			text = text.trim().replaceAll(" ", "% ");
			text2 = text2.trim().replaceAll(" ", "% ");
			text = ("% ".concat(text)).concat("%");
			out.println("Text: " + text);
			out.println("Text2: "+ text2);
			text2 = text2.concat("%");
			String sql = "select title from movies where title like '".concat(text + "' or title like '" + text2 + "' limit 5");*/
			String sql = "SELECT title, MATCH(title) AGAINST('" + text.trim() + "*') as score FROM movies WHERE MATCH (title) AGAINST('" + text.trim() + "*') ORDER BY score DESC limit 5";
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			int i = 0;
			out.println("<datalist id=\"text\">");
			String[] strings = new String[5];
			while (rs.next() && i < 5)
			{
				strings[i] = rs.getString("title");
				out.println("<option value=\"" + rs.getString("title") + "\"/>");
				i++;
			}
			rs.close();
			statement.close();
			out.println("</datalist>");
			connection.close();
			for (i = 0; i < 5; i++)
			{
				if (strings[i] != null)
				{
					out.println("<p onclick=\"fillTextFunction(this.innerHTML)\">" + strings[i] + "</p>");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			out.println("<html>" +
					"<head><title>" +
					"TextSearch: Error" +
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