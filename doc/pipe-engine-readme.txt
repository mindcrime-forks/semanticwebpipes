The Semantic Web Pipe Engine allows pipes to be executed on the command line or embedded within an application. This includes only the execution environment and not the graphical pipes development environment.
see: http://pipes.deri.org

To have a pipe process command line input from the STDIN input stream, include a <stdin/> operator in your pipe.

To invoke a pipe expecting name and place parameters, invoke as follows:

bin/pipes path/to/pipe/syntax/file name=Giovanni place=Galway

Here follows an example pipe reading from stdin. This requires 2 files, input.txt and pipe.txt which are shown below:

Command line is:
bin/pipes pipe.txt <input.txt

Output is:
<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' 
          xmlns:foaf='http://xmlns.com/foaf/0.1/'>
  <foaf:person rdf:about='http://example.com/Robert_Fuller'>
    <foaf:name>Robert Fuller</foaf:name>
    <foaf:firstName>Robert</foaf:firstName>
    <foaf:surname>Fuller</foaf:surname>
  </foaf:person>
  <foaf:person rdf:about='http://example.com/Giovanni_Tummarello'>
    <foaf:name>Giovanni Tummarello</foaf:name>
    <foaf:firstName>Giovanni</foaf:firstName>
    <foaf:surname>Tummarello</foaf:surname>
  </foaf:person>
  <foaf:person rdf:about='http://example.com/Dahn_Le_Pouch'>
    <foaf:name>Dahn Le Pouch</foaf:name>
    <foaf:firstName>Dahn</foaf:firstName>
    <foaf:surname>Le Pouch</foaf:surname>
  </foaf:person>
</rdf:RDF>

============input.txt========================
Robert Fuller
Giovanni Tummarello
Dahn Le Pouch
=============================================

=============pipe.txt========================
<pipe>
  <code>
    <scripting>
      <language>groovy</language>
      <script>
import groovy.xml.MarkupBuilder
 def writer = new StringWriter()
 def xml = new MarkupBuilder(writer)
 xml.'rdf:RDF'('xmlns:rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#','xmlns:foaf':'http://xmlns.com/foaf/0.1/'){
   input.inputStream.eachLine{ 
    name -> parts=name.split(/\s/,2);
     xml.'foaf:person'('rdf:about':"http://example.com/${name.replaceAll(' ','_')}"){
      'foaf:name'(name)
      'foaf:firstName'(parts[0])
      'foaf:surname'(parts[1])
     }
   }
 }
writer.toString();
      </script>
      <source>
        <stdin/>
      </source>
    </scripting>
  </code>
</pipe>
=============================================


Robert Fuller
11/02/2009
Galway, Ireland
