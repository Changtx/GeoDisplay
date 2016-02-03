package geoDisplay;
import java.sql.*;

import oracle.spatial.geometry.JGeometry; 
import oracle.sql.STRUCT; 

public class oracleConnect {
	public Connection con;
	public Statement stmt;
	public oracleConnect() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver"); 
		con=DriverManager.getConnection(  
				"jdbc:oracle:thin:@localhost:1521:xe","changtx","123"); 
		stmt=con.createStatement();
	}
	
	public ResultSet findRegion() throws SQLException { 
		ResultSet rs=stmt.executeQuery("select region_id, region_area from region");  
		return rs;
	}
	
	public ResultSet findPond() throws SQLException {
		ResultSet rs=stmt.executeQuery("select pond_id, pond_area from pond");  
		return rs;
	}
	
	public ResultSet findLion() throws SQLException {
		ResultSet rs=stmt.executeQuery("select lion_id, lion_loc from lion");  
		return rs;
	}
	
	public void closeConnect() throws SQLException {
		con.close();
	}
	
	public static void main(String args[]) throws ClassNotFoundException, SQLException{   
		oracleConnect connect = new oracleConnect();
		ResultSet rs = connect.findLion();
		
		while(rs.next()){  
			STRUCT dbObject = (STRUCT) rs.getObject(2);  
			JGeometry geom = JGeometry.load(dbObject); 
			System.out.println(geom.getPoint()[0] + "  " + geom.getPoint()[1]);  
		}
		connect.closeConnect();
	}
}
