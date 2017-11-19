package com.ncatz.yeray697.managepc.components;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ncatz.yeray697.managepc.MouseListener;
import com.ncatz.yeray697.managepc.R;
import com.ncatz.yeray697.managepc.socket.Client;

/**
 * Created by yeray697 on 22/10/17.
 */

public class MouseView extends LinearLayout {

    private RelativeLayout rlLeftClick;
    private RelativeLayout rlWheel;
    private RelativeLayout rlRightClick;
    private RelativeLayout rlMousePad;

    private MouseListener mouseListener;

    private boolean mouseMoved = false;
    private boolean wheelScrolled = false;
    private Point lastMouseCoordinates;
    private int lastYPositionWheel = -1;

    public MouseView(Context context) {
        super(context);
        initialize();
    }

    public MouseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MouseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MouseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =
                (LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.mouseview, this, true);

        findViews();
        setEvents();
    }

    private void findViews() {
        rlLeftClick = (RelativeLayout) findViewById(R.id.rlLeftClick);
        rlWheel = (RelativeLayout) findViewById(R.id.rlWheel);
        rlRightClick = (RelativeLayout) findViewById(R.id.rlRightClick);
        rlMousePad = (RelativeLayout) findViewById(R.id.rlMousePad);
    }

    private void setEvents() {
        rlRightClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return manageRightClick(event.getAction());
            }
        });
        rlWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return manageWheel(event.getAction(), (int)event.getY());
            }
        });
        rlLeftClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return manageLeftClick(event.getAction());
            }
        });
        rlMousePad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return manageMouseMovement(event.getAction(), (int)event.getX(), (int)event.getY());
            }
        });
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    private boolean manageLeftClick(int eventAction) {
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if (mouseListener != null)
                    mouseListener.onClick(Client.ORDER_LEFT_CLICK_DOWN);
                break;

            case MotionEvent.ACTION_UP:
                if (mouseListener != null)
                    mouseListener.onClick(Client.ORDER_LEFT_CLICK_UP);
                break;
        }
        return true;
    }

    private boolean manageRightClick(int eventAction) {
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if (mouseListener != null)
                    mouseListener.onClick(Client.ORDER_RIGHT_CLICK_DOWN);
                break;
            case MotionEvent.ACTION_UP:
                if (mouseListener != null)
                    mouseListener.onClick(Client.ORDER_RIGHT_CLICK_UP);
                break;
        }
        return true;
    }

    private boolean manageWheel(int eventAction, int y) {

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                lastYPositionWheel = y;
                wheelScrolled = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (lastYPositionWheel != y) {
                    int yAux = y - lastYPositionWheel;
                    if (yAux != 0) {
                        yAux = (yAux < 0) ? -1 : 1;
                        if (mouseListener != null)
                            mouseListener.onMoveWheel(yAux);
                        wheelScrolled = true;
                    }
                }
                lastYPositionWheel = y;
                break;

            case MotionEvent.ACTION_UP:

                if (!wheelScrolled && mouseListener != null) {
                    mouseListener.onClick(Client.ORDER_WHEEL_CLICK_DOWN);
                    mouseListener.onClick(Client.ORDER_WHEEL_CLICK_UP);
                }
                lastYPositionWheel = Integer.MIN_VALUE;
                break;
        }
        return true;
    }

    private boolean manageMouseMovement(int eventAction, int x, int y) {
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                lastMouseCoordinates = new Point(x,y);
                mouseMoved = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (lastMouseCoordinates.x != x || lastMouseCoordinates.y != y) {
                    int width = rlMousePad.getWidth();
                    int height = rlMousePad.getHeight();
                    int MAX_VELOCITY = 50;
                    int MIN_VELOCITY = 2;
                    int xAux = x - lastMouseCoordinates.x,
                            yAux = y - lastMouseCoordinates.y;
                    xAux *= MIN_VELOCITY;
                    yAux *= MIN_VELOCITY;
                    //xAux = xAux * MAX_VELOCITY / (width - lastMouseCoordinates.x);
                    //yAux = yAux * MAX_VELOCITY / (height - lastMouseCoordinates.y);
                    if (mouseListener != null)
                        mouseListener.onMove(xAux, yAux);
                    mouseMoved = true;
                }
                lastMouseCoordinates = new Point(x, y);
                break;

            case MotionEvent.ACTION_UP:
                if (!mouseMoved && mouseListener != null) {
                    mouseListener.onClick(Client.ORDER_LEFT_CLICK_DOWN);
                    mouseListener.onClick(Client.ORDER_LEFT_CLICK_UP);
                }
                lastMouseCoordinates = null;
                break;
        }
        return true;
    }

    private int putBetweenRanges(int value, int min, int max) {
        int result = value;
        if (result < min) {
            result = min;
        } else if (result > max){
            result = max;
        }
        return result;
    }
}
