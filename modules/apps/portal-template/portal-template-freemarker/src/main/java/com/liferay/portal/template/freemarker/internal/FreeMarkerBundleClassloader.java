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

import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
public class FreeMarkerBundleClassloader extends URLClassLoader {

	public FreeMarkerBundleClassloader(Bundle... bundles) {
		super(new URL[0]);

		if (bundles.length == 0) {
			throw new IllegalArgumentException("Bundles are empty");
		}

		Collections.addAll(_bundles, bundles);

		for (Bundle bundle : bundles) {
			String symbolicName = bundle.getSymbolicName();

			if (symbolicName.startsWith("com.liferay.util.taglib")) {
				_bundlesMap.put("com.liferay.taglib", bundle);
			}
			else {
				_bundlesMap.put(bundle.getSymbolicName(), bundle);
			}
		}
	}

	public void addBundle(Bundle bundle) {
		_bundles.add(bundle);
		_bundlesMap.put(bundle.getSymbolicName(), bundle);
	}

	@Override
	public URL findResource(String name) {
		for (Bundle bundle : _bundles) {
			URL url = bundle.getResource(name);

			if (url != null) {
				return url;
			}
		}

		return null;
	}

	@Override
	public Enumeration<URL> findResources(String name) {
		for (Bundle bundle : _bundles) {
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
		_bundles.remove(bundle);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Set<Map.Entry<String, Bundle>> entry = _bundlesMap.entrySet();

		Stream<Map.Entry<String, Bundle>> stream = entry.stream();

		Map<String, Bundle> filteredMap = stream.filter(
			x -> name.startsWith(x.getKey())
		).collect(
			Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
		);

		for (Bundle bundle : filteredMap.values()) {
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

	private static final Map<String, Bundle> _bundlesMap =
		new ConcurrentHashMap<>();

	private final Set<Bundle> _bundles = ConcurrentHashMap.newKeySet();

}