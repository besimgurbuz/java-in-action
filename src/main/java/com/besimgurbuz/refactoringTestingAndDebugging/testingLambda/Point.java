package com.besimgurbuz.refactoringTestingAndDebugging.testingLambda;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point moveRightBy(int x) {
        return new Point(this.x + x, this.y);
    }

    public final static Comparator<Point> compareByXAndThenY =
            Comparator.comparing(Point::getX).thenComparing(Point::getY);

    @Override
    public boolean equals(Object obj) {
        Point p = (Point) obj;
        return obj != null && this.x == p.getX() && this.y == p.getY();
    }

    public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
        return points.stream()
                .map(p -> new Point(p.getX() + x, p.getY()))
                .collect(toList());
    }
}
