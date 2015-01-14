package kfl.app.kf6exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kfl.app.kf6exporter.model.KFContains;
import kfl.app.kf6exporter.model.KFLink;
import kfl.app.kf6exporter.model.KFObject;

public class KFViewLocationConverter {

	private Map<KFObject, List<KFLink>> views = new HashMap<KFObject, List<KFLink>>();

	public void put(KFObject view, KFLink link) {
		getLinks(view).add(link);
	}

	private List<KFLink> getLinks(KFObject view) {
		if (!views.containsKey(view)) {
			views.put(view, new ArrayList<KFLink>());
		}
		return views.get(view);
	}

	public void doConvert() {
		for (KFObject view : views.keySet()) {
			processConvert(getLinks(view));
		}
	}

	private void processConvert(List<KFLink> links) {
		int minX = 0;
		int minY = 0;
		for (KFLink link : links) {
			KFContains data = (KFContains) link.data;
			minX = Math.min(minX, data.x);
			minY = Math.min(minY, data.y);
		}
		int additiveX = (minX * -1) + 10;
		int additiveY = (minY * -1) + 10;
		for (KFLink link : links) {
			KFContains data = (KFContains) link.data;
			data.x = data.x + additiveX;
			data.y = data.y + additiveY;
		}
	}

}
