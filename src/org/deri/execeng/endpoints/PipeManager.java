package org.deri.execeng.endpoints;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Properties;
import org.deri.pipes.ui.PipeEditor;
import org.zkoss.zk.ui.Executions;

public class PipeManager {
	private static String DB_PROP ="db.properties";
	public static Connection getConnection(){
    	Connection conn = null;
    	Properties prop = new Properties();
		try
		{
			prop.load(new FileReader(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/"+DB_PROP)));
			
            StringBuffer connStr= new StringBuffer("jdbc:mysql://");
            connStr.append(prop.getProperty("host"));
            if (prop.getProperty("port")!=null) 
            	connStr.append(":").append(prop.getProperty("port"));
            connStr.append("/").append(prop.getProperty("database"));
            
/*    		DataSource ds = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/pipes");
    	    conn = ds.getConnection();*/
    	
 	    	Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(connStr.toString(), prop.getProperty("username"), prop.getProperty("password"));
	    	//System.out.println("connStr "+ connStr.toString());
    	}
    	catch(ClassNotFoundException e){
   // 	catch(NamingException e){
    		e.printStackTrace();
		}
    	catch(SQLException e){
			System.out.println("Exception in openning connection");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return conn;
    }
	
	public static  ArrayList<Pipe> getPipeList(){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT pipeid,pipename FROM pipes");	
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
			while(rs.next()){				
				pipeList.add(new Pipe(rs.getString("pipeid"),rs.getString("pipename")));
			}
			return pipeList;
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return null;
	}
	
	public static String getPipeSyntax(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT syntax FROM pipes where pipeid='"+pipeid+"'");	
			//System.out.println(rs.getString("syntax"));
			if(rs.next()){
				///System.out.println(rs.getString("syntax"));
				return rs.getString("syntax");
			}
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return null;
	}
	
	public static String getPipeConfig(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT config FROM pipes where pipeid='"+pipeid+"'");	
			//System.out.println(rs.getString("syntax"));
			if(rs.next()){
				///System.out.println(rs.getString("syntax"));
				return rs.getString("config");
			}
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return null;
	}
	
	public static Pipe getPipe(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT syntax,pipename,config FROM pipes where pipeid='"+pipeid+"'");
			if(rs.next()){
				return new Pipe(pipeid,rs.getString("pipename"),rs.getString("syntax"),rs.getString("config"));
			}
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return null;
	}
	
	public static void deletePipe(String pipeid){
		Connection conn = getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pipes where pipeid=?");
			pstmt.setString(1, pipeid);
			pstmt.executeUpdate();
			org.zkoss.zk.ui.Executions.sendRedirect(".");
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
	}
	
	public static boolean isExist(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT pipeid FROM pipes where pipeid='"+pipeid+"'");	
			if(rs.next())return true;			
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return false;
	}

	public static String getPassword(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT password FROM pipes where pipeid='"+pipeid+"'");	
			if(rs.next())
				if(rs.getString("password")!=null)
					if (rs.getString("password").trim().length()>0) return rs.getString("password").trim();
		}
		catch(SQLException e){
			System.out.println("query Exception"+e);
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) {
				try {
					stmt.close();
				} 
				catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return null;
	}
	
	public static boolean savePipe(PipeEditor wsp){
		String pipeid=wsp.getPipeId(),pipename=wsp.getPipeName(),
		password=wsp.getPassword(),syntax=wsp.getSrcCode(false),config=wsp.getSrcCode(true);
		
		if((null!=pipeid)&&(pipeid.trim().length()>0)){
			Connection conn = getConnection();
			Statement stmt = null;
			ResultSet rs=null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT pipeid FROM pipes where pipeid='"+pipeid+"'");	
				PreparedStatement pstmt;
				if(rs.next())
				   pstmt = conn.prepareStatement("UPDATE  pipes set pipeid=?,pipename=?,syntax=?,config=?,password=? where pipeid='"+pipeid+"'");
				else
				   pstmt = conn.prepareStatement("INSERT INTO pipes(pipeid,pipename,syntax,config,password) values(?, ?,?,?,?)");				
				pstmt.setString(1, pipeid);
				pstmt.setString(2, pipename);
				pstmt.setString(3, syntax);
				pstmt.setString(4, config);
				pstmt.setString(5, password);
				pstmt.executeUpdate();					
				return true;
			}
			catch(SQLException e){
				System.out.println("query Exception"+e);
				e.printStackTrace();
			}
			finally { 
				if (stmt != null) {
					try {
						stmt.close();
					} 
					catch (SQLException ex) {
					}
				}
				if (conn != null) {
					try {
					conn.close();
					} catch (SQLException ex) {
					}
				}
			}
		}		
		return false;
	}
}
