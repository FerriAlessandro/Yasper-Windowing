package it.polimi.yasper.core.operators.s2r.execution.assigner;

import it.polimi.yasper.core.secret.content.Content;
import it.polimi.yasper.core.operators.s2r.execution.instance.Window;
import it.polimi.yasper.core.secret.report.Report;
import it.polimi.yasper.core.enums.ReportGrain;
import it.polimi.yasper.core.enums.Tick;
import it.polimi.yasper.core.secret.tick.Ticker;
import it.polimi.yasper.core.secret.tick.secret.TickerFactory;
import it.polimi.yasper.core.secret.time.Time;
import lombok.extern.log4j.Log4j;
import org.apache.commons.rdf.api.IRI;

import java.util.Observable;

@Log4j
public abstract class ObservableWindowAssigner<E, O> extends Observable implements Assigner<E, O> {

    protected Tick tick;
    protected ReportGrain grain;
    protected Report report;
    protected final Ticker ticker;
    protected final Time time;
    protected final IRI iri;

    protected ObservableWindowAssigner(IRI iri, Time time, Tick tick, Report report, ReportGrain grain) {
        this.time = time;
        this.iri = iri;
        this.tick = tick;
        this.ticker = TickerFactory.tick(tick, this);
        this.report = report;
        this.grain = grain;
    }

    @Override
    public void notify(E arg, long ts) {
        windowing(arg, ts);
    }

    @Override
    public Report report() {
        return report;
    }

    @Override
    public Tick tick() {
        return tick;
    }

    protected Content<E,O> setVisible(long t_e, Window w, Content<E,O> c) {
        log.debug("Report [" + w.getO() + "," + w.getC() + ") with Content " + c + "");
        setChanged();
        notifyObservers(t_e);
        return c;
    }

    protected abstract void windowing(E arg, long ts);

    @Override
    public String iri() {
        return iri.getIRIString();
    }

    @Override
    public boolean named() {
        return iri != null;
    }
}
