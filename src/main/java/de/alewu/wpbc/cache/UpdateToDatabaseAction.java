package de.alewu.wpbc.cache;

import de.alewu.coreapi.db.caching.Cachable;
import de.alewu.coreapi.db.caching.Cache;
import de.alewu.coreapi.db.caching.CacheAction;
import de.alewu.coreapi.db.caching.CacheDatabaseSynchroInfo;
import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class UpdateToDatabaseAction extends CacheAction {

    @Override
    public void execute(CacheDatabaseSynchroInfo<? extends Cachable<?>> synchroInfo) {
        Optional<Cache<? extends Cachable<?>, ?>> cache = CacheRegistry.getCacheFromEntity(synchroInfo.getCacheEntityClass());
        if (!cache.isPresent()) {
            throw new CachingException("Somehow the cache " + synchroInfo.getCacheEntityClass() + " was not registered even if it was used before?");
        }
        Cache<? extends Cachable<?>, ?> c = cache.get();
        List<Cachable<?>> delete = new ArrayList<>();
        synchroInfo.getDatabaseConnection().findAll(synchroInfo.getCacheEntityClass(), l -> {
            Iterator<? extends Cachable<?>> iterator = l.iterator();
            for (Cachable<?> obj = null; iterator.hasNext(); obj = iterator.next()) {
                if (obj == null) {
                    continue;
                }
                if (!c.findById(obj.serializeId()).isPresent()) {
                    delete.add(obj);
                }
            }
        });
        delete.forEach(synchroInfo.getDatabaseConnection()::delete);
        c.getCache().forEach(synchroInfo.getDatabaseConnection()::save);
        System.gc();
    }
}
