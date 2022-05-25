package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature that's registered to an {@link IoDevice}. This container
 * exists to group together the information necessary to fetch and update
 * the state of a device feature.
 * <p>
 * For optimal performance, it is best to cache the state of a device feature
 * to a field for later retrieval. The container state of a device feature can
 * be fetched via {@link IoDevice#getState(IoFeature)}.
 *
 * @param <F> the I/O feature type. Users can access this via the
 *            {@link #getFeature()} method when registering a feature.
 * @param <Z> the internal state type. The field containing the internal
 *            state is kept package-private. This prevents anyone other
 *            than the I/O device which owns it from accessing it.
 * @param <S> the state container type. Users can access this via the
 *            {@link #getState()} method when registering a feature.
 * @see FeaturePresent
 * @see FeatureState
 */
public final class RegisteredIoFeature<F extends IoFeature<Z, S>, Z, S> {

    /**
     * This should be used when a feature has no updater. Its purpose to
     * increase speed by removing an unnecessary nullability check.
     */
    public static final Runnable NO_UPDATER = () -> {
        /* nothing to update */
    };

    final @NotNull F feature;
    final @NotNull IoDeviceObserver observer;
    final @NotNull S containerState;
    final @NotNull Z internalState;
    final @NotNull Runnable autonomousUpdater;
    @NotNull Runnable adapterUpdater;

    RegisteredIoFeature(@NotNull F feature,
                        @NotNull IoDeviceObserver observer) {
        this.feature = feature;
        this.observer = observer;

        StatePair<Z, S> pair = feature.getState(observer);
        this.containerState = pair.container;
        this.internalState = pair.internal;

        if (internalState instanceof AutonomousState) {
            AutonomousState autonomy = (AutonomousState) internalState;
            this.autonomousUpdater = autonomy::update;
        } else {
            this.autonomousUpdater = NO_UPDATER;
        }

        this.adapterUpdater = NO_UPDATER;
    }

    /**
     * @return the registered I/O feature.
     */
    public @NotNull F getFeature() {
        return this.feature;
    }

    /**
     * @return the current state of the feature.
     */
    public @NotNull S getState() {
        return this.containerState;
    }

}