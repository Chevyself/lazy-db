package me.googas.lazy.jsongo;

import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import me.googas.lazy.cache.Catchable;
import org.bson.Document;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A subloader for catchable objects. Children of the loader {@link Jsongo}
 *
 * @param <T> the type of the catchable
 */
public abstract class CatchableJsongoSubloader<T extends Catchable> extends JsongoSubloader<T> {

    /**
     * Create the subloader.
     *
     * @param parent     the parent loader
     * @param collection the collection where the objects will be managed.
     */
    protected CatchableJsongoSubloader(@NonNull Jsongo parent, @NonNull MongoCollection<Document> collection) {
        super(parent, collection);
    }

    /**
     * Get a {@link Catchable} from the database. If the object is obtained from the database it will
     * be added to cache
     *
     * @param typeOfC the class of the catchable
     * @param query the query to match the catchable
     * @param predicate the predicate to match the catchable inside the cache
     * @param <C> the type of the catchable
     * @return a {@link Optional} instance holding the nullable catchable
     */
    protected Optional<T> get(@NonNull Document query, @NonNull Predicate<T> predicate) {
        return Optional.ofNullable(
                this.parent
                        .getCache()
                        .get(this.getTypeClazz(), predicate, true)
                        .orElseGet(
                                () -> {
                                    Optional<T> optional = this.get(query);
                                    optional.ifPresent(catchable -> this.parent.getCache().add(catchable));
                                    return optional.orElse(null);
                                }));
    }
}
