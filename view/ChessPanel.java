package com.example.administrator.five_in_a_row.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.activity.GameActivity;
import com.example.administrator.five_in_a_row.activity.MenuActivity;
import com.example.administrator.five_in_a_row.activity.SettingActivity;
import com.example.administrator.five_in_a_row.util.Planel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/16.
 */
public class ChessPanel extends View {

    private Bitmap wpiece;
    private Bitmap bpiece;//可作为属性
    private float ratio = 3 * 1.0f/ 4 ;//设置棋子大小为行高的3/4
    private WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

    private ArrayList<Point> whiteArray = new ArrayList<>();//声明两个 存储 用户touch坐标x，y的泛型数组
    private ArrayList<Point> blackArray = new ArrayList<>();

    public static void setWhite(boolean white) {
        isWhite = white;
    }

    private static boolean isWhite;//声明一个布尔类型 确定 白棋先手，当前该谁
    private Point lastWhitePoint;//白棋当前最后一步
    private Point lastBlackPoint;

    public boolean isWhite() {
        return isWhite;
    }

    private Paint panelpaint = new Paint();
    private int panelwidth;//画布宽度
    private float lineheight;
    private int MAX_LINE = 10;//行数可作为view属性 公开
    private int pieceWidth;//棋子宽度
    //音效相关
    private SoundPool sp_chess;
    private int sp_chessId;//落子音效文件id
    private SoundPool sp_victory;
    private int sp_victory_id;

    public static void setPlayAudio(boolean playAudio) {
        isPlayAudio = playAudio;
    }

    private static boolean isPlayAudio;//默认播放音效,这里获取到的为false

    public boolean isGameOver() {
        return isGameOver;
    }

    private boolean isGameOver;

    public boolean isWhiteWinner() {
        return isWhiteWinner;
    }

    private boolean isWhiteWinner;

