import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Checkout extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet asks for customer information to checkout out movies.";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try
		{
			out.println(
					"<html>\n<head>\n\t<title>Fabflix - Customer Information</title>" + 
						"\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Checkout.css\">\n</head>\n" +
					"<body>\n" +
						//insert url links to other servlets
						"\n<a href=\"/Fabflix/search\">Continue browsing</a>" +
						"\n<br><a href=\"/Fabflix/cart\">Go to Shopping Cart</a>" +
						"\n<br><a href=\"/Fabflix/logout\">Logout</a>" +
						"<br><h1>Checkout</h1>"
			);
			
			if(request.getParameter("proceed") != null)
			{
				out.println(
						"\n<br><br><table border=\"1\"><th>Customer Information</th>" +
						"\n<tr><td><form " +  
							"\n\taction=\"/Fabflix/confirm\" method=\"POST\">" +
							"\n\t<label>First Name:</label>\n\t\t<input type=\"text\" name=\"firstName\" autofocus required><br>" +
							"\n\t<label>Last Name:</label>\n\t\t<input type=\"text\" name=\"lastName\" required><br>" +
							"\n\t<label>Credit Card Number:</label>\n\t\t<input type=\"text\" name=\"ccid\" required><br>" +
							"\n\t<label>Card Expiration Date (YYYY-MM-DD):</label>\n\t\t<input type=\"text\" name=\"expiration\" required><br>" +
							"\n\t<br><input type=\"submit\" value=\"Confirm Checkout\"><br>" +
						"\n</form></td></tr></table>"
				);	
			}
			else
			{
				out.println("\n<p>Error: Must confirm items in shopping cart first.</p>");
			}
			
			out.println("\n</body>\n</html>");
		}
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