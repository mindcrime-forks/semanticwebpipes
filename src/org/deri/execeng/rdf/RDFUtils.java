package org.deri.execeng.rdf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFUtils {
	static Logger logger = LoggerFactory.getLogger(RDFUtils.class);
	public static Connection getConnection(){
    	String virtuosoDriverClass = "virtuoso.jdbc3.Driver";
		Connection conn = null;
		try{
			Class.forName(virtuosoDriverClass);
	    	conn = DriverManager.getConnection("jdbc:virtuoso://localhost:1112/DATABASE=Demo/UID=dba/PWD=dba/");
		}
		catch(ClassNotFoundException e){
			logger.warn("Could not load virtuoso driver (not in classpath) "+virtuosoDriverClass,e);
		}
		catch(SQLException e){
			logger.debug("Exception in opening connection",e);
		}
		return conn;
	}
	public static void loadNamedGraph(String url,String graphName){
		query("SPARQL LOAD <"+url+"> INTO graph <"+graphName+">");
	}

	public static void clearNamedGraph(String graph){
		query("SPARQL CLEAR GRAPH <"+graph+">");
	}

	public static void dropNamedGraph(String graph){
		query("SPARQL DROP GRAPH <"+graph+">");
	}
	
	public static ResultSet query(String query){
		//DataSource ds = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/Demo");
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		logger.debug("query "+query);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);		
		}
		catch(SQLException e){
			logger.debug("query Exception"+e);
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
		return rs;
	}
}
