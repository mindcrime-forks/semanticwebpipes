package org.deri.execeng.endpoints;

public class Pipe {
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
