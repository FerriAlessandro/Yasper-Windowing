package it.polimi.jasper.engine.query;

import com.espertech.esper.client.EventBean;
import it.polimi.jasper.engine.instantaneous.GraphBase;
import it.polimi.jasper.engine.instantaneous.JenaGraph;
import it.polimi.jasper.esper.ContentBean;
import it.polimi.jasper.esper.EsperStatementView;
import it.polimi.yasper.core.spe.windowing.assigner.WindowAssigner;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Getter
public class DefaultTVG extends EsperStatementView<JenaGraph> {

    private Set<WindowAssigner> windowAssigners;
    private JenaGraph graph = new GraphBase();

    public DefaultTVG(JenaGraph graph) {
        this.graph = graph;
        this.windowAssigners = new HashSet<>();
    }

    @Override
    public void update(long t) {
        List<EventBean> beans = windowAssigners.stream()
                .map(windowAssigner -> (ContentBean) windowAssigner.getContent(t))
                .flatMap(contentBean -> Arrays.stream(contentBean.asArray()))
                .collect(Collectors.toList());

        eval(null, beans.toArray(new EventBean[beans.size()]), t);
    }

    @Override
    public JenaGraph eval(long t) {
        graph.setTimestamp(t);
        return graph;
    }

    @Override
    public JenaGraph getContent(long now) {
        return graph;
    }

}