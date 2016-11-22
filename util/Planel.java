package com.example.administrator.five_in_a_row.util;

import android.graphics.Point;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class Planel {

    public static Point getValidPoint(int x, int y,float lineheight) {
        //由于之前绘制棋盘时已经将横纵坐标都加了0.MAXNUMs lineheight，故这里就将棋盘上的点固定为（0,0）（1,0）
        //                                                                                                                                                                                                                                  (1,0)
        Point O = new Point((int) (x / lineheight), (int) (y / lineheight));
        return O;
    }
    //判断相邻的同色4个棋子是否满足4个方向的连续MAXNUMs个
    public static boolean checkFiveInLine(List<Point> points) {
        for (Point p :
                points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;
        }
        return false;
    }

    //水平方向上的比较
    public static boolean checkHorizontal(int x, int y, List<Point> points) {
        //该棋子向右4个
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x + 1, y)) &&
                points.contains(new Point(x + 2, y)) &&
                points.contains(new Point(x + 3, y)) &&
                points.contains(new Point(x + 4, y))) {
            return true;
        }
        return false;
    }

    //垂直方向
    public static boolean checkVertical(int x, int y, List<Point> points) {
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x, y + 1)) &&
                points.contains(new Point(x, y + 2)) &&
                points.contains(new Point(x, y + 3)) &&
                points.contains(new Point(x, y + 4))) {
            return true;
        }
        return false;
    }

    //左斜
    public static boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x - 1, y + 1)) &&
                points.contains(new Point(x - 2, y + 2)) &&
                points.contains(new Point(x - 3, y + 3)) &&
                points.contains(new Point(x - 4, y + 4))) {
            return true;
        }
        return false;
    }

    public static boolean checkRightDiagonal(int x, int y, List<Point> points) {
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x + 1, y + 1)) &&
                points.contains(new Point(x + 2, y + 2)) &&
                points.contains(new Point(x + 3, y + 3)) &&
                points.contains(new Point(x + 4, y + 4))) {
            return true;
        }
        return false;
    }



}
