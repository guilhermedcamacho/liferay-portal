package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerServicesTracker;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.ArrayList;
import java.util.List;

@Component(
	immediate = true, service = ObjectEntryManagerServicesTracker.class
)
public class ObjectEntryManagerServicesTrackerImpl implements
	ObjectEntryManagerServicesTracker {

	@Override
	public ObjectEntryManager getObjectEntryManager(String key) {
		return _serviceTrackerMap.getService(key);
	}

	@Override
	public List<ObjectEntryManager> getObjectEntryManagers() {
		return new ArrayList(_serviceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ObjectEntryManager.class,
			"object.entry.manager.key");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, ObjectEntryManager>
		_serviceTrackerMap;
}
