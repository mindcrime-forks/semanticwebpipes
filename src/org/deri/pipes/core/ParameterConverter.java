package org.deri.pipes.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ParameterConverter implements Converter {

	@Override
	public boolean canConvert(Class clazz) {
		return Parameters.class.isAssignableFrom(clazz);
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Parameters parameters = (Parameters)obj;
		for(String p : parameters.list()){
			Parameters.Parameter parameter = parameters.getParameter(p);
			Map<String,String> map = parameter.toMap();
			writer.startNode("parameter");
			for(String key : map.keySet()){
				writer.startNode(key);
				writer.setValue(map.get(key));
				writer.endNode();
			}
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Parameters parameters = new Parameters();
		while(reader.hasMoreChildren()){
			Map<String,String> map = new HashMap<String,String>();
			reader.moveDown();
			while(reader.hasMoreChildren()){
				reader.moveDown();
				String key = reader.getNodeName();
				String value = reader.getValue();
				map.put(key, value);
				reader.moveUp();
			}
			reader.moveUp();
			parameters.add(map);
		}
		return parameters;
	}


}
