package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoFeature extends IoFeature<Object, Object> {

    Object internalState;
    Object containerState;

    MockIoFeature(@NotNull String id) {
        super(id);
        this.internalState = new Object();
        this.containerState = internalState;
    }

    MockIoFeature() {
        this("mock");
    }

    @Override
    protected @NotNull Object getInternalState() {
        return this.internalState;
    }

    @Override
    protected @NotNull Object getContainerState(@NotNull Object internalState) {
        return this.containerState;
    }

}
