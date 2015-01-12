package kfl.app.kf6exporter.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class KFShape extends KFContribution {
	public String shapeType;
	public Color color = Color.black;
	public Color fill;
	public int penWidth;
	public Point point1;
	public Point point2;
	public List<Point> points;

	public String getSvg(Point loc) {
		return getSvg(getTranslated(point1, loc), getTranslated(point2, loc),
				getTranslated(points, loc));
	}

	public Rectangle getSize(Point loc) {
		Rectangle r = new Rectangle();
		if (point1 != null && point2 != null) {
			r.add(point1);
			r.add(point2);
		} else {
			for (Point p : points) {
				r.add(p);
			}
		}
		r.translate(loc.x, loc.y);
		return r;
	}

	private List<Point> getTranslated(List<Point> org, Point d) {
		if (org == null) {
			return null;
		}
		List<Point> newPoints = new ArrayList<Point>();
		for (Point point : this.points) {
			newPoints.add(getTranslated(point, d));
		}
		return newPoints;
	}

	private Point getTranslated(Point org, Point d) {
		if (org == null) {
			return null;
		}
		Point newP = new Point(org);
		newP.translate(d.x, d.y);
		return newP;
	}

	private String getSvg(Point point1, Point point2, List<Point> points) {

		String svg;
		if (shapeType.equals("line")) {
			svg = "<line x1='%X1%' y1='%Y1%' x2='%X2%' y2='%Y2%' stroke-width='%PENWIDTH%' stroke='%COLOR%' fill='%FILL%'/>";
			svg = svg.replace("%X1%", Integer.toString(point1.x));
			svg = svg.replace("%Y1%", Integer.toString(point1.y));
			svg = svg.replace("%X2%", Integer.toString(point2.x));
			svg = svg.replace("%Y2%", Integer.toString(point2.y));
		} else if (shapeType.equals("rect")) {
			svg = "<rect x='%X%' y='%Y%' width='%WIDTH%' height='%HEIGHT%' stroke-width='%PENWIDTH%' stroke='%COLOR%' fill='%FILL%'/>";
			int x = Math.min(point1.x, point2.x);
			int y = Math.min(point1.y, point2.y);
			int width = Math.abs(point2.x - point1.x);
			int height = Math.abs(point2.y - point1.y);
			svg = svg.replace("%X%", Integer.toString(x));
			svg = svg.replace("%Y%", Integer.toString(y));
			svg = svg.replace("%WIDTH%", Integer.toString(width));
			svg = svg.replace("%HEIGHT%", Integer.toString(height));
		} else if (shapeType.equals("oval")) {
			svg = "<ellipse cx='%X%' cy='%Y%' rx='%WIDTH%' ry='%HEIGHT%' stroke-width='%PENWIDTH%' stroke='%COLOR%' fill='%FILL%'/>";
			int x = (point1.x + point2.x) / 2;
			int y = (point1.y + point2.y) / 2;
			int width = Math.abs(point2.x - point1.x) / 2;
			int height = Math.abs(point2.y - point1.y) / 2;
			svg = svg.replace("%X%", Integer.toString(x));
			svg = svg.replace("%Y%", Integer.toString(y));
			svg = svg.replace("%WIDTH%", Integer.toString(width));
			svg = svg.replace("%HEIGHT%", Integer.toString(height));
		} else if (shapeType.equals("brush")) {
			svg = "<path d='%PATH%' stroke-width='%PENWIDTH%' stroke='%COLOR%' fill='%FILL%'/>";
			StringBuffer path = new StringBuffer();
			int len = points.size();
			for (int i = 0; i < len; i++) {
				Point p = points.get(i);
				if (i == 0) {
					path.append("M");
				} else {
					path.append(" ");
					path.append("L");
				}
				path.append(" ");
				path.append(Integer.toString(p.x));
				path.append(" ");
				path.append(Integer.toString(p.y));
			}
			svg = svg.replace("%PATH%", path.toString());
		} else {
			svg = "";
		}
		String colorStr = "#"
				+ Integer.toHexString(color.getRGB()).substring(2);
		svg = svg.replace("%COLOR%", colorStr);
		String fillStr = "none";
		if (fill != null) {
			fillStr = "#" + Integer.toHexString(fill.getRGB()).substring(2);
		}
		svg = svg.replace("%FILL%", fillStr);
		svg = svg.replace("%PENWIDTH%", Integer.toString(penWidth));
		return svg;
	}
}