    public ChessPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.parseColor("#deb887"));//用来看实际 view 的大小，后面可以删除或注释
        panelpaint.setColor(Color.BLACK);
        panelpaint.setAntiAlias(true);
        panelpaint.setDither(true);
        panelpaint.setStyle(Paint.Style.STROKE);
        //获取棋子图片 位图
        wpiece = BitmapFactory.decodeResource(getResources(), R.drawable.wpic);
        bpiece = BitmapFactory.decodeResource(getResources(), R.drawable.bpic);

        sp_chess = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);//同时播放音效数量，类型，质量
        sp_chessId = sp_chess.load(getContext(), R.raw.chess, 1);//上下文，资源文件，优先级
        sp_victory = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        sp_victory_id = sp_victory.load(getContext(), R.raw.victory, 1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //view 默认大小为屏幕宽度
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = windowManager.getDefaultDisplay().getWidth();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = windowManager.getDefaultDisplay().getWidth();
        }
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
        lineheight =  panelwidth*1.0f/ MAX_LINE;
        pieceWidth = (int) (lineheight * ratio + 0.5);//动态确定棋子大小

        wpiece = Bitmap.createScaledBitmap(wpiece, pieceWidth, pieceWidth, false);
        bpiece = Bitmap.createScaledBitmap(bpiece, pieceWidth, pieceWidth, false);
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
        //                                                                                                                                                                                                                                  (1,0)
        Point O = new Point((int) (x / lineheight), (int) (y / lineheight));
        return O;
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    private void drawPiece(Canvas canvas) {
        //优化，避免每次都要计算list元素个数
        int m = whiteArray.size();
        for (int i = 0; i < m; i++) {
            Point whitePoint = whiteArray.get(i);//这里拿到的坐标是之前变换过，跟实际画布坐标不符。
            //另外画位图，和画文本类似。起点坐标都为所画位图的左上角（注 坐标原点在画布左上角）
            canvas.drawBitmap(wpiece, (whitePoint.x + (1 - ratio) / 2) * lineheight ,
                    (whitePoint.y + (1 - ratio) / 2) * lineheight , null);
            lastWhitePoint = whiteArray.get(m - 1);
            if (isPlayAudio == true && !isGameOver) {
                sp_chess.play(sp_chessId, 1f, 1f, 1, 0, 2);
            }
        }
        int n = blackArray.size();
        for (int i = 0; i < n; i++) {
            Point blackPoint = blackArray.get(i);
            canvas.drawBitmap(bpiece, (blackPoint.x + (1 - ratio) / 2) * lineheight ,
                    (blackPoint.y + (1 - ratio) / 2) * lineheight , null);
            lastBlackPoint = blackArray.get(n - 1);
            if (isPlayAudio == true && !isGameOver) {
                sp_chess.play(sp_chessId, 1f, 1f, 1, 0, 2);
            }
        }
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(2.0f);
        redPaint.setStyle(Paint.Style.STROKE);
        if (m > 0 && !isWhite) {
            //给白棋最后一个棋子画个圆圈
            canvas.drawCircle(lastWhitePoint.x * lineheight + lineheight / 2, lastWhitePoint.y * lineheight + lineheight / 2, pieceWidth/2, redPaint);
        } else if (n > 0 && isWhite) {
            //同理给黑棋
            canvas.drawCircle(lastBlackPoint.x * lineheight + lineheight / 2, lastBlackPoint.y * lineheight + lineheight / 2, pieceWidth/2, redPaint);
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

    private void checkGameOver() {
        boolean whiteWin = Planel.checkFiveInLine(whiteArray);
        boolean blackWin = Planel.checkFiveInLine(blackArray);
        if (whiteWin || blackWin) {
            isGameOver = true;
            isWhiteWinner = whiteWin;
            if (isPlayAudio == true) {
                sp_victory.play(sp_victory_id, 1f, 1f, 1, 0, 1);
            }
            final AlertDialog winnerDialog = new AlertDialog.Builder(getContext()).create();
            winnerDialog.show();
            winnerDialog.setCancelable(false);
            Window window = winnerDialog.getWindow();
            window.setContentView(R.layout.gameover_dialog);
            Button btn_onemoregame = (Button) window.findViewById(R.id.btn_win_onemoregame);
            Button btn_nomoregame = (Button) window.findViewById(R.id.btn_win_nomoregame);
            TextView tv_winner = (TextView) window.findViewById(R.id.tv_winner);
            tv_winner.setText(isWhiteWinner ? "白棋胜利！" : "黑棋胜利！");
            btn_nomoregame.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    winnerDialog.dismiss();
                }
            });
            btn_onemoregame.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    oneMoreGame();
                    winnerDialog.dismiss();
                }
            });
        }
    }


    //再来一局
    public void oneMoreGame() {
        whiteArray.clear();
        blackArray.clear();
        isGameOver = false;
        isWhiteWinner = false;
        isWhite = MenuActivity.isGameWhoFirst();
        invalidate();//重绘
    }

    //悔棋
    public void withDraw() {
        if (whiteArray.size() > 0 || blackArray.size() > 0) {
            if (blackArray.size() > 0 && isWhite) {
                int blackIndex = blackArray.size() - 1;
                if (isGameOver) {
                    isGameOver = !isGameOver;
                }
                blackArray.remove(blackIndex);
                isWhite = !isWhite;
                invalidate();
            } else if (whiteArray.size() > 0 && !isWhite) {
                int whiteIndex = whiteArray.size() - 1;
                if (isGameOver) {
                    isGameOver = !isGameOver;
                }
                whiteArray.remove(whiteIndex);
                isWhite = !isWhite;
                invalidate();
            }
        } else {
            Toast.makeText(getContext(), "没棋了!", Toast.LENGTH_SHORT).show();
        }
    }

    //判断棋盘上棋子是否为空
    public boolean isPanelEmpty() {
        if (whiteArray.size() > 0 || blackArray.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    //view的存储与恢复（常见于内存不足进程被系统杀死，切换屏幕方向）
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instance", super.onSaveInstanceState());
        bundle.putParcelableArrayList("whitelist", whiteArray);
        bundle.putParcelableArrayList("blacklist", blackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle && state != null) {
            Bundle bundle = (Bundle) state;
            whiteArray = bundle.getParcelableArrayList("whitelist");
            blackArray = bundle.getParcelableArrayList("blacklist");
            super.onRestoreInstanceState(bundle.getParcelable("instance"));
        }
        super.onRestoreInstanceState(state);
    }
}
