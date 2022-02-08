package io.ketill.pc;

import io.ketill.Button1b;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class MouseButton extends IoFeature<Button1b> {

    public MouseButton(@NotNull String id) {
        super(id, Button1b::new);
    }

}