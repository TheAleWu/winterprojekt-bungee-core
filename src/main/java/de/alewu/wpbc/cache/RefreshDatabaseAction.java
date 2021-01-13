package de.alewu.wpbc.cache;

import de.alewu.coreapi.db.caching.Cachable;
import de.alewu.coreapi.db.caching.CacheAction;
import de.alewu.coreapi.db.caching.CacheDatabaseSynchroInfo;

public class RefreshDatabaseAction extends CacheAction {

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CacheDatabaseSynchroInfo<? extends Cachable<?>> synchroInfo) {
        synchroInfo.getCache().clear();
        synchroInfo.getDatabaseConnection().findAll(synchroInfo.getCacheEntityClass(), c -> {
            for (Cachable<?> cachable : c) {
                synchroInfo.getCache().add(cachable);
            }
        });
    }
}
