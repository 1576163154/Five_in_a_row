package com.example.administrator.five_in_a_row;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Panel extends View {

    private Bitmap wpiece;
    private Bitmap bpiece;//可作为属性
    private float ratio = 3 / 4 * 1.0f;//设置棋子大小为行高的3/4

    private List<Point> whiteArray = new ArrayList<>();//声明两个 存储 用户touch坐标x，y的泛型数组
    private List<Point> blackArray = new ArrayList<>();
    private boolean isWhite = false;//声明一个布尔类型 确定 白棋先手，当前该谁

    private Paint panelpaint = new Paint();
    private int panelwidth;//画布宽度
    private float lineheight;
    private int MAX_LINE = 10;//行数可作为view属性 公开
    private int pieceWidth;//棋子宽度

    private boolean isGameOver;
    private boolean isWhiteWinner;
    private int MAXNUMs = 7;

    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);//用来看实际 view 的大小，后面可以删除或注释
        panelpaint.setColor(Color.BLACK);
        panelpaint.setAntiAlias(true);
        panelpaint.setDither(true);
        panelpaint.setStyle(Paint.Style.STROKE);
        //获取棋子图片 位图
        wpiece = BitmapFactory.decodeResource(getResources(), R.drawable.wpic);
        bpiece = BitmapFactory.decodeResource(getResources(), R.drawable.bpic);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //该view是一个正方形
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        panelwidth = w;
        lineheight = (float) panelwidth / MAX_LINE;
        pieceWidth = (int) (lineheight * ratio);//动态确定棋子大小
        wpiece = Bitmap.createScaledBitmap(wpiece, 57, 57, false);
        bpiece = Bitmap.createScaledBitmap(bpiece, 57, 57, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (isGameOver) {
            return false;
        }
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) (event.getX());
            int y = (int) (event.getY());//获取用户 触屏 点击的坐标值
            Log.d("LocationX", String.valueOf(x));
            Log.d("LocationY", String.valueOf(y));
            Point p = getValidPoint(x, y);//转换为规范的数组，便于之后判断游戏状态
            Log.d("ValidX", String.valueOf(p.x));
            Log.d("ValidY", String.valueOf(p.y));
            //如果某点被点过，则不处理
            if (whiteArray.contains(p) || blackArray.contains(p)) {
                return false;
            }

            if (isWhite) {
                whiteArray.add(p);
            } else {
                blackArray.add(p);
            }
            invalidate();//进行重绘
            isWhite = !isWhite;//将isWhite的布尔值进行颠倒，便于轮流下棋
        }
        return true;//表明 这个点击事件交由onTounchEvent处理
    }

    private Point getValidPoint(int x, int y) {
        //由于之前绘制棋盘时已经将横纵坐标都加了0.MAXNUMs lineheight，故这里就将棋盘上的点固定为（0,0）（1,0）
        //                                                                                                                                                                                                               (1,0)
        Point O = new Point((int) (x / lineheight), (int) (y / lineheight));
        return O;
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init(canvas);
        canvas.save();
        drawPiece(canvas);
        checkGameOver(canvas);
    }

    private void drawPiece(Canvas canvas) {
        //优化，避免每次都要计算list元素个数
        int m = whiteArray.size();
        for (int i = 0; i < m; i++) {
            Point whitePoint = whiteArray.get(i);//这里拿到的坐标是之前变换过，跟实际画布坐标不符。
            //另外画位图，和画文本类似。起点坐标都为所画位图的左上角（注 坐标原点在画布左上角）
            canvas.drawBitmap(wpiece, whitePoint.x * lineheight + lineheight / 8,
                    whitePoint.y * lineheight + lineheight / 8, null);
        }
        int n = blackArray.size();
        for (int i = 0; i < n; i++) {
            Point blackPoint = blackArray.get(i);
            canvas.drawBitmap(bpiece, blackPoint.x * lineheight + lineheight / 8,
                    blackPoint.y * lineheight + lineheight / 8, null);
        }
    }

    private void init(Canvas canvas) {
        int w = panelwidth;//画布宽度
        float lh = lineheight;//棋盘宽度，必须用float类型。以防止最后一行出现明显的误差。如int 768
        //view在measure方法中已确定，考虑到棋子会下在最边缘的情况。故所画线条必须距离view边缘有一定距离
        for (int i = 0; i < MAX_LINE; i++) {//先确定棋盘中的横线x，y
            int startX = (int) (lh / 2);//由于棋子会下在最左边缘，故起始线条坐标不是（0，y）而是（lh/2,
            int stopX = (int) (w - lh / 2);//同样最右边缘也要流出lh/2空间
            int y = (int) ((0.5 + i) * lh);
            canvas.drawLine(startX, y, stopX, y, panelpaint);
            canvas.drawLine(y, startX, y, stopX, panelpaint);//交换x，y 画竖线
        }

//        wpiece = BitmapFactory.decodeResource(getResources(),R.drawable.wpic);
//        bpiece = BitmapFactory.decodeResource(getResources(),R.drawable.bpic);


// for(int j = 0;j < MAX_LINE;j++){
//            int starty = (int) (0.MAXNUMs*lh);
//            int stopy = (int) (10.MAXNUMs*lh);
//            int x = (int) ((0.MAXNUMs+j)*lh);
//            canvas.drawLine(x,starty,x,stopy,panelpaint);
//        } 由于是 所画棋盘较为特殊，故交换x，y可画竖线
    }

    private void checkGameOver(Canvas canvas) {
        boolean whiteWin = checkFiveInLine(whiteArray);
        boolean blackWin = checkFiveInLine(blackArray);
        if (whiteWin || blackWin) {
            isGameOver = true;
            isWhiteWinner = whiteWin;
            Toast.makeText(getContext(), isWhiteWinner ? "白棋赢了！" : "黑棋赢了！", Toast.LENGTH_LONG).show();
            canvas.restore();
        }
    }

    //判断相邻的同色4个棋子是否满足4个方向的连续MAXNUMs个
    private boolean checkFiveInLine(List<Point> points) {
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
    private boolean checkHorizontal(int x, int y, List<Point> points) {
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
    private boolean checkVertical(int x, int y, List<Point> points) {
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
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x - 1, y + 1)) &&
                points.contains(new Point(x - 2, y + 2)) &&
                points.contains(new Point(x - 3, y + 3)) &&
                points.contains(new Point(x - 4, y + 4))) {
            return true;
        }
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        if (points.contains(new Point(x, y)) &&
                points.contains(new Point(x + 1, y+1)) &&
                points.contains(new Point(x + 2, y+2)) &&
                points.contains(new Point(x + 3, y+3)) &&
                points.contains(new Point(x + 4, y+4))) {
            return true;
        }
        return false;
    }

    //直接比较4个方向
    private boolean checkHVD(int x, int y, List<Point> points) {
        int count = 1;//计数器


        for (int i = 0; i < MAXNUMs; i++) {

            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                count = 1;
            }
            //向下
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                count = 1;
            }
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                count = 1;
            }
            //向右
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                count = 1;
            }
            //该棋子斜向上4个
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                count = 1;
            }
            //斜向下
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                count = 1;
            }
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                count = 1;
            }
            //斜向下
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                count = 1;
            }
        }
        if (count == MAXNUMs) {
            return true;
        }
        return false;
    }
}
