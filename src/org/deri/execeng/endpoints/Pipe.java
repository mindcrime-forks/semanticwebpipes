package org.deri.execeng.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Pipe {
	final Logger logger = LoggerFactory.getLogger(Pipe.class);
	public String pipeid,pipename,syntax,config=null;
    public Pipe(String pipeid,String pipename,String syntax,String config){
        this.pipeid=pipeid;
        this.pipename=pipename;
        this.syntax=syntax;
        this.config=config;
    }
    
    public Pipe(String pipeid,String pipename){
        this.pipeid=pipeid;
        this.pipename=pipename;
    }
}
