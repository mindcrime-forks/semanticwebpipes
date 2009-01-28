package org.deri.execeng.endpoints;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.deri.pipes.ui.PipeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;

public class PipeManager {
	static Logger logger = LoggerFactory.getLogger(PipeManager.class);
	private static String DB_PROP ="db.properties";
	public static Connection getConnection(){
    	Connection conn = null;
    	Properties prop = new Properties();
		try
		{
			prop.load(new FileInputStream(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/"+DB_PROP)));
			
            StringBuffer connStr= new StringBuffer("jdbc:mysql://");
            connStr.append(prop.getProperty("host"));
            if (prop.getProperty("port")!=null) 
            	connStr.append(":").append(prop.getProperty("port"));
            connStr.append("/").append(prop.getProperty("database"));
            
/*    		DataSource ds = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/pipes");
    	    conn = ds.getConnection();*/
    	
 	    	Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection(connStr.toString(), prop.getProperty("username"), prop.getProperty("password"));
	    	//logger.debug("connStr "+ connStr.toString());
    	}
    	catch(Exception e){
			logger.info("could not get connection",e);
		}
    	return conn;
    }
	
	public static  List<Pipe> getPipeList(){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		String query = "SELECT pipeid,pipename FROM pipes";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			List<Pipe> pipeList = new ArrayList<Pipe>();
			while(rs.next()){
				Pipe pipe = new Pipe();
				pipe.setId(rs.getString("pipeid"));
				pipe.setName(rs.getString("pipename"));
				pipeList.add(pipe);
			}
			return pipeList;
		}
		catch(SQLException e){
			logger.info("error executing query ["+query+"]",e);
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
		String query = "SELECT syntax FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			//logger.debug(rs.getString("syntax"));
			if(rs.next()){
				///logger.debug(rs.getString("syntax"));
				return rs.getString("syntax");
			}
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
		String query = "SELECT config FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			//logger.debug(rs.getString("syntax"));
			if(rs.next()){
				///logger.debug(rs.getString("syntax"));
				return rs.getString("config");
			}
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
		String query ="SELECT syntax,pipename,config FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()){
				Pipe pipe = new Pipe();
				pipe.setId(pipeid);
				pipe.setName(rs.getString("pipename"));
				pipe.setSyntax(rs.getString("syntax"));
				pipe.setConfig(rs.getString("config"));
				return pipe;
			}
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
		String query = "DELETE FROM pipes where pipeid=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, pipeid);
			pstmt.executeUpdate();
			org.zkoss.zk.ui.Executions.sendRedirect(".");
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
		String query = "SELECT pipeid FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			if(rs.next())return true;			
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
		String query = "SELECT password FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			if(rs.next())
				if(rs.getString("password")!=null)
					if (rs.getString("password").trim().length()>0) return rs.getString("password").trim();
		}
		catch(SQLException e){
			logger.info("problem executing query ["+query+"]",e);
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
			String query = "SELECT pipeid FROM pipes where pipeid='"+pipeid+"'";
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);	
				PreparedStatement pstmt;

				if(rs.next()){
					query =  "UPDATE  pipes set pipeid=?,pipename=?,syntax=?,config=?,password=? where pipeid='"+pipeid+"'";
				   pstmt = conn.prepareStatement(query);
				}else{
					query = "INSERT INTO pipes(pipeid,pipename,syntax,config,password) values(?, ?,?,?,?)";
				    pstmt = conn.prepareStatement(query);
				}
				pstmt.setString(1, pipeid);
				pstmt.setString(2, pipename);
				pstmt.setString(3, syntax);
				pstmt.setString(4, config);
				pstmt.setString(5, password);
				pstmt.executeUpdate();					
				return true;
			}
			catch(SQLException e){
				logger.info("problem executing query ["+query+"]",e);
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
