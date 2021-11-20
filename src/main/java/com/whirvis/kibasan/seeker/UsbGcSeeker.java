package com.whirvis.kibasan.seeker;

import java.util.HashMap;
import java.util.Map;

import org.usb4java.DeviceHandle;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.GcController;
import com.whirvis.kibasan.adapter.gamecube.GcUsbAdapter;
import com.whirvis.kibasan.adapter.gamecube.GcUsbDevice;

public class UsbGcSeeker extends UsbDeviceSeeker {

	private final boolean allowMultiple;
	private final Map<DeviceHandle, GcUsbDevice> hubs;
	private final Map<GcUsbAdapter, GcController> controllers;

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param allowMultiple
	 *            {@code true} if multiple USB GameCube adapters should be
	 *            recognized, {@code false} if only the first one found should
	 *            be used.
	 */
	public UsbGcSeeker(EventManager events, boolean allowMultiple) {
		super(GcController.class, events);

		this.allowMultiple = allowMultiple;
		this.hubs = new HashMap<>();
		this.controllers = new HashMap<>();

		this.seekDevice(GcUsbDevice.VENDOR_ID, GcUsbDevice.PRODUCT_ID);
	}

	/**
	 * Constructs a new {@code UsbGcSeeker} with support for multiple USB
	 * GameCube adapters enabled.
	 * 
	 * @param events
	 *            the event manager, may be {@code null}.
	 */
	public UsbGcSeeker(EventManager events) {
		this(events, true);
	}

	@Override
	protected void onAttach(DeviceHandle handle) {
		if (hubs.isEmpty() || allowMultiple) {
			hubs.put(handle, new GcUsbDevice(handle));
		}
	}

	@Override
	protected void onDetach(DeviceHandle device) {
		GcUsbDevice hub = hubs.remove(device);
		if (hub == null) {
			return;
		}
		
		hub.shutdown();
		for (GcUsbAdapter adapter : hub.getAdapters()) {
			GcController controller = controllers.get(adapter);
			if (controller != null) {
				this.unregister(controller);
			}
		}
	}

	@Override
	protected void onTrouble(DeviceHandle handle, Throwable cause) {
		/* TODO: handle this situation */
	}

	@Override
	protected void poll(DeviceHandle device) throws Exception {
		GcUsbDevice hub = hubs.get(device);
		if (hub == null) {
			return;
		}

		hub.poll();
		for (GcUsbAdapter adapter : hub.getAdapters()) {
			boolean connected = adapter.isConnected();
			boolean registered = controllers.containsKey(adapter);
			if (connected && !registered) {
				GcController controller = new GcController(events, adapter);
				controllers.put(adapter, controller);
				this.register(controller);
			} else if (!connected && registered) {
				GcController controller = controllers.remove(adapter);
				this.unregister(controller);
			}
		}
	}

}