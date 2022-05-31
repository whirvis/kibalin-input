package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;

/**
 * GLFW device seekers scan for I/O devices currently connected to a
 * GLFW window. When a device is connected to the window, the appropriate
 * {@code IoDevice} instance and adapter will be created. Devices must be
 * polled manually after creation using {@link IoDevice#poll()}. This can
 * also be done using {@link #pollDevices()}.
 * <p>
 * Implementations should call {@link #discoverDevice(IoDevice)} when a
 * device is discovered and {@link #forgetDevice(IoDevice)} when a device
 * is forgotten.
 * <p>
 * <b>Note:</b> For a GLFW device seeker to work as expected, scans must be
 * performed periodically via {@link #seek()}. It is recommended to perform
 * a scan once every application update.
 *
 * @param <I> the I/O device type.
 * @see GlfwDeviceAdapter
 * @see GlfwJoystickSeeker
 */
public abstract class GlfwDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> {

    /**
     * The pointer to the GLFW window that this seeker interfaces with.
     * <p>
     * This field is {@code protected} so it is visible to child classes.
     * This allows them to interface with the GLFW window directly.
     */
    protected final long ptr_glfwWindow;

    /**
     * Constructs a new {@code GlfwDeviceSeeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = GlfwUtils.requireWindow(ptr_glfwWindow);
    }

}