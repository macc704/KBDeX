package kfl.app.kf6exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kfl.app.kf6exporter.model.K6Contains;
import kfl.app.kf6exporter.model.K6Link;
import kfl.app.kf6exporter.model.K6Object;

public class KFViewLocationConverter {

	private Map<K6Object, List<K6Link>> views = new HashMap<K6Object, List<K6Link>>();

	public void put(K6Object view, K6Link link) {
		getLinks(view).add(link);
	}

	private List<K6Link> getLinks(K6Object view) {
		if (!views.containsKey(view)) {
			views.put(view, new ArrayList<K6Link>());
		}
		return views.get(view);
	}

	public void doConvert() {
		for (K6Object view : views.keySet()) {
			processConvert(getLinks(view));
		}
	}

	private void processConvert(List<K6Link> links) {
		int minX = 0;
		int minY = 0;
		for (K6Link link : links) {
			K6Contains data = (K6Contains) link.data;
			minX = Math.min(minX, data.x);
			minY = Math.min(minY, data.y);
		}
		int additiveX = (minX * -1) + 10;
		int additiveY = (minY * -1) + 10;
		for (K6Link link : links) {
			K6Contains data = (K6Contains) link.data;
			data.x = data.x + additiveX;
			data.y = data.y + additiveY;
		}
	}

}
