package org.deri.execeng.rdf;
import org.xml.sax.InputSource;
import java.io.*;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
public class RdfExecEngineTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BoxParserImplRDF parser= new BoxParserImplRDF();
		Stream stream=null;
		try{
		   stream= parser.parse(new InputSource(new FileReader("c:\\pipes\\patch.xml")));
		}
		catch(FileNotFoundException e){	
		}
		if(stream instanceof Box){
		  ((Box)stream).execute();	
          System.out.println(stream.toString());
          try{
          Writer output = null;
          File file = new File("c:\\pipes\\patchout.rdf");
          output = new BufferedWriter(new FileWriter(file));
          output.write(stream.toString());
          output.close();
          System.out.println("Your file has been written");
          }
          catch(IOException e){
        	  e.printStackTrace();
          }
          
		}
		else{
		  System.out.println("parsing error");
		}
	}

}
