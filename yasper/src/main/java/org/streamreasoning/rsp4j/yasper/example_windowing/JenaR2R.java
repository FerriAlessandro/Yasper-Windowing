package org.streamreasoning.rsp4j.yasper.example_windowing;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.core.mem.DatasetGraphInMemory;
import org.apache.jena.sparql.engine.binding.Binding;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMappingBase;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class JenaR2R implements RelationToRelationOperator<ValidatedGraph, Binding> {

    private Query query;

    public JenaR2R(String query) {
        this.query = QueryFactory.create(query);
        this.query.getProjectVars();

    }


    @Override
    public Stream<Binding> eval(Stream<ValidatedGraph> sds) {
        Node aDefault = NodeFactory.createURI("default");
        DatasetGraph dg = new DatasetGraphInMemory();

        sds.forEach(g -> {
            dg.addGraph(aDefault, g.content);
        });

        QueryExecution queryExecution = QueryExecutionFactory.create(query, DatasetImpl.wrap(dg));
        ResultSet resultSet = queryExecution.execSelect();

        List<Binding> res = new ArrayList<>();

        while (resultSet.hasNext()) {

            ResultBinding rb = (ResultBinding) resultSet.next();
            res.add(rb.getBinding());

        }

        return res.stream();
    }

    @Override
    public TimeVarying<Collection<Binding>> apply(SDS<ValidatedGraph> sds) {
        //TODO this should return an SDS
        List<Binding> res = new ArrayList<>();
        return new TimeVarying<>() {
            @Override
            public void materialize(long ts) {
                //time should not be important
                res.clear();
                eval(sds.toStream()).forEach(res::add);
            }

            @Override
            public Collection<Binding> get() {
                return res;
            }

            @Override
            public String iri() {
                return null;
            }
        };
    }

    @Override
    public SolutionMapping<Binding> createSolutionMapping(Binding result) {
        return new SolutionMappingBase<>(result, System.currentTimeMillis());
    }
}
