package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeviceButton extends DeviceFeature<Button1b> {

    public final @Nullable Direction direction;

    /**
     * @param id        the button ID.
     * @param direction the direction this button represents. A {@code null}
     *                  value is permitted, and indicates that this button
     *                  does not represent a direction.
     */
    public DeviceButton(@NotNull String id, @Nullable Direction direction) {
        super(id, Button1b::new);
        this.direction = direction;
    }

    public DeviceButton(@NotNull String id) {
        this(id, null);
    }

}