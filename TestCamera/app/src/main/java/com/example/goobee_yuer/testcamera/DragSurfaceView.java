package com.example.goobee_yuer.testcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Goobee_yuer on 2018/10/31.
 */

public class DragSurfaceView extends SurfaceView implements View.OnTouchListener {
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    private int offset = 20;

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        Log.i("DragViewTAG", "DragSurfaceView.initScreenW_H: screenWidth="+screenWidth+", screenHeight="+screenHeight);
    }
    public DragSurfaceView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        initScreenW_H();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
        // 处理拖动事件
        delDrag(v, event, action);
        if(action==MotionEvent.ACTION_UP){
            dragDirection=0;
        }
        invalidate();
        return true;
    }


    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 40 && bottom - top - y < 40) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     * @param action
     */
    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                    default:
                        break;
                }
                v.layout(oriLeft, oriTop, oriRight, oriBottom);
//        if (dragDirection != CENTER) {
//          v.layout(oriLeft, oriTop, oriRight, oriBottom);
//        }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                Log.i("DragViewTAG", "DragSurfaceView.delDrag:ACTION_MOVE direction="+dragDirection+", left="+oriLeft+", top="+oriTop+", right="+oriRight+", bottom="+oriBottom);
                break;
            case MotionEvent.ACTION_UP:
                ViewGroup.LayoutParams newLayoutParams = getNewLayoutParams();
                if(newLayoutParams!=null){
                    Log.i("DragViewTAG", "DragSurfaceView.delDrag:ACTION_UP width="+newLayoutParams.width+", height="+newLayoutParams.height);
                    setLayoutParams(newLayoutParams);
                }else {
                    Log.e("DragViewTAG", "DragSurfaceView.delDrag: 父组件类型？");
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                break;
            default:
                break;
        }
    }

    private ViewGroup.LayoutParams getNewLayoutParams(){
        if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)getLayoutParams( );
            lp.leftMargin = oriLeft;
            lp.topMargin = oriTop;
            lp.width = oriRight-oriLeft;
            lp.height = oriBottom-oriTop;
            return lp;
        }else if(getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
            lp.leftMargin = oriLeft;
            lp.topMargin = oriTop;
            lp.width = oriRight - oriLeft;
            lp.height = oriBottom - oriTop;
            return lp;
        }
        return null;
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        oriLeft += dx;
        oriTop += dy;
        oriRight += dx;
        oriBottom += dy;
        Log.i("DragViewTAG", "DragSurfaceView.center: v.left="+v.getLeft()+", v.top="+v.getTop());
        if (oriLeft < -offset) {
            Log.e("DragViewTAG", "DragSurfaceView.center: 左侧越界, left="+oriLeft+", offset="+offset);
            oriLeft = -offset;
            oriRight = oriLeft + v.getWidth();
        }
        if (oriRight > screenWidth + offset) {
            Log.e("DragViewTAG", "DragSurfaceView.center: 右侧越界, right="+oriRight+", screenWidth="+screenWidth+", offset="+offset);
            oriRight = screenWidth + offset;
            oriLeft = oriRight - v.getWidth();
        }
        if (oriTop < -offset) {
            Log.e("DragViewTAG", "DragSurfaceView.center: 顶部越界, top="+oriTop+", offset="+offset);
            oriTop = -offset;
            oriBottom = oriTop + v.getHeight();
        }
        if (oriBottom > screenHeight + offset) {
            Log.e("DragViewTAG", "DragSurfaceView.center: 底部越界, bottom="+oriBottom+", screenHeight="+screenHeight+", offset="+offset);
            oriBottom = screenHeight + offset;
            oriTop = oriBottom - v.getHeight();
        }
//    v.layout(left, top, right, bottom);

    }

    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offset) {
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriTop = oriBottom - 2 * offset - 200;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriBottom = 200 + oriTop + 2 * offset;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriRight = oriLeft + 2 * offset + 200;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriLeft = oriRight - 2 * offset - 200;
        }
    }

    /**
     * 获取截取宽度
     *
     * @return
     */
    public int getCutWidth() {
        return getWidth() - 2 * offset;
    }

    /**
     * 获取截取高度
     *
     * @return
     */
    public int getCutHeight() {
        return getHeight() - 2 * offset;
    }
}
