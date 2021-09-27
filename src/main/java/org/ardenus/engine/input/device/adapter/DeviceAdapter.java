package org.ardenus.engine.input.device.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.InputSource;
import org.ardenus.engine.input.device.adapter.mapping.AdapterMapping;
import org.ardenus.engine.input.device.adapter.mapping.AnalogMapping;
import org.ardenus.engine.input.device.adapter.mapping.ButtonMapping;
import org.ardenus.engine.input.device.adapter.mapping.InputMapping;

/**
 * An adapter which maps input for an {@link InputDevice}.
 * <p>
 * The purpose of a device adapter is to map data from a source (such as GLFW or
 * XInput) to an input device. This allows the same input device to be used with
 * different implementations. The use of adapters brings portability while also
 * providing a way to enable extra features, such as rumble or gyroscopes.
 * <p>
 * <b>Note:</b> For a device adapter to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the adapter once on every application update.
 *
 * @param <I>
 *            the input device type.
 * @see AnalogMapping
 * @see ButtonMapping
 */
public abstract class DeviceAdapter<I extends InputDevice> {

	protected final Logger log;

	private final Map<Class<?>, Method> adapters;
	private final Map<Class<?>, Class<?>> adapterHierarchy;
	private final Map<InputSource<?>, InputMapping<?>> mappings;
	private final Set<InputSource<?>> absentMappings;

	public DeviceAdapter() {
		this.log = LogManager.getLogger(this.getClass());

		this.adapters = new HashMap<>();
		this.adapterHierarchy = new HashMap<>();
		this.loadSourceAdapters();

		this.mappings = new HashMap<>();
		this.absentMappings = new HashSet<>();
		this.loadInputMappings();
	}

	private void loadSourceAdapters() {
		for (Method method : this.getClass().getDeclaredMethods()) {
			SourceAdapter adapter = method.getAnnotation(SourceAdapter.class);
			if (adapter == null) {
				continue;
			}

			/*
			 * For simplicity's sake, all analog adapters cannot be static, and
			 * they must be public. This ensures they behave like instanced
			 * methods are accessible by this class.
			 */
			int mod = method.getModifiers();
			if (Modifier.isStatic(mod)) {
				throw new InputException("button adapter cannot be static");
			} else if (!Modifier.isPublic(mod)) {
				throw new InputException("button adapter must be public");
			}

			/*
			 * Analog adapters update an already existing value, they do not
			 * return a new one. This is to cut down on object allocations. Do
			 * not ignore this issue, and throw an exception. It is likely this
			 * was a silly mistake by the programmer.
			 */
			if (method.getReturnType() != void.class) {
				throw new InputException("expecting void return type");
			}

			/*
			 * Analog adapter methods take two parameters. The first parameter
			 * is the analog they are going to update. The second parameter is
			 * the current value of the analog, which the method updates.
			 */
			Parameter[] params = method.getParameters();
			if (params.length != 2) {
				throw new InputException("expecting two parameters");
			}

			Class<?> adapterType = params[0].getType();
			if (!InputMapping.class.isAssignableFrom(adapterType)) {
				throw new InputException("TODO");
			}

			adapters.put(adapterType, method);
		}

		/*
		 * At runtime, the appropriate analog adapter method is chosen based on
		 * the highest order class assignable from the analog adapter type. If
		 * two or more classes are assignable to the same analog adapter, the
		 * device will not know which one to choose.
		 */
		for (Class<?> clazz : adapters.keySet()) {
			for (Class<?> lower : adapters.keySet()) {
				if (clazz != lower && clazz.isAssignableFrom(lower)) {
					throw new InputException("tangled hierarchy");
				}
			}
		}
	}

	private void requireAdapter(Class<?> mappingClazz) {
		/*
		 * Search for an appropriate analog adapter method and cache it for
		 * later calls. This will be used by updateAnalog() later. The first
		 * analog adapter method whose type is assignable from the mapped analog
		 * will be chosen. It is guaranteed by loadAnalogAdapters() that there
		 * will only be one class assignable to the mapped analog.
		 */
		if (!adapterHierarchy.containsKey(mappingClazz)) {
			for (Class<?> candidate : adapters.keySet()) {
				if (candidate.isAssignableFrom(mappingClazz)) {
					adapterHierarchy.put(mappingClazz, candidate);
					break;
				}
			}

			if (!adapterHierarchy.containsKey(mappingClazz)) {
				throw new InputException("unsupported analog type");
			}
		}
	}

	/**
	 * Returns if the adapter has a mapping for an input source.
	 * 
	 * @param source
	 *            the input source to check for.
	 * @return {@code true} if a mapping exists for {@code source},
	 *         {@code false} otherwise.
	 */
	public boolean hasMapping(InputSource<?> source) {
		if (source == null) {
			return false;
		}
		return mappings.containsKey(source);
	}

	/**
	 * Returns if an input mapping is registered to this adapter.
	 * 
	 * @param mapping
	 *            the input mapping to check for.
	 * @return {@code true} if {@code mapping} is registered to this adapter,
	 *         {@code false} otherwise.
	 */
	public boolean hasMapping(InputMapping<?> mapping) {
		if (mapping == null) {
			return false;
		}
		return this.hasMapping(mapping.source);
	}

	/**
	 * Returns the input mapping for an input source.
	 * 
	 * @param source
	 *            the input source.
	 * @return the mapping for {@code source}, {@code null} if none is
	 *         registered.
	 */
	protected InputMapping<?> getMapping(InputSource<?> source) {
		if (source == null) {
			return null;
		}
		return mappings.get(source);
	}

