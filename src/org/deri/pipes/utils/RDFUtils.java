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
package org.deri.pipes.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFUtils {
	private static Logger logger = LoggerFactory.getLogger(RDFUtils.class);
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
