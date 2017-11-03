import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class CheckAuth implements Filter 
{
	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse,
			FilterChain chain) throws IOException, ServletException
	{
		try
		{
			HttpServletRequest request = (HttpServletRequest) sRequest;
			HttpServletResponse response = (HttpServletResponse) sResponse;
			HttpSession session = request.getSession(true);

			/*if (request.getRequestURI().endsWith(".css"))
			{
			    chain.doFilter(sRequest, sResponse);
			    return;
			}
			
			if(request.getParameter("enter") != null ||
					request.getRequestURL().toString().endsWith("/Fabflix") ||
					request.getRequestURL().toString().endsWith("/Fabflix/Login.html"))
			{
				sRequest.setAttribute("enter", "true");
				sRequest.setAttribute("userEmail", null);
				sRequest.setAttribute("userName", null);
				sRequest.setAttribute("userID", null);
				chain.doFilter(sRequest, sResponse);
			}
			else if(session.getAttribute("empName") != null && request.getRequestURL().toString().endsWith("/logout"))
			{
				chain.doFilter(sRequest, sResponse);
			}
			else if(session.getAttribute("userEmail") == null &&
					(request.getParameter("enter") == null || request.getAttribute("enter") == null)) //Login Results
			{
				response.sendRedirect("Login.html");
			}
			else
			{
				chain.doFilter(sRequest, sResponse);
			}*/
			chain.doFilter(sRequest, sResponse);
		}
		catch (Exception e)
		{
			
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
	}

}