	/**
	 * Registers an input mapping to this adapter.
	 * 
	 * @param mapping
	 *            the mapping to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 * @throws InputException
	 *             if no adapter exists for {@code mapping}.
	 */
	protected DeviceAdapter<I> map(InputMapping<?> mapping) {
		Objects.requireNonNull(mapping, "mapping");
		this.requireAdapter(mapping.getClass());
		mappings.put(mapping.source, mapping);
		return this;
	}

	/**
	 * Registers the specified analog mappings to this adapter.
	 * <p>
	 * This method is a shorthand for {@link #map(A)}, with each value of
	 * {@code mappings} being passed as the argument for {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mappings} or one of its elements are {@code null}.
	 * @throws InputException
	 *             if no adapter exists for {@code mappings}.
	 */
	protected DeviceAdapter<I> map(InputMapping<?>... mappings) {
		Objects.requireNonNull(mappings, "mappings");
		for (InputMapping<?> mapping : mappings) {
			this.map(mapping);
		}
		return this;
	}

	/**
	 * Unregisters an input mapping from this adapter.
	 * 
	 * @param source
	 *            the source whose mapping to unregister.
	 * @return {@code true} if the mapping for {@code source} was unregistered
	 *         from this adapter, {@code false} otherwise.
	 */
	protected boolean unmap(InputSource<?> source) {
		if (source == null) {
			return false;
		}
		InputMapping<?> unmapped = mappings.remove(source);
		return unmapped != null;
	}

	/**
	 * Unregisters an analog mapping from this adapter.
	 * 
	 * @param mapping
	 *            the mapping to unregister.
	 * @return {@code true} if {@code mapping} was unregistered from this
	 *         adapter, {@code false} otherwise.
	 */
	protected boolean unmap(InputMapping<?> mapping) {
		if (mapping == null) {
			return false;
		}
		Iterator<InputMapping<?>> buttonsI = mappings.values().iterator();
		while (buttonsI.hasNext()) {
			InputMapping<?> value = buttonsI.next();
			if (mapping == value) {
				buttonsI.remove();
				return true;
			}
		}
		return false;
	}

	private void loadInputMappings() {
		for (Field field : this.getClass().getDeclaredFields()) {
			AdapterMapping mapping = field.getAnnotation(AdapterMapping.class);
			if (mapping == null) {
				continue;
			}

			/*
			 * All button mappings must be of a type extending MappedButton. Any
			 * other type will not contain the data necessary to map the input
			 * data. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */

			/*
			 * We must know if the field is static before trying to get its
			 * value. If it is static, we must pass null for the instance.
			 * Otherwise, we must pass this current adapter instance.
			 */
			int modifiers = field.getModifiers();
			boolean statik = Modifier.isStatic(modifiers);

			/*
			 * If the field is not accessible, temporarily grant access so the
			 * contents can be retrieved. Accessibility will be reverted to its
			 * original state later.
			 */
			boolean tempAccess = false;
			if (!field.canAccess(statik ? null : this)) {
				try {
					field.setAccessible(true);
					tempAccess = true;
				} catch (InaccessibleObjectException | SecurityException e) {
					throw new InputException("failure to set accessible", e);
				}
			}

			try {
				Object obj = field.get(statik ? null : this);
				InputMapping<?> mapped = (InputMapping<?>) obj;
				if (this.hasMapping(mapped.source)) {
					throw new InputException("already mapped");
				}
				this.map(mapped);
			} catch (IllegalAccessException e) {
				throw new InputException("failure to access", e);
			} finally {
				if (tempAccess) {
					field.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Returns if this device adapter is still connected.
	 * 
	 * @return {@code true} if this device adapter is still connected,
	 *         {@code false} otherwise.
	 */
	public abstract boolean isConnected();

	/**
	 * Queries the device for the current value of an input source.
	 * <p>
	 * For this method to work properly, {@code source} must have been given a
	 * mapping as well as an appropriate adapter at construction. If no mapping
	 * was specified, this method will be a no-op. If no adapter for the mapping
	 * type was registered, an {@code InputException} will be thrown.
	 * 
	 * @param source
	 *            the input source to query.
	 * @param value
	 *            the value container to update.
	 * @throws NullPointerException
	 *             if {@code source} or {@code value} are {@code null}.
	 * @throws InputException
	 *             if the mapping type for {@code source} has no adapter; if
	 *             {@code value} is of an invalid type for the adapter.
	 * @see SourceAdapter
	 * @see #map(InputMapping)
	 */
	public void update(InputSource<?> source, Object value) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(value, "value");

		InputMapping<?> mapping = mappings.get(source);
		if (mapping == null) {
			if (!absentMappings.contains(source)) {
				log.error("no mapping for source \"" + source.name() + "\"");
				absentMappings.add(source);
			}
			return;
		}

		Class<?> adapterClazz = adapterHierarchy.get(mapping.getClass());
		if (adapterClazz == null) {
			throw new InputException("no adapter for source");
		}

		Method adapter = adapters.get(adapterClazz);
		try {
			adapter.invoke(this, mapping, value);
		} catch (IllegalArgumentException e) {
			/*
			 * If the value parameter type is not assignable from the passed
			 * value type, that means the IllegalArgumentException was likely
			 * caused by that. Otherwise, it was caused by something else.
			 */
			Parameter valueParam = adapter.getParameters()[1];
			if (valueParam.getType().isAssignableFrom(value.getClass())) {
				throw new InputException("invalid value type for adapter", e);
			} else {
				throw e;
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Polls the adapter to update input device data.
	 * <p>
	 * Polling a device adapter is not always necessary for retrieving up to
	 * date input information. However, this is only down to how the adapter
	 * retrieves information from its source. As such, it is highly recommended
	 * to always poll the adapter; even if the method may be a no-op.
	 */
	public abstract void poll();

}