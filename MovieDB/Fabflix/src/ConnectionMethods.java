import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class ConnectionMethods
{
	public static Connection initialize(PrintWriter out) throws NamingException, SQLException
	{
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		if (envCtx == null) out.println("envCtx is NULL");

		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
		if (ds == null) out.println("ds is null.");

		Connection connection = ds.getConnection();
		if (connection == null) out.println("connection is null.");
		
		return connection;
	}
}
