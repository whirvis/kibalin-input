package org.ardenus.engine.input.device.adapter.glfw.analog;

import org.ardenus.engine.input.device.adapter.glfw.GLFWDeviceAdapter;
import org.ardenus.engine.input.device.analog.AnalogStick;

/**
 * An {@link AnalogStick} mapping for use with a {@link GLFWDeviceAdapter}.
 */
public class GLFWAnalogStickMapping extends GLFWAnalogMapping<AnalogStick> {

	public final int glfwAxisX;
	public final int glfwAxisY;

	/**
	 * Constructs a new {@code GLFWMappedAnalogStick}.
	 * 
	 * @param analog
	 *            the stick being mapped to.
	 * @param glfwAxisX
	 *            the GLFW stick X-axis.
	 * @param glfwAxisY
	 *            the GLFW stick Y-axis.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public GLFWAnalogStickMapping(AnalogStick analog, int glfwAxisX,
			int glfwAxisY) {
		super(analog);
		this.glfwAxisX = glfwAxisX;
		this.glfwAxisY = glfwAxisY;
	}

}