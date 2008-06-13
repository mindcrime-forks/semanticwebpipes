package org.deri.execeng.endpoints;

public class Pipe {
	public String pipeid,pipename,syntax=null;
    public Pipe(String pipeid,String pipename,String syntax){
        this.pipeid=pipeid;
        this.pipename=pipename;
        this.syntax=syntax;
    }
}
