package io.ketill.glfw.xbox;

import io.ketill.MappedFeatureRegistry;
import io.ketill.controller.Trigger1f;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.glfw.GlfwStickMapping;
import io.ketill.glfw.WranglerMethod;
import io.ketill.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static io.ketill.xbox.XboxController.*;

public class GlfwXboxAdapter extends GlfwJoystickAdapter<XboxController> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 8),
            RS_MAPPING = new GlfwStickMapping(2, 3, 9);
    /* @formatter:on */

    protected static final int AXIS_LT = 4, AXIS_RT = 5;

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled XBOX controller.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    /* @formatter:off */
    @WranglerMethod
    public static @NotNull XboxController
                wrangle(long ptr_glfwWindow, int glfwJoystick) {
        return new XboxController((c, r) -> new GlfwXboxAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }
    /* @formatter:on */

    /**
     * @param controller     the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code controller} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    public GlfwXboxAdapter(@NotNull XboxController controller,
                           @NotNull MappedFeatureRegistry registry,
                           long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_A, 0);
        this.mapButton(BUTTON_B, 1);
        this.mapButton(BUTTON_X, 2);
        this.mapButton(BUTTON_Y, 3);
        this.mapButton(BUTTON_LB, 4);
        this.mapButton(BUTTON_RB, 5);
        this.mapButton(BUTTON_GUIDE, 6);
        this.mapButton(BUTTON_START, 7);
        this.mapButton(BUTTON_L_THUMB, 8);
        this.mapButton(BUTTON_R_THUMB, 9);
        this.mapButton(BUTTON_UP, 10);
        this.mapButton(BUTTON_RIGHT, 11);
        this.mapButton(BUTTON_DOWN, 12);
        this.mapButton(BUTTON_LEFT, 13);

        this.mapStick(STICK_LS, LS_MAPPING);
        this.mapStick(STICK_RS, RS_MAPPING);

        this.mapTrigger(TRIGGER_LT, AXIS_LT);
        this.mapTrigger(TRIGGER_RT, AXIS_RT);
    }

    @Override
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(stick, mapping);
        if (mapping == LS_MAPPING || mapping == RS_MAPPING) {
            stick.y *= -1.0F;
        }
    }

    @Override
    protected void updateTrigger(@NotNull Trigger1f trigger, int glfwAxis) {
        super.updateTrigger(trigger, glfwAxis);
        if (glfwAxis == AXIS_LT || glfwAxis == AXIS_RT) {
            trigger.force += 1.0F;
            trigger.force /= 2.0F;
        }
    }

}
