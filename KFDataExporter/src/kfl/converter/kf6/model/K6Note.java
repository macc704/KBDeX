package kfl.converter.kf6.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kfl.converter.kf4.model.K4TextLocator;

public class K6Note extends K6Contribution {
	public transient Map<K6Link, K4TextLocator> supporteds = new LinkedHashMap<K6Link, K4TextLocator>();
	public transient Map<K6Link, K4TextLocator> references = new LinkedHashMap<K6Link, K4TextLocator>();
	public transient List<Integer> offsets;
	public String body;

}
