package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class AnalogStickObserver extends PressableIoFeatureObserver<StickPosZ> {

    private final AnalogStick stick;
    private final Direction direction;
    private final ButtonStateZ buttonState;

    AnalogStickObserver(@NotNull AnalogStick stick,
                        @NotNull Direction direction,
                        @NotNull StickPosZ internalState,
                        @NotNull ButtonStateZ buttonState,
                        @NotNull IoDeviceObserver observer) {
        super(stick, internalState, observer);
        this.stick = stick;
        this.direction = direction;
        this.buttonState = buttonState;
    }

    @Override
    protected boolean isPressed() {
        return AnalogStick.isPressed(internalState.calibratedPos, direction);
    }

    @Override
    protected void onPress() {
        buttonState.pressed = true;
        this.onNext(new AnalogStickPressEvent(device, stick, direction));
    }

    @Override
    protected void onHold() {
        buttonState.held = true;
        this.onNext(new AnalogStickHoldEvent(device, stick, direction));
    }

    @Override
    protected void onRelease() {
        buttonState.pressed = false;
        buttonState.held = false;
        this.onNext(new AnalogStickReleaseEvent(device, stick, direction));
    }

}