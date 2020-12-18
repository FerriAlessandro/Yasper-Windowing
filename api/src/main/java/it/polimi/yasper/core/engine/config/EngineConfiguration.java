package it.polimi.yasper.core.engine.config;

import it.polimi.yasper.core.secret.time.Times;
import it.polimi.yasper.core.querying.ContinuousQuery;
import it.polimi.yasper.core.secret.report.Report;
import it.polimi.yasper.core.enums.ReportGrain;
import it.polimi.yasper.core.secret.report.ReportImpl;
import it.polimi.yasper.core.secret.report.strategies.NonEmptyContent;
import it.polimi.yasper.core.secret.report.strategies.OnContentChange;
import it.polimi.yasper.core.secret.report.strategies.OnWindowClose;
import it.polimi.yasper.core.secret.report.strategies.Periodic;
import it.polimi.yasper.core.enums.Tick;
import it.polimi.yasper.core.stream.metadata.StreamSchema;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;

import static it.polimi.yasper.core.engine.config.ConfigurationUtils.*;

/**
 * Created by riccardo on 10/07/2017.
 */
public class EngineConfiguration extends PropertiesConfiguration {

    private static EngineConfiguration config;

    public EngineConfiguration(String fileName) throws ConfigurationException {
        super(fileName);
    }


    public static EngineConfiguration getCurrent() throws ConfigurationException {
        if (config == null)
            return config;
        return getDefault();
    }



    public Boolean isUsingEventTime() {
        return Times.EventTime.equals(Times.valueOf(this.getString(TIME, Times.EventTime.name())));

    }

    public Boolean isUsingIngestionTime() {
        return Times.IngestionTime.equals(Times.valueOf(this.getString(TIME, Times.EventTime.name())));

    }

    public String getQueryClass() {
        return this.getString(QUERY_CLASS, ContinuousQuery.class.getCanonicalName());
    }

    public boolean isRecursionEnables() {
        return this.getBoolean(QUERY_RECURSION, false);
    }

    public boolean partialWindowsEnabled() {
        return this.getBoolean(PARTIAL_WINDOW, true);
    }

    public static EngineConfiguration loadConfig(String path) throws ConfigurationException {
        URL resource = EngineConfiguration.class.getResource(path);
        if (config == null) {
            config = new EngineConfiguration(resource.getPath());
        }
        return config;
    }

    public static EngineConfiguration getDefault() throws ConfigurationException {
        return loadConfig("/default.properties");
    }

    public String getBaseURI() {
        return this.getString("rsp_engine.base_uri");
    }

    public StreamSchema getStreamSchema() {
        try {
            return (StreamSchema) Class.forName(this.getString("rsp_engine.stream.item.class")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return StreamSchema.UNKNOWN;
    }

    public String getBaseIRI() {
        return this.getString(BASE_IRI);
    }


    public boolean onWindowClose() {
        return this.getBoolean(REPORT_STRATEGY_WC, false);
    }

    public boolean onContentChange() {
        return this.getBoolean(REPORT_STRATEGY_CC, false);
    }

    public boolean nonEmptyContent() {
        return this.getBoolean(REPORT_STRATEGY_NC, false);
    }

    public boolean periodic() {
        return this.getBoolean(REPORT_STRATEGY_PP, false);
    }

    public Tick getTick() {
        return Tick.valueOf(getString(TICK));
    }

    public ReportGrain getReportGrain() {
        return ReportGrain.valueOf(getString(REPORT_GRAIN));
    }

    public Report getReport() {
        Report report = new ReportImpl();

        if (onContentChange())
            report.add(new OnContentChange());
        if (nonEmptyContent())
            report.add(new NonEmptyContent());
        if (onContentChange())
            report.add(new OnWindowClose());
        if (periodic())
            report.add(new Periodic());

        //TODO remove period from policy

        return report;
    }

    public String getResponseFormat() {
        return this.getString("rsp_engine.response_format");
    }
}
