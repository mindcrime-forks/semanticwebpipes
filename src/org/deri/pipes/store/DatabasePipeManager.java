/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.store;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.deri.pipes.endpoints.PipeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.Executions;

public class DatabasePipeManager implements PipeStore {
	static Logger logger = LoggerFactory.getLogger(DatabasePipeManager.class);
	private static String DB_PROP ="db.properties";
	public static PipeStore instance = new DatabasePipeManager(); //TODO remove this.
	public static Connection getConnection(){
		Connection conn = null;
		Properties prop = new Properties();
		try
		{
//			prop.load(new FileInputStream(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/"+DB_PROP)));
			prop.load(DatabasePipeManager.class.getClassLoader().getResourceAsStream(DB_PROP));

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

	/* (non-Javadoc)
	 * @see org.deri.pipes.endpoints.PipeStore#getPipeList()
	 */
	public List<PipeConfig> getPipeList(){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		String query = "SELECT pipeid,pipename FROM pipes";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			List<PipeConfig> pipeList = new ArrayList<PipeConfig>();
			while(rs.next()){
				PipeConfig pipeConfig = new PipeConfig();
				pipeConfig.setId(rs.getString("pipeid"));
				pipeConfig.setName(rs.getString("pipename"));
				pipeList.add(pipeConfig);
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

	/* (non-Javadoc)
	 * @see org.deri.pipes.endpoints.PipeStore#getPipe(java.lang.String)
	 */
	public PipeConfig getPipe(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		String query ="SELECT syntax,pipename,config FROM pipes where pipeid='"+pipeid+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next()){
				PipeConfig pipeConfig = new PipeConfig();
				pipeConfig.setId(pipeid);
				pipeConfig.setName(rs.getString("pipename"));
				pipeConfig.setSyntax(rs.getString("syntax"));
				pipeConfig.setConfig(rs.getString("config"));
				return pipeConfig;
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

	/* (non-Javadoc)
	 * @see org.deri.pipes.endpoints.PipeStore#deletePipe(java.lang.String)
	 */
	public void deletePipe(String pipeid){
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

	/* (non-Javadoc)
	 * @see org.deri.pipes.endpoints.PipeStore#contains(java.lang.String)
	 */
	public boolean contains(String pipeid){
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

	/* (non-Javadoc)
	 * @see org.deri.pipes.endpoints.PipeStore#save(org.deri.pipes.endpoints.PipeConfig)
	 */
	public boolean save(PipeConfig pipeConfig){
		if(pipeConfig.getId() == null){
			logger.warn("cannot save pipe having null id");
			return false;
		}
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		String query = "SELECT pipeid FROM pipes where pipeid='"+pipeConfig.getId()+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);	
			PreparedStatement pstmt;

			if(rs.next()){
				query =  "UPDATE  pipes set pipeid=?,pipename=?,syntax=?,config=?,password=? where pipeid=?";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(6, pipeConfig.getId());
			}else{
				query = "INSERT INTO pipes(pipeid,pipename,syntax,config,password) values(?,?,?,?,?)";
				pstmt = conn.prepareStatement(query);
			}
			pstmt.setString(1, pipeConfig.getId());
			pstmt.setString(2, pipeConfig.getName());
			pstmt.setString(3, pipeConfig.getSyntax());
			pstmt.setString(4, pipeConfig.getConfig());
			pstmt.setString(5, pipeConfig.getPassword());
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
		return false;
	}

}
