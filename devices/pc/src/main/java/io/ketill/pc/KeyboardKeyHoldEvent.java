package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is held down.
 */
public final class KeyboardKeyHoldEvent extends IoFeatureHoldEvent
        implements KeyboardKeyEvent {

    KeyboardKeyHoldEvent(@NotNull IoDevice device,
                         @NotNull KeyboardKey key) {
        super(device, key);
    }

    @Override
    public @NotNull KeyboardKey getKey() {
        return (KeyboardKey) this.getFeature();
    }

}