package it.polimi.yasper.core.engine.features;

import it.polimi.yasper.core.querying.ContinuousQuery;

public interface QueryDeletionFeature {

    void unregister(ContinuousQuery q);

}
