package com.commercetools.sync.services.impl;

import com.commercetools.sync.commons.BaseSyncOptions;
import com.commercetools.sync.commons.utils.CtpQueryUtils;
import com.commercetools.sync.services.TypeService;
import com.commercetools.sync.types.TypeSyncOptions;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.queries.PagedResult;
import io.sphere.sdk.queries.QueryExecutionUtils;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.commands.TypeUpdateCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import io.sphere.sdk.types.queries.TypeQueryBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.http.util.TextUtils.isBlank;

/**
 * Implementation of TypeService interface.
 * TODO: USE graphQL to get only keys. GITHUB ISSUE#84
 */
public final class TypeServiceImpl extends BaseService<Type, TypeDraft> implements TypeService {
    private static final String FETCH_FAILED = "Failed to fetch types with keys: '%s'. Reason: %s";

    public TypeServiceImpl(@Nonnull final BaseSyncOptions syncOptions) {
        super(syncOptions);
    }

    public TypeServiceImpl(@Nonnull final TypeSyncOptions syncOptions) {
        super(syncOptions);
    }

    @Nonnull
    @Override
    public CompletionStage<Optional<String>> fetchCachedTypeId(@Nonnull final String key) {
        if (!isCached) {
            return fetchAndCache(key);
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(keyToIdCache.get(key)));
    }

    @Nonnull
    @Override
    public CompletionStage<Set<Type>> fetchMatchingTypesByKeys(@Nonnull final Set<String> keys) {
        if (keys.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptySet());
        }

        final TypeQuery typeQuery = TypeQueryBuilder
            .of()
            .plusPredicates(queryModel -> queryModel.key().isIn(keys))
            .build();

        return QueryExecutionUtils.queryAll(syncOptions.getCtpClient(), typeQuery)
                                  .thenApply(types -> types
                                      .stream()
                                      .peek(type -> keyToIdCache.put(type.getKey(), type.getId()))
                                      .collect(toSet()));
    }

    @Nonnull
    @Override
    public CompletionStage<Optional<Type>> fetchType(@Nullable final String key) {
        if (isBlank(key)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return syncOptions.getCtpClient()
                          .execute(TypeQuery.of().plusPredicates(typeQueryModel -> typeQueryModel.key().is(key)))
                          .thenApply(PagedResult::head)
                          .exceptionally(sphereException -> {
                              syncOptions.applyErrorCallback(format(FETCH_FAILED, key, sphereException),
                                  sphereException);
                              return Optional.empty();
                          });
    }

    @Nonnull
    @Override
    public CompletionStage<Optional<Type>> createType(@Nonnull final TypeDraft typeDraft) {
        return applyCallbackAndCreate(typeDraft, typeDraft.getKey(), TypeCreateCommand::of);
    }

    @Nonnull
    @Override
    public CompletionStage<Type> updateType(@Nonnull final Type type,
                                            @Nonnull final List<UpdateAction<Type>> updateActions) {

        return updateResource(type, TypeUpdateCommand::of, updateActions);
    }

    @Nonnull
    private CompletionStage<Optional<String>> fetchAndCache(@Nonnull final String key) {
        final Consumer<List<Type>> typePageConsumer = typesPage ->
            typesPage.forEach(type -> keyToIdCache.put(type.getKey(), type.getId()));

        return CtpQueryUtils.queryAll(syncOptions.getCtpClient(), TypeQuery.of(), typePageConsumer)
                            .thenAccept(result -> isCached = true)
                            .thenApply(result -> Optional.ofNullable(keyToIdCache.get(key)));
    }
}
