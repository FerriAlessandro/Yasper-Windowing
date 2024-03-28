package org.streamreasoning.rsp4j.yasper.example_windowing;

import org.apache.jena.graph.Graph;

public class ValidatedGraph {

    public Graph report;
    public Graph content;
    public ValidatedGraph(Graph report, Graph content){
        this.report = report;
        this.content = content;
    }

    public Graph getReport(){
        return this.report;
    }

    public Graph getContent(){
        return this.content;
    }

    @Override
    public String toString(){
        String str = new String();
        str += "Content size: " + content.size();
        str += "Validate report size: " + report.size();
        return str;
    }
}
