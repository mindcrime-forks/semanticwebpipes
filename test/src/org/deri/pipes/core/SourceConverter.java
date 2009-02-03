package org.deri.pipes.core;

import java.util.HashMap;
import java.util.Map;

import org.deri.pipes.model.Operator;
import org.deri.pipes.rdf.ConstructBox;
import org.deri.pipes.rdf.ForLoopBox;
import org.deri.pipes.rdf.HTMLFetchBox;
import org.deri.pipes.rdf.PatchExecutorBox;
import org.deri.pipes.rdf.PatchGeneratorBox;
import org.deri.pipes.rdf.RDFFetchBox;
import org.deri.pipes.rdf.SameAsBox;
import org.deri.pipes.rdf.SelectBox;
import org.deri.pipes.rdf.SimpleMixBox;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SourceConverter implements Converter {

	static Map<String,Class> MAPPINGS = new HashMap<String,Class>();
	static{
		MAPPINGS.put("simplemix",SimpleMixBox.class);
		MAPPINGS.put("source",Source.class);
		MAPPINGS.put("code",Source.class);
		MAPPINGS.put("sourcelist",Source.class);
		MAPPINGS.put("rdffetch",RDFFetchBox.class);
		MAPPINGS.put("for",ForLoopBox.class);
		MAPPINGS.put("select",SelectBox.class);
		MAPPINGS.put("smoosher",SameAsBox.class);
		MAPPINGS.put("smoosher",SameAsBox.class);
		MAPPINGS.put("patch-executor",PatchExecutorBox.class);
		MAPPINGS.put("patch-generator",PatchGeneratorBox.class);
		MAPPINGS.put("construct",ConstructBox.class);
		MAPPINGS.put("htmlfetch", HTMLFetchBox.class);


	}
	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Source source = (Source)arg0;
		Operator delegate = source.getDelegate();
		if(delegate != null){
			writer.startNode(getNodeForClass(delegate.getClass()));
			context.convertAnother(delegate);
			writer.endNode();
		}
		
	}

	private String getNodeForClass(Class clazz) {
		for(String key : MAPPINGS.keySet()){
			if(clazz.equals(MAPPINGS.get(key))){
				return key;
			}
		}
		return(clazz.getName());
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Source source = new Source();
		if(reader.hasMoreChildren()){
			reader.moveDown();
			String nodeName = reader.getNodeName();
			Object delegate = context.convertAnother(source, MAPPINGS.get(nodeName));
			source.setDelegate((Operator)delegate);
			reader.moveUp();
		}
		return source;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return Source.class.equals(clazz);
	}

	public static void registerAliases(XStream xstream) {
		for(String key : MAPPINGS.keySet()){
			xstream.alias(key, MAPPINGS.get(key));
		}
	}

}
