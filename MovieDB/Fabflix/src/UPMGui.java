import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;

public class UPMGui extends HttpServlet
{
	/**
	 * 
	 */
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
			
			Statement stmt = connection.createStatement();
			ResultSet grant = stmt.executeQuery("Show grants for '" + session.getAttribute("empName") + "'@'localhost'");
			boolean hasGrant = false;
			checkGrant: while(grant.next())
			{
				if(grant.getString(1).contains("*.*") && grant.getString(1).contains("WITH GRANT OPTION"))
				{
					hasGrant = true;
					break checkGrant;
				}
			}
			grant.close();
			stmt.close();
			
			out.println("<html><head><title>Fabflix - User Privilege Management</title></head><body>\n" +
						//insert url links to other servlets
						"<a href=\"/Fabflix/employee/menu\">Return to Menu</a><br>\n" + 
						"<a href=\"/Fabflix/logout\">Logout</a><br><br>" +
						"\n<br><h1>User Privilege Management</h1>");
			
			// If user has no granting privileges
			if(!hasGrant)
			{
				out.println("<p>Sorry! You do not have the necessary privileges to enter.</p>");
			}
			else
			{
				if(request.getParameter("user") != null)
					session.setAttribute("sUser", request.getParameter("user"));
				if(request.getParameter("resource") != null)
					session.setAttribute("sResource", request.getParameter("resource"));
				if(request.getParameter("specific") != null)
					session.setAttribute("sSpecific", request.getParameter("specific"));
				else
					session.setAttribute("sSpecific", null);
				
				out.println("<table border=\"0\"><tr>");
				
				// USER LIST
				out.println("<td><form action=\"/Fabflix/employee/userPrivileges\" method=\"POST\" id=\"staticForm\">" +
						"<fieldset><legend>Employee Select:</legend><select name=\"user\" size=\"10\">");
				stmt = connection.createStatement();
				ResultSet userList = stmt.executeQuery("SELECT fullname, email from moviedb.employees");
				while(userList.next())
				{
					out.println("<option value=\"" + userList.getString(1) + "\"" +
							(session.getAttribute("sUser") != null?
									(session.getAttribute("sUser").equals(userList.getString(1))?
											" selected>" : ">")
									: ">") +
							userList.getString(1) + "  :  " + userList.getString(2) + "<br>");
				}
				userList.close();
				stmt.close();
				out.println("</select></fieldset><br></td>");
				
				// RESOURCE LIST
				out.println("<td><fieldset><legend>Resource Select:</legend><select name=\"resource\" size=\"10\" form=\"staticForm\">");
				out.println("<br><option value=\"User\"" +
						(session.getAttribute("sResource") != null?
								(session.getAttribute("sResource").equals("User")?
										" selected>" : ">")
								: ">") +
						"Users");
				out.println("<br><option value=\"Database\"" +
						(session.getAttribute("sResource") != null?
								(session.getAttribute("sResource").equals("Database")?
										" selected>" : ">")
								: ">") +
						"Databases");
				out.println("<br><option value=\"Table\"" +
						(session.getAttribute("sResource") != null?
								(session.getAttribute("sResource").equals("Table")?
										" selected>" : ">")
								: ">") +
						"Tables");
				out.println("<br><option value=\"Procedure\"" +
						(session.getAttribute("sResource") != null?
								(session.getAttribute("sResource").equals("Procedure")?
										" selected>" : ">")
								: ">") +
						"Stored Procedures");
				out.println("</select></fieldset><input type=\"submit\" value=\"Next\"></form></td>");
				
				specificSelect(out, connection, session);
				specificPrivilegeSelect(out, connection, session);
				
				out.println("</table>");
			}
			
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

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		doGet(request, response);
	}
	
	private void specificSelect(PrintWriter out, Connection connection, HttpSession session)
	{
		try
		{
			 String user = (String) session.getAttribute("sUser");
			 String resource = (String) session.getAttribute("sResource");
			
			if(user != null && resource != null)
			{
				//CHECK GRANT OPTIONS
				
				if(resource.equals("User"))
				{
					//USER *.* PRIVILEGES, SAVE
					out.println("</tr><tr><td rowspan=\"2\"><form action=\"/Fabflix/employee/queryResults\" method=\"POST\" id=\"userForm\">" +
							"<fieldset><legend>User Privileges:</legend>");
					Statement checkPrivileges = connection.createStatement();
					ResultSet priv = checkPrivileges.executeQuery(
							"SELECT privilege_type" +
							" FROM information_schema.user_privileges" +
							" WHERE grantee = \"'" + user + "'@'localhost'\"");
					
					ArrayList<String> granted = new ArrayList<String>();
					
					while(priv.next())
						granted.add(priv.getString(1));
					
					priv.close();
					checkPrivileges.close();
					
					HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
					
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"select\"" + 
							(granted.contains("SELECT")? "checked>" : ">") + "Select<br>");
						privileges.put("select", granted.contains("SELECT"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"insert\"" + 
							(granted.contains("INSERT")? "checked>" : ">") + "Insert<br>");
						privileges.put("insert", granted.contains("INSERT"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"update\"" + 
							(granted.contains("UPDATE")? "checked>" : ">") + "Update<br>");
						privileges.put("update", granted.contains("UPDATE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"delete\"" + 
							(granted.contains("DELETE")? "checked>" : ">") + "Delete<br>");
						privileges.put("delete", granted.contains("DELETE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create\"" + 
							(granted.contains("CREATE")? "checked>" : ">") + "Create<br>");
						privileges.put("create", granted.contains("CREATE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"drop\"" + 
							(granted.contains("DROP")? "checked>" : ">") + "Drop<br>");
						privileges.put("drop", granted.contains("DROP"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"reload\"" + 
							(granted.contains("RELOAD")? "checked>" : ">") + "Reload<br>");
						privileges.put("reload", granted.contains("RELOAD"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"shutdown\"" + 
							(granted.contains("SHUTDOWN")? "checked>" : ">") + "Shutdown<br>");
						privileges.put("shutdown", granted.contains("SHUTDOWN"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"process\"" + 
							(granted.contains("PROCESS")? "checked>" : ">") + "Process<br>");
						privileges.put("process", granted.contains("PROCESS"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"file\"" + 
							(granted.contains("FILE")? "checked>" : ">") + "File<br>");
						privileges.put("file", granted.contains("FILE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"references\"" + 
							(granted.contains("REFERENCES")? "checked>" : ">") + "References<br>");
						privileges.put("references", granted.contains("REFERENCES"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"index\"" + 
							(granted.contains("INDEX")? "checked>" : ">") + "Index<br>");
						privileges.put("index", granted.contains("INDEX"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"alter\"" + 
							(granted.contains("ALTER")? "checked>" : ">") + "Alter<br>");
						privileges.put("alter", granted.contains("ALTER"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"show databases\"" + 
							(granted.contains("SHOW DATABASES")? "checked>" : ">") + "Show databases<br>");
						privileges.put("show databases", granted.contains("SHOW DATABASES"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"super\"" + 
							(granted.contains("SUPER")? "checked>" : ">") + "Super<br>");
						privileges.put("super", granted.contains("SUPER"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create temporary tables\"" + 
							(granted.contains("CREATE TEMPORARY TABLES")? "checked>" : ">") + "Create temporary tables<br>");
						privileges.put("create temporary tables", granted.contains("CREATE TEMPORARY TABLES"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"lock tables\"" + 
							(granted.contains("LOCK TABLES")? "checked>" : ">") + "Lock tables<br>");
						privileges.put("lock tables", granted.contains("LOCK TABLES"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"execute\"" + 
							(granted.contains("EXECUTE")? "checked>" : ">") + "Execute<br>");
						privileges.put("execute", granted.contains("EXECUTE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"replication slave\"" + 
							(granted.contains("REPLICATION SLAVE")? "checked>" : ">") + "Replication slave<br>");
						privileges.put("replication slave", granted.contains("REPLICATION SLAVE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"replication client\"" + 
							(granted.contains("REPLICATION CLIENT")? "checked>" : ">") + "Replication Client<br>");
						privileges.put("replication client", granted.contains("REPLICATION CLIENT"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create view\"" + 
							(granted.contains("CREATE VIEW")? "checked>" : ">") + "Create view<br>");
						privileges.put("create view", granted.contains("CREATE VIEW"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"show view\"" + 
							(granted.contains("SHOW VIEW")? "checked>" : ">") + "Show view<br>");
						privileges.put("show view", granted.contains("SHOW VIEW"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create routine\"" + 
							(granted.contains("CREATE ROUTINE")? "checked>" : ">") + "Create routine<br>");
						privileges.put("create routine", granted.contains("CREATE ROUTINE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"alter routine\"" + 
							(granted.contains("ALTER ROUTINE")? "checked>" : ">") + "Alter routine<br>");
						privileges.put("alter routine", granted.contains("ALTER ROUTINE"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create user\"" + 
							(granted.contains("CREATE USER")? "checked>" : ">") + "Create user<br>");
						privileges.put("create user", granted.contains("CREATE USER"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"event\"" + 
							(granted.contains("EVENT")? "checked>" : ">") + "Event<br>");
						privileges.put("event", granted.contains("EVENT"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"trigger\"" + 
							(granted.contains("TRIGGER")? "checked>" : ">") + "Trigger<br>");
						privileges.put("trigger", granted.contains("TRIGGER"));
						
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"create tablespace\"" + 
							(granted.contains("CREATE TABLESPACE")? "checked>" : ">") + "Create tablespace<br>");
						privileges.put("create tablespace", granted.contains("CREATE TABLESPACE"));
					
					checkPrivileges = connection.createStatement();
					priv = checkPrivileges.executeQuery("show grants for '" + user + "'@'localhost'");
					boolean hasGrantOption = false;
					checkGrant: while(priv.next())
					{
						if(priv.getString(1).contains("*.*") && priv.getString(1).contains("WITH GRANT OPTION"))
						{
							hasGrantOption = true;
							break checkGrant;
						}
					}
					
					priv.close();
					checkPrivileges.close();
					
					out.println("<input type=\"checkbox\" name=\"user_priv\" value=\"grant option\"" + 
							(hasGrantOption? "checked>" : ">") + "Grant option<br>");
						privileges.put("grant option", hasGrantOption);
					
					session.setAttribute("privList", privileges);
					
					out.println("</fieldset><input type=\"submit\" value=\"Save\"></form></td></tr>");			
				}
				else if(resource.equals("Database"))
				{
					//SCHEMA db.* PRIVILEGES, NEXT		
					out.println("<td><form action=\"/Fabflix/employee/userPrivileges\"" +
							" method=\"POST\"><fieldset><legend>" + resource + 
							" Select:</legend><select name=\"specific\" size=\"10\">");
					Statement dbQuery = connection.createStatement();
					ResultSet dbList = dbQuery.executeQuery("show databases");
					while(dbList.next())
					{
						String db = dbList.getString(1);
						out.println("<option value=\"" + db + "\"" +
								(session.getAttribute("sSpecific") != null?
										(session.getAttribute("sSpecific").equals(db)?
												" selected>" : ">")
										: ">") +
								db + "<br>");
					}
					
					dbList.close();
					dbQuery.close();
					
					out.println("</select></fieldset><input type=\"submit\" value=\"Next\"></form></td></tr>");
				}
				else if(resource.equals("Table"))
				{
					//TABLE db.table PRIVILEGES, NEXT
					out.println("<td><form action=\"/Fabflix/employee/userPrivileges\"" +
							" method=\"POST\"><fieldset><legend>");
					out.println(resource + " Select:</legend><select name=\"specific\" size=\"10\">");
					Statement tableQuery = connection.createStatement();
					ResultSet tableList = tableQuery.executeQuery("show tables in moviedb");
					while(tableList.next())
					{
						String table = tableList.getString(1);
						out.println("<option value=\"" + table + "\"" +
								(session.getAttribute("sSpecific") != null?
										(session.getAttribute("sSpecific").equals(table)?
												" selected>" : ">")
										: ">") +
								table + "<br>");
					}
					
					tableList.close();
					tableQuery.close();
					
					out.println("</select></fieldset><input type=\"submit\" value=\"Next\"></form></td></tr>");
				}
				else if(resource.equals("Procedure"))
				{
					//PROCEDURE grant blah on procedure name PRIVILEGES, NEXT
					out.println("<td><form action=\"/Fabflix/employee/userPrivileges\"" +
							" method=\"POST\"><fieldset><legend>");
					out.println(resource + " Select:</legend><select name=\"specific\" size=\"10\">");
					Statement procQuery = connection.createStatement();
					ResultSet procList = procQuery.executeQuery("SELECT type, db, specific_name FROM mysql.proc");
					while(procList.next())
					{
						String proc = procList.getString(1) + " " + procList.getString(2) + "." + procList.getString(3);
						out.println("<option value=\"" + proc + "\"" +
								(session.getAttribute("sSpecific") != null?
										(session.getAttribute("sSpecific").equals(proc)?
												" selected>" : ">")
										: ">") +
								proc + "<br>");
					}
					
					procList.close();
					procQuery.close();
					
					out.println("</select></fieldset><input type=\"submit\" value=\"Next\"></form></td></tr>");
				}
				else
					;//ERROR
				
			}
			else
			{
				out.println("</tr>");
			}	
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void specificPrivilegeSelect(PrintWriter out, Connection connection, HttpSession session)
	{
		try
		{
			String user = (String) session.getAttribute("sUser");
			String resource = (String) session.getAttribute("sResource");
			String specific = (String) session.getAttribute("sSpecific");
			
			if(user != null && resource != null && specific != null)
			{
				if(resource.equals("Database") && specific != null)
				{
					//DATABASE PRIVILEGES, SAVE
					out.println("</tr><tr><td>&nbsp;</td><td><form action=\"/Fabflix/employee/queryResults\" method=\"POST\">" +
							"<fieldset><legend>Database Privileges:</legend>");
					Statement checkPrivileges = connection.createStatement();
					ResultSet priv = checkPrivileges.executeQuery(
							"SELECT privilege_type" +
							" FROM information_schema.schema_privileges" +
							" WHERE table_schema = '" + specific + "'" +
									" AND grantee = \"'" + user + "'@'localhost'\"");
					
					ArrayList<String> granted = new ArrayList<String>();
					
					while(priv.next())
						granted.add(priv.getString(1));
					
					priv.close();
					checkPrivileges.close();
					
					HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"select\"" + 
							(granted.contains("SELECT")? "checked>" : ">") + "Select<br>");
						privileges.put("select", granted.contains("SELECT"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"insert\"" + 
							(granted.contains("INSERT")? "checked>" : ">") + "Insert<br>");
						privileges.put("insert", granted.contains("INSERT"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"update\"" + 
							(granted.contains("UPDATE")? "checked>" : ">") + "Update<br>");
						privileges.put("update", granted.contains("UPDATE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"delete\"" + 
							(granted.contains("DELETE")? "checked>" : ">") + "Delete<br>");
						privileges.put("delete", granted.contains("DELETE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"create\"" + 
							(granted.contains("CREATE")? "checked>" : ">") + "Create<br>");
						privileges.put("create", granted.contains("CREATE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"drop\"" + 
							(granted.contains("DROP")? "checked>" : ">") + "Drop<br>");
						privileges.put("drop", granted.contains("DROP"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"references\"" + 
							(granted.contains("REFERENCES")? "checked>" : ">") + "References<br>");
						privileges.put("references", granted.contains("REFERENCES"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"index\"" + 
							(granted.contains("INDEX")? "checked>" : ">") + "Index<br>");
						privileges.put("index", granted.contains("INDEX"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"alter\"" + 
							(granted.contains("ALTER")? "checked>" : ">") + "Alter<br>");
						privileges.put("alter", granted.contains("ALTER"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"create temporary tables\"" + 
							(granted.contains("CREATE TEMPORARY TABLES")? "checked>" : ">") + "Create temporary tables<br>");
						privileges.put("create temporary tables", granted.contains("CREATE TEMPORARY TABLES"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"lock tables\"" + 
							(granted.contains("LOCK TABLES")? "checked>" : ">") + "Lock tables<br>");
						privileges.put("lock tables", granted.contains("LOCK TABLES"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"execute\"" + 
							(granted.contains("EXECUTE")? "checked>" : ">") + "Execute<br>");
						privileges.put("execute", granted.contains("EXECUTE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"create view\"" + 
							(granted.contains("CREATE VIEW")? "checked>" : ">") + "Create view<br>");
						privileges.put("create view", granted.contains("CREATE VIEW"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"show view\"" + 
							(granted.contains("SHOW VIEW")? "checked>" : ">") + "Show view<br>");
						privileges.put("show view", granted.contains("SHOW VIEW"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"create routine\"" + 
							(granted.contains("CREATE ROUTINE")? "checked>" : ">") + "Create routine<br>");
						privileges.put("create routine", granted.contains("CREATE ROUTINE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"alter routine\"" + 
							(granted.contains("ALTER ROUTINE")? "checked>" : ">") + "Alter routine<br>");
						privileges.put("alter routine", granted.contains("ALTER ROUTINE"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"event\"" + 
							(granted.contains("EVENT")? "checked>" : ">") + "Event<br>");
						privileges.put("event", granted.contains("EVENT"));
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"trigger\"" + 
							(granted.contains("TRIGGER")? "checked>" : ">") + "Trigger<br>");
						privileges.put("trigger", granted.contains("TRIGGER"));
					
					checkPrivileges = connection.createStatement();
					priv = checkPrivileges.executeQuery("show grants for '" + user + "'@'localhost'");
					boolean hasGrantOption = false;
					checkGrant: while(priv.next())
					{
						String grant = priv.getString(1);
						if(grant.contains(specific) && grant.contains(".*") && grant.contains("WITH GRANT OPTION"))
						{
							hasGrantOption = true;
							break checkGrant;
						}
					}
					
					priv.close();
					checkPrivileges.close();
					
					out.println("<input type=\"checkbox\" name=\"db_priv\" value=\"grant option\"" + 
							(hasGrantOption? "checked>" : ">") + "Grant option<br>");
						privileges.put("grant option", hasGrantOption);
					
					session.setAttribute("privList", privileges);
					
					out.println("</fieldset><input type=\"submit\" value=\"Save\"></form></td></tr>");
				}
				else if(resource.equals("Table") && specific != null)
				{
					//TABLE/COLUMN PRIVILEGES, SAVE
					out.println("</tr><tr><td>&nbsp;</td><td><form action=\"/Fabflix/employee/queryResults\" method=\"POST\">" +
							"<fieldset><legend>Table and Column Privileges:</legend>");
					
					//Check privileges user has on table
					Statement checkPrivileges = connection.createStatement();
					ResultSet priv = checkPrivileges.executeQuery(
							"SELECT privilege_type" +
							" FROM information_schema.table_privileges" +
							" WHERE table_name = '" + specific + "'" +
									" AND grantee = \"'" + user + "'@'localhost'\"");
					
					ArrayList<String> granted = new ArrayList<String>();
					
					while(priv.next())
						granted.add(priv.getString(1));
					
					priv.close();
					checkPrivileges.close();
					
					//Retrieve column names
					checkPrivileges = connection.createStatement();
					priv = checkPrivileges.executeQuery("describe moviedb." + specific + "");
					
					ArrayList<String> colNames = new ArrayList<String>();
					
					while(priv.next())
						colNames.add(priv.getString(1));
					
					priv.close();
					checkPrivileges.close();
					
					//Check privileges user has on columns
					checkPrivileges = connection.createStatement();
					priv = checkPrivileges.executeQuery(
							"SELECT column_name, privilege_type" +
							" FROM information_schema.column_privileges" +
							" WHERE table_name = '" + specific + "'" +
									" AND grantee = \"'" + user + "'@'localhost'\"");
					
					ArrayList<ColumnPrivilege> colGranted = new ArrayList<ColumnPrivilege>();
					
					while(priv.next())
						colGranted.add(new ColumnPrivilege(priv.getString(1), priv.getString(2)));
					
					priv.close();
					checkPrivileges.close();
					
					HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"select\"" + 
							(granted.contains("SELECT")? "checked>" : ">") + "Select<br>");
						for(String col: colNames)
						{
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"col_priv\" value=\"select (" +
									col + ")\"");
							boolean found = false;
							checkPriv: for(ColumnPrivilege cp: colGranted)
							{
								if(cp.equals(col, "SELECT"))
								{
									found = true;
									break checkPriv;
								}
							}
							out.println((found? "checked>" : ">") + col +"<br>");
							privileges.put("select (" + col + ")", found);
						}
						privileges.put("select", granted.contains("SELECT"));
						
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"insert\"" + 
							(granted.contains("INSERT")? "checked>" : ">") + "Insert<br>");
						for(String col: colNames)
						{
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"col_priv\" value=\"insert (" +
									col + ")\"");
							boolean found = false;
							checkPriv: for(ColumnPrivilege cp: colGranted)
							{
								if(cp.equals(col, "INSERT"))
								{
									found = true;
									break checkPriv;
								}
							}
							out.println((found? "checked>" : ">") + col +"<br>");
							privileges.put("insert (" + col + ")", found);
						}
						privileges.put("insert", granted.contains("INSERT"));
						
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"update\"" + 
							(granted.contains("UPDATE")? "checked>" : ">") + "Update<br>");
						for(String col: colNames)
						{
							out.println("&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"col_priv\" value=\"update (" +
									col + ")\"");
							boolean found = false;
							checkPriv: for(ColumnPrivilege cp: colGranted)
							{
								if(cp.equals(col, "UPDATE"))
								{
									found = true;
									break checkPriv;
								}
							}
							out.println((found? "checked>" : ">") + col +"<br>");
							privileges.put("update (" + col + ")", found);
						}
						privileges.put("update", granted.contains("UPDATE"));
						
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"delete\"" + 
							(granted.contains("DELETE")? "checked>" : ">") + "Delete<br>");
						privileges.put("delete", granted.contains("DELETE"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"create\"" + 
							(granted.contains("CREATE")? "checked>" : ">") + "Create<br>");
						privileges.put("create", granted.contains("CREATE"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"drop\"" + 
							(granted.contains("DROP")? "checked>" : ">") + "Drop<br>");
						privileges.put("drop", granted.contains("DROP"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"references\"" + 
							(granted.contains("REFERENCES")? "checked>" : ">") + "References<br>");
						privileges.put("references", granted.contains("REFERENCES"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"index\"" + 
							(granted.contains("INDEX")? "checked>" : ">") + "Index<br>");
						privileges.put("index", granted.contains("INDEX"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"alter\"" + 
							(granted.contains("ALTER")? "checked>" : ">") + "Alter<br>");
						privileges.put("alter", granted.contains("ALTER"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"create view\"" + 
							(granted.contains("CREATE VIEW")? "checked>" : ">") + "Create view<br>");
						privileges.put("create view", granted.contains("CREATE VIEW"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"show view\"" + 
							(granted.contains("SHOW VIEW")? "checked>" : ">") + "Show view<br>");
						privileges.put("show view", granted.contains("SHOW VIEW"));
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"trigger\"" + 
							(granted.contains("TRIGGER")? "checked>" : ">") + "Trigger<br>");
						privileges.put("trigger", granted.contains("TRIGGER"));
						
					checkPrivileges = connection.createStatement();
					priv = checkPrivileges.executeQuery("show grants for '" + user + "'@'localhost'");
					boolean hasGrantOption = false;
					checkGrant: while(priv.next())
					{
						String grant = priv.getString(1);
						if(grant.contains("moviedb") && grant.contains(specific) && grant.contains("WITH GRANT OPTION"))
						{
							hasGrantOption = true;
							break checkGrant;
						}
					}
					
					priv.close();
					checkPrivileges.close();
					
					out.println("<input type=\"checkbox\" name=\"table_priv\" value=\"grant option\"" + 
							(hasGrantOption? "checked>" : ">") + "Grant option<br>");
						privileges.put("grant option", hasGrantOption);
					
					session.setAttribute("privList", privileges);
					
					out.println("</fieldset><input type=\"submit\" value=\"Save\"></form></td></tr>");
				}
				else if(resource.equals("Procedure") && specific != null)
				{
					//PROCEDURE PRIVILEGES, SAVE
					out.println("</tr><tr><td>&nbsp;</td><td><form action=\"/Fabflix/employee/queryResults\" method=\"POST\">" +
							"<fieldset><legend>Procedure Privileges:</legend>");
					Statement checkPrivileges = connection.createStatement();
					ResultSet priv = checkPrivileges.executeQuery("show grants for '" + user + "'@'localhost'");
					
					ArrayList<String> granted = new ArrayList<String>();
					
					boolean hasGrant = false;
					while(priv.next())
					{
						String grant = priv.getString(1);
						int indexOfDot = grant.indexOf(".");
						String spec = grant.substring(indexOfDot + 1);
						
						if(grant.contains("PROCEDURE") && grant.contains(spec))
						{
							if(grant.contains("EXECUTE"))
								granted.add("EXECUTE");
							if(grant.contains("ALTER ROUTINE"))
								granted.add("ALTER ROUTINE");	
							if(grant.contains("WITH GRANT OPTION"))
								hasGrant = true;
						}
					}
					
					priv.close();
					checkPrivileges.close();
					
					HashMap<String, Boolean> privileges = new HashMap<String, Boolean>();
					
					out.println("<input type=\"checkbox\" name=\"proc_priv\" value=\"execute\"" + 
							(granted.contains("EXECUTE")? "checked>" : ">") + "Execute<br>");
						privileges.put("execute", granted.contains("EXECUTE"));
					
					out.println("<input type=\"checkbox\" name=\"proc_priv\" value=\"alter routine\"" + 
							(granted.contains("ALTER ROUTINE")? "checked>" : ">") + "Alter routine<br>");
						privileges.put("alter routine", granted.contains("ALTER ROUTINE"));
						
					out.println("<input type=\"checkbox\" name=\"proc_priv\" value=\"grant option\"" + 
							(hasGrant? "checked>" : ">") + "Grant option<br>");
						privileges.put("grant option", hasGrant);
						
					session.setAttribute("privList", privileges);
						
					out.println("</fieldset><input type=\"submit\" value=\"Save\"></form></td></tr>");
				}
				else
				{
					//DO NOTHING
				}
			}
			else
			{
				out.println("</tr>");
			}	
		}
		catch(Exception e)
		{
			
		}
	}
}