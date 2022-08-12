/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringPool;

import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.framework.Bundle;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
public class FreeMarkerBundleClassloader extends URLClassLoader {

	public FreeMarkerBundleClassloader(
		String[] symbolicNameMappers, Bundle... bundles) {

		super(new URL[0]);

		if ((symbolicNameMappers != null) && (symbolicNameMappers.length > 0)) {
			for (String symbolicNameMapper : symbolicNameMappers) {
				String[] parts = symbolicNameMapper.split(StringPool.EQUAL);

				_symbolicNameMappersMap.put(parts[0], parts[1]);
			}
		}

		if (bundles.length == 0) {
			throw new IllegalArgumentException("Bundles are empty");
		}

		for (Bundle bundle : bundles) {
			_bundlesMap.put(_getBundleKey(bundle), bundle);
		}
	}

	public void addBundle(Bundle bundle) {
		_bundlesMap.put(_getBundleKey(bundle), bundle);
	}

	@Override
	public URL findResource(String name) {
		for (Bundle bundle : _bundlesMap.values()) {
			URL url = bundle.getResource(name);

			if (url != null) {
				return url;
			}
		}

		return null;
	}

	@Override
	public Enumeration<URL> findResources(String name) {
		for (Bundle bundle : _bundlesMap.values()) {
			try {
				Enumeration<URL> enumeration = bundle.getResources(name);

				if ((enumeration != null) && enumeration.hasMoreElements()) {
					return enumeration;
				}
			}
			catch (IOException ioException) {
			}
		}

		return Collections.enumeration(Collections.<URL>emptyList());
	}

	@Override
	public URL getResource(String name) {
		return findResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) {
		return findResources(name);
	}

	public void removeBundle(Bundle bundle) {
		_bundlesMap.remove(_getBundleKey(bundle));
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Map.Entry<String, Bundle> entry = null;

		while ((entry = _getMatchedEntry(
					(entry != null) ? entry.getKey() : null, name)) != null) {

			Bundle bundle = entry.getValue();

			try {
				return bundle.loadClass(name);
			}
			catch (ClassNotFoundException classNotFoundException) {
			}
		}

		throw new ClassNotFoundException(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException {

		Class<?> clazz = findClass(name);

		if (resolve) {
			resolveClass(clazz);
		}

		return clazz;
	}

	private String _getBundleKey(Bundle bundle) {
		return _symbolicNameMappersMap.getOrDefault(
			bundle.getSymbolicName(), bundle.getSymbolicName());
	}

	private Map.Entry<String, Bundle> _getMatchedEntry(
		String fromKey, String name) {

		ConcurrentNavigableMap<String, Bundle> bundlesMap =
			((fromKey == null) || fromKey.isEmpty()) ? _bundlesMap :
				_bundlesMap.subMap(fromKey, false, _bundlesMap.lastKey(), true);

		for (Map.Entry<String, Bundle> entry : bundlesMap.entrySet()) {
			if (name.startsWith(entry.getKey())) {
				return entry;
			}
		}

		return null;
	}

	private final ConcurrentNavigableMap<String, Bundle> _bundlesMap =
		new ConcurrentSkipListMap<>();
	private final Map<String, String> _symbolicNameMappersMap = new HashMap<>();

}