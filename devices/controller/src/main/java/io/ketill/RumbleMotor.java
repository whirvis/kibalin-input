package io.ketill;

import org.jetbrains.annotations.NotNull;

public class RumbleMotor extends IoFeature<Vibration1f> {

    public RumbleMotor(@NotNull String id) {
        super(id, Vibration1f::new);
    }

}