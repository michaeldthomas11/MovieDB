
public class ColumnPrivilege
{
	private String columnName;
	private String privilegeType;
	
	public ColumnPrivilege(String col, String priv)
	{
		columnName = col;
		privilegeType = priv;
	}
	
	public boolean equals(String col, String priv)
	{
		return (columnName.equals(col) && privilegeType.equals(priv));
	}
}
