package com.whirvis.kibasan.feature.monitor;

import java.util.Objects;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.feature.DeviceFeature;

/**
 * A monitor for the features of an input device.
 * <p>
 * The purpose of a feature monitor is to monitor the state of an input device
 * and its features. What a monitor does in response to a change in state is
 * dependent on the implementation. A common use for monitors is to send events
 * which signal the change of a feature's state.
 */
public abstract class FeatureMonitor {

	protected final InputDevice device;
	protected final EventManager events;

	/**
	 * @param device
	 *            the device whose features to monitor.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public FeatureMonitor(InputDevice device, EventManager events) {
		this.device = Objects.requireNonNull(device, "device");
		this.events = EventManager.valueOf(events);
	}

	/**
	 * @param device
	 *            the device to check.
	 * @return {@code true} if this monitor is assigned to {@code device},
	 *         {@code false} otherwise.
	 */
	public final boolean isAssignedTo(InputDevice device) {
		return this.device == device;
	}

	/**
	 * Whether or not the monitor actually tracks the state of the feature is
	 * dependant on the implementation. Feature monitors have every right to
	 * ignore a feature that they are told to monitor. This method is only to
	 * indicate when they are registered to the input device, preventing the
	 * need to cycle through every device feature on every update.
	 * 
	 * @param feature
	 *            the feature to track.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 */
	public abstract void monitor(DeviceFeature<?> feature);

	public abstract void update();

}