import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Logout extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet logs out current user.";
	}
	
	// Use http GET
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		try
		{
			// Remove customer attributes
			request.getSession().removeAttribute("userName");
			request.getSession().removeAttribute("userID");
			request.getSession().removeAttribute("userEmail");
			
			// Remove employee attributes
			request.getSession().removeAttribute("empName");
			request.getSession().removeAttribute("empEmail");
			
			// Remove privilege attributes
			request.getSession().removeAttribute("sUser");
			request.getSession().removeAttribute("sResource");
			request.getSession().removeAttribute("sSpecfic");
			request.getSession().removeAttribute("privList");
			
			out.println(
					"<html>\n<head>\n\t<title>Fabflix - Logout</title>" + 
						"<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/Login.css\">\n</head>\n" +
					"<body>\n" +
						"<p>You have been logged out.</p>" + 
						"\n<br><a href=\"Login.html\">Login</a>\n" +
					"</body>\n</html>"
			);
			
			request.getSession().invalidate();
			out.close();
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