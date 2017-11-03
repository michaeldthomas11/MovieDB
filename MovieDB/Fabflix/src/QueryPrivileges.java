import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.*;

public class QueryPrivileges extends HttpServlet
{
	private static final long	serialVersionUID	= 1L;

	public String getServletInfo()
	{
		return "Servlet connects to MySQL database and allows user to management privileges.";
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		Connection connection = null;

		try
		{
			connection = ConnectionMethods.initialize(out);
			HttpSession session = request.getSession();
			
			out.println("<html><head><title>Fabflix - User Privilege Results</title></head><body>\n" +
					//insert url links to other servlets
					"<a href=\"/Fabflix/employee/menu\">Return to Menu</a><br>\n" + 
					"<a href=\"/Fabflix/logout\">Logout</a><br><br>" +
					"\n<br><h1>User Privilege Results</h1>");
			
			String user = (String) session.getAttribute("sUser");
			String resource = (String) session.getAttribute("sResource");
			//privlist is a list of ALL privileges of a resource
			HashMap<String, Boolean> privList = (HashMap<String, Boolean>) session.getAttribute("privList");
			
			//TODO: Query privileges in SQL
			
			if(resource.equals("User"))
			{
				//grant blah (read by userGrants and privList) on *.* to user@localhost (if grant option) with grant option;
				if(request.getParameterValues("user_priv") != null)
				{
					//array of (String) privileges granted
					//revoked == null
					//if value in privList = true && priv does not exist in granted[], then revoke
					//if value in privlist = true && (String) privileges is in granted[], do nothing
					//if value in privList = false && priv does not exist in granted[], do nothing
					//SPECIAL CASE: if grant option only, syntax will look like:
					//grant usage on *.* to user@localhost with grant option;
					Iterator<String> iterator = privList.keySet().iterator();
					String[] userGrants = request.getParameterValues("user_priv");
					String s;
					String queryGrant = "grant usage";
					String queryRevoke = "revoke usage";
					while (iterator.hasNext())
					{
						s = iterator.next();
						
						if (!s.equals("grant option"))
						{
							if (!privList.get(s) && contains(userGrants, s))
							{
								queryGrant = queryGrant.concat(", " + s);
							}
							else if (privList.get(s) && !contains(userGrants, s))
							{
								queryRevoke = queryRevoke.concat(", " + s);
							}
						}
						else
						{
							if (!privList.get("grant option") && contains(userGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on *.* to '" + user + "'@'localhost' with grant option");
								queryRevoke = queryRevoke.concat(" on *.* from '" + user + "'@'localhost'");
							}
							else if (privList.get("grant option") && !contains(userGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on *.* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(", grant option on *.* from '" + user + "'@'localhost'");
							}
							else
							{
								queryGrant = queryGrant.concat(" on *.* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(" on *.* from '" + user + "'@'localhost'");
							}
						}
					}
					
					Statement statement = connection.createStatement();
					statement.execute(queryGrant);
					statement.close();
					statement = connection.createStatement();
					statement.execute(queryRevoke);
					statement.close();
					
					out.print("NUMBER OF GRANTS FOR " + user + ": " + userGrants.length);
				}
				//revoke blah on *.* from user@localhost;
				//revoke blah, grant option on *.* from user@localhost;
				else //Revoke ALL
				{
					Statement statement = connection.createStatement();
					statement.execute("revoke all on *.* from '" + user + "'@localhost;");
					statement.close();
					statement = connection.createStatement();
					statement.execute("revoke grant option on *.* from '" + user + "'@localhost;");
					statement.close();
					out.print("NUMBER OF GRANTS FOR " + user + ": 0");
				}
				
			}
			//grant blah on specifc.* to user@localhost;
			else if (resource.equals("Database"))
			{
				String database = (String) session.getAttribute("sSpecific");	
				if(request.getParameterValues("db_priv") != null)
				{
					
					Iterator<String> iterator = privList.keySet().iterator();
					String[] dbGrants = request.getParameterValues("db_priv");
					String s;
					String queryGrant = "grant usage";
					String queryRevoke = "revoke usage";
					while (iterator.hasNext())
					{
						s = iterator.next();
						
						if (!s.equals("grant option"))
						{
							if (!privList.get(s) && contains(dbGrants, s))
							{
								queryGrant = queryGrant.concat(", " + s);
							}
							else if (privList.get(s) && !contains(dbGrants, s))
							{
								queryRevoke = queryRevoke.concat(", " + s);
							}
						}
						else
						{
							if (!privList.get("grant option") && contains(dbGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on " + database + ".* to '" + user + "'@'localhost' with grant option");
								queryRevoke = queryRevoke.concat(" on " + database + ".* from '" + user + "'@'localhost'");
							}
							else if (privList.get("grant option") && !contains(dbGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on " + database + ".* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(", grant option on " + database + ".* from '" + user + "'@'localhost'");
							}
							else
							{
								queryGrant = queryGrant.concat(" on " + database + ".* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(" on " + database + ".* from '" + user + "'@'localhost'");
							}
						}
					}
					
					Statement statement = connection.createStatement();
					statement.execute(queryGrant);
					statement.close();
					statement = connection.createStatement();
					statement.execute(queryRevoke);
					statement.close();
					
					out.print("NUMBER OF GRANTS ON DB, " + database + " FOR " + user + ": " + dbGrants.length);
				}
				else //Revoke ALL
				{
					Statement statement = connection.createStatement();
					statement.execute("revoke all on " + database + ".* from '" + user + "'@localhost;");
					statement.close();
					statement = connection.createStatement();
					statement.execute("revoke grant option on " + database + ".* from '" + user + "'@localhost;");
					statement.close();
					out.print("NUMBER OF GRANTS ON DB, " + database + " FOR " + user + ": 0");
				}
				
			}
			//grant blah on moviedb.specific to user@localhost;
			else if (resource.equals("Table"))
			{
				String table = (String) session.getAttribute("sSpecific");
				if(request.getParameterValues("table_priv") != null)
				{
					String[] tableGrants = request.getParameterValues("table_priv");
					String[] colGrants = request.getParameterValues("col_priv");
					ArrayList<String> deletedTables = new ArrayList<String>();
					String queryTableGrant = "grant usage";
					String queryTableRevoke = "revoke usage";
					String queryColumnGrant = "grant usage";
					String queryColumnRevoke = "revoke usage";
					String s;
					Iterator<String> iterator = privList.keySet().iterator();
					while (iterator.hasNext())
					{
						s = iterator.next();
						
						if (!s.equals("grant option"))
						{
							if (!privList.get(s) && contains(tableGrants, s))
							{
								queryTableGrant = queryTableGrant.concat(", " + s);
							}
							else if (privList.get(s) && !contains(tableGrants, s))
							{
								queryTableRevoke = queryTableRevoke.concat(", " + s);
								deletedTables.add(s);
							}
						}
						else
						{
							if (!privList.get("grant option") && contains(tableGrants, "grant option"))
							{
								queryTableGrant = queryTableGrant.concat(" on moviedb." + table + " to '" + user + "'@'localhost' with grant option");
								queryTableRevoke = queryTableRevoke.concat(" on moviedb." + table + " from '" + user + "'@'localhost'");
							}
							else if (privList.get("grant option") && !contains(tableGrants, "grant option"))
							{
								queryTableGrant = queryTableGrant.concat(" on moviedb." + table + " to '" + user + "'@'localhost'");
								queryTableRevoke = queryTableRevoke.concat(", grant option on moviedb." + table + " from '" + user + "'@'localhost'");
							}
							else
							{
								queryTableGrant = queryTableGrant.concat(" on moviedb." + table + " to '" + user + "'@'localhost'");
								queryTableRevoke = queryTableRevoke.concat(" on moviedb." + table + " from '" + user + "'@'localhost'");
							}
						}
					}
					
					Statement statement = connection.createStatement();
					statement.execute(queryTableGrant);
					statement.close();
					statement = connection.createStatement();
					statement.execute(queryTableRevoke);
					statement.close();
					
					if(tableGrants != null && colGrants != null)
					{
						iterator = privList.keySet().iterator();
						while (iterator.hasNext())
						{
							s = iterator.next();

							if (!s.equals("grant option"))
							{
								if (!privList.get(s) && contains(colGrants, s))
								{
									queryColumnGrant = queryColumnGrant.concat(", " + s);
								}
								else if (!this.partOf(deletedTables, s) && privList.get(s) && !contains(colGrants, s))
								{
									queryColumnRevoke = queryColumnRevoke.concat(", " + s);
								}
							}
							else
							{
								queryColumnGrant = queryColumnGrant.concat(" on moviedb." + table + " to '" + user + "'@'localhost'");
								queryColumnRevoke = queryColumnRevoke.concat(" on moviedb." + table + " from '" + user + "'@'localhost'");
							}
						}

						statement = connection.createStatement();
						statement.execute(queryColumnGrant);
						statement.close();
						statement = connection.createStatement();
						statement.execute(queryColumnRevoke);
						statement.close();
					}
					
					
					/*Iterator<String> iterator = privList.keySet().iterator();
					while(iterator.hasNext())
					{
						String s = iterator.next();
						if (!s.equals("grant option"))
						{
							if (privList.get(s) && contains(tableGrants, s))
							{
								
							}
							else if (!privList.get(s) && contains(tableGrants, s))
							{
								queryTableGrant = queryTableGrant.concat(", " + s);
							}
							else if (privList.get(s) && !contains(tableGrants, s))
							{
								queryTableRevoke = queryTableRevoke.concat(", " + s);
								if (contains(colGrants, s))
								{
									queryColumnGrant = queryColumnGrant.concat(", " + s);
								}
							}
							else
							{
								
							}
							
							
						}
						else
						{
							
						}
					}*/
					
					
					out.print("NUMBER OF GRANTS FOR TABLE, " + table + ": " + tableGrants.length);
					out.print("NUMBER OF GRANTS FOR COLUMN, " + table + ": " + colGrants.length);
					
					//SPECIAL CASE: if table priv == null && column priv != null
					//revoke priv from table first, then grant individual column privileges
				}
				else //Revoke ALL from table
				{
					Statement statement = connection.createStatement();
					statement.execute("revoke all on moviedb." + table + " from '" + user + "'@localhost;");
					statement.close();
					out.print("NUMBER OF GRANTS FOR USER: 0");
					if(request.getParameterValues("col_priv") != null)
					{
						//revoke table method, then grant specific to column
						
						//out.print("NUMBER OF GRANTS FOR COLUMN, " + table + ": " + colGrants.length);
					}
					else //Revoke ALL from table AND col
					{
						statement = connection.createStatement();
						statement.execute("revoke all on moviedb." + table + " from '" + user + "'@localhost;");
						statement.close();
					}
				}
			}
			//grant blah on (procedure? procedure : function) specific to user@localhost; 
			else if (resource.equals("Procedure"))
			{
				//specifc = "PROCEDURE moviedb.add_movie"
				String specific = (String) session.getAttribute("sSpecific");
				if(request.getParameterValues("proc_priv") != null)
				{
					String[] procGrants = request.getParameterValues("proc_priv");
					Iterator<String> iterator = privList.keySet().iterator();
					String s;
					String queryGrant = "grant usage";
					String queryRevoke = "revoke usage";
					while (iterator.hasNext())
					{
						s = iterator.next();
						
						if (!s.equals("grant option"))
						{
							if (!privList.get(s) && contains(procGrants, s))
							{
								queryGrant = queryGrant.concat(", " + s);
							}
							else if (privList.get(s) && !contains(procGrants, s))
							{
								queryRevoke = queryRevoke.concat(", " + s);
							}
						}
						else
						{
							if (!privList.get("grant option") && contains(procGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on " + specific + " to '" + user + "'@'localhost' with grant option");
								queryRevoke = queryRevoke.concat(" on " + specific + ".* from '" + user + "'@'localhost'");
							}
							else if (privList.get("grant option") && !contains(procGrants, "grant option"))
							{
								queryGrant = queryGrant.concat(" on " + specific + ".* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(", grant option on " + specific + ".* from '" + user + "'@'localhost'");
							}
							else
							{
								queryGrant = queryGrant.concat(" on " + specific + ".* to '" + user + "'@'localhost'");
								queryRevoke = queryRevoke.concat(" on " + specific + ".* from '" + user + "'@'localhost'");
							}
						}
					}
					
					Statement statement = connection.createStatement();
					statement.execute(queryGrant);
					statement.close();
					statement = connection.createStatement();
					statement.execute(queryRevoke);
					statement.close();
					
					
					out.print("NUMBER OF GRANTS FOR PROC, : " + specific + ": " + procGrants.length);
				}
				else //Revoke ALL
				{
					Statement statement = connection.createStatement();
					statement.execute("revoke all on  " + specific + " from '" + user + "'@localhost;");
					statement.close();
					statement = connection.createStatement();
					statement.execute("revoke grant option on " + specific + ".* from '" + user + "'@localhost;");
					statement.close();
					out.print("NUMBER OF GRANTS ON " + specific + " FOR " + user + " : 0");
				}
			}
			
			session.removeAttribute("sUser");
			session.removeAttribute("sResource");
			session.removeAttribute("sSpecfic");
			session.removeAttribute("privList");
			
			HTMLMethods.printMovieListClosing(connection, out);
		}
		catch(Exception ex)
		{
			out.println("<html>" +
					"<head><title>" +
					"Search and Browse: Error" +
					"</title></head>\n<body>" +
					"<p>Error in doGet: " +
					ex.getMessage() + "</p></body></html>");
			return;
		}
		finally
		{
			try
			{
				out.close();
				if(connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				out.println("<html>" +
						"<head><title>" +
						"Search and Browse: Error" +
						"</title></head>\n<body>" +
						"<p>Error in doGet: " +
						e.getMessage() + "</p></body></html>");
				return;
			}
		}
	}

	private boolean partOf(ArrayList<String> deletedTables, String s)
	{
		for (int i = 0; i < deletedTables.size(); i++)
		{
			if (deletedTables.get(i).contains(s))
			{
				return true;
			}
		}
		return false;
	}

	private int getPosition(String[] userGrants, String s)
	{
		for (int i = 0; i < userGrants.length; i++)
		{
			if (userGrants[i].equals(s))
			{
				return i;
			}
		}
		return -1;
	}

	private boolean contains(String[] userGrants, String string)
	{
		for (int i = 0; i < userGrants.length; i++)
		{
			if (userGrants[i].equals(string))
			{
				return true;
			}
		}
		return false;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		doGet(request, response);
	}
}