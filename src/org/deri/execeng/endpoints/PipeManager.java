package org.deri.execeng.endpoints;

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

import org.deri.execeng.rdf.RDFBox;
import org.zkoss.zul.Textbox;
import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.core.ExecBuffer;
public class PipeManager {
	public static Connection getConnection(){
    	Connection conn = null;
    	   
//    	try {
//    		DataSource ds = (DataSource)new InitialContext().lookup("java:comp/env/jdbc/PIPES");
//    	    conn = ds.getConnection();
//    	}
    	try{
	    	Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection("jdbc:mysql://localhost/pipes", "root", "pipe");
    	}
    	catch(ClassNotFoundException e){
//    	catch(NamingException e){
//    		e.printStackTrace();
//    		System.out.println("Naming Exception");
		}
    	catch(SQLException e){
			System.out.println("Exception in openning connection");
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
				pipeList.add(new Pipe(rs.getString("pipeid"),rs.getString("pipename"),null));
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
	public static Pipe getPipe(String pipeid){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs=null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT syntax,pipename FROM pipes where pipeid='"+pipeid+"'");
			if(rs.next()){
				return new Pipe(pipeid,rs.getString("pipename"),rs.getString("syntax"));
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
	public static boolean debugPipe(String syntax,Textbox view){
		if(syntax!=null) {
		   BoxParserImplRDF parser= new BoxParserImplRDF();
		   if(Stream.log.length()>0)
			   Stream.log.delete(0,Stream.log.length());
		   if (ExecBuffer.log.length()>0)
			   ExecBuffer.log.delete(0,ExecBuffer.log.length());
		   Stream    stream= parser.parse(syntax);
		       if(stream instanceof RDFBox){
		    		  ((RDFBox)stream).execute();
		    		  if(Stream.log.length()+ExecBuffer.log.length()==0)
		    	      view.setValue((new StringBuffer("Successful!!!\n\n Output RDF: \n\n").append(stream.toString())).toString());
		    		  else
		    			  view.setValue((new StringBuffer("Error!!! \n Syntax:\n").append(Stream.log.toString()+"Executing:\n"+ExecBuffer.log.toString())).toString());  
		       }
		       else{		    	  
		    	   view.setValue((new StringBuffer("Error!!! \n Syntax:\n").append(Stream.log.toString()+"Executing:\n "+ExecBuffer.log.toString())).toString());
		       }
		} 
		return false;
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
	
	public static boolean savePipe(String pipeid,String pipename,String syntax,String password){
		if(pipeid!=null){
			if(pipeid.trim().length()>0){
				Connection conn = getConnection();
				Statement stmt = null;
				ResultSet rs=null;
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT pipeid FROM pipes where pipeid='"+pipeid+"'");	
					PreparedStatement pstmt;
					if(rs.next())
					   pstmt = conn.prepareStatement("UPDATE  pipes set pipeid=?,pipename=?,syntax=?,password=? where pipeid='"+pipeid+"'");
					else
					   pstmt = conn.prepareStatement("INSERT INTO pipes(pipeid,pipename,syntax,password) values(?, ?,?,?)");				
					pstmt.setString(1, pipeid);
					pstmt.setString(2, pipename);
					pstmt.setString(3, syntax);
					pstmt.setString(4, password);
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
		}		
		return false;
	}
}
