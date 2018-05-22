package co.siempo.phone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import co.siempo.phone.R;

/**
 * Created by roma on 23/4/18.
 */

public class OverlayService extends Service {

    private WindowManager wm;
    private View bottomView;
    private Handler handler;
    private int delay;
    private int heightWindow;
    private Handler handlerNew;
    private View topView;
    private Runnable runnableViewBottom;
    private boolean isTopAdded;
    private int minusculeHeight;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            bottomView = ((LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                    .gray_scale_layout, null);
            wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 200;
            params.gravity = Gravity.BOTTOM;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            params.format = PixelFormat.TRANSLUCENT;


            topView = ((LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                    .gray_scale_layout_reverse, null);
            final WindowManager.LayoutParams paramsTop = new WindowManager
                    .LayoutParams();
            paramsTop.width = ViewGroup.LayoutParams.MATCH_PARENT;
            paramsTop.height = 0;
            paramsTop.gravity = Gravity.TOP;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                paramsTop.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                paramsTop.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            paramsTop.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            paramsTop.format = PixelFormat.TRANSLUCENT;

            if (null != wm) {
                wm.addView(bottomView, params);
                wm.addView(topView, paramsTop);
            }
            handler = new Handler();
            //milliseconds
            delay = 1000;
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            heightWindow = size.y * 6 / 9;
            minusculeHeight = size.y / 9;
            //Increase height of overlay
            runnableViewBottom = new Runnable() {
                public void run() {
                    try {
                        if (params.height <= heightWindow) {
                            //Increase height of overlay
                            params.height = params.height + minusculeHeight;
                            bottomView.setLayoutParams(new ViewGroup.LayoutParams(params));
                            wm.updateViewLayout(bottomView, params);
//                            handler.postDelayed(this, delay);
                        } else {
                            handler.removeCallbacksAndMessages(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (params.height != 0) {
                        paramsTop.height = 2 * topView.getLayoutParams().height;
                        topView.setLayoutParams(new ViewGroup.LayoutParams
                                (paramsTop));
                        wm.updateViewLayout(topView, paramsTop);
                        params.height = 0;
                        if (bottomView.getWindowToken() != null) {
                            wm.removeView(bottomView);
                        }
                    } else {
                        paramsTop.height = topView.getLayoutParams().height / 2;
                        topView.setLayoutParams(new ViewGroup.LayoutParams
                                (paramsTop));
                        wm.updateViewLayout(topView, paramsTop);
                        params.height = paramsTop.height;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                (params));
                        if (bottomView.getWindowToken() != null) {
                            wm.updateViewLayout(bottomView, params);
                        } else {
                            wm.addView(bottomView, params);
                        }
                    }
                }
            });

            bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (paramsTop.height != 0) {

                        params.height = 2 * bottomView.getLayoutParams().height;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                (params));
                        wm.updateViewLayout(bottomView, params);
                        paramsTop.height = 0;
                        if (topView.getWindowToken() != null) {
                            wm.removeView(topView);
                        }


                    } else {
                        params.height = bottomView.getLayoutParams().height / 2;
                        bottomView.setLayoutParams(new ViewGroup.LayoutParams
                                (params));
                        wm.updateViewLayout(bottomView, params);
                        paramsTop.height = params.height;
                        topView.setLayoutParams(new ViewGroup.LayoutParams
                                (paramsTop));
                        if (topView.getWindowToken() != null) {
                            wm.updateViewLayout(topView, paramsTop);
                        } else {
                            wm.addView(topView, paramsTop);
                        }


                    }
                }
            });


//            handler.postDelayed(runnableViewBottom, delay);
//            topView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    paramsTop.height = 2 * bottomView.getLayoutParams().height;
//                    topView.setLayoutParams(new ViewGroup.LayoutParams
//                            (paramsTop));
//                    wm.updateViewLayout(topView, paramsTop);
//                    params.height = 0;
////                    bottomView.getLayoutParams().height = 0;
//                    bottomView.setLayoutParams(new ViewGroup.LayoutParams
//                            (params));
//                    wm.updateViewLayout(bottomView, params);
//
////
////
////                    if (bottomView != null) {
////
////                    }
//
//                    return false;
//                }
//            });


//            bottomView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {

//                    if (!isTopAdded) {
//                        //splitting logic
//
////                    topView.setLayoutParams(params);
//                        if (null != topView && null != wm) {
//                            wm.addView(topView, params);
//                            isTopAdded = true;
//                        }
//
//                        handlerNew = new Handler();
//                        //milliseconds
//                        delay = 1000;
//                        Display display = wm.getDefaultDisplay();
//                        Point size = new Point();
//                        display.getSize(size);
//
//                        heightWindow = size.y / 3;
//                        handlerNew.postDelayed(new Runnable() {
//                            public void run() {
//                                try {
//
//                                    if (params.height <= heightWindow) {
//                                        //Increase height of overlay
//                                        params.height = params.height + minusculeHeight;
//                                        topView.setLayoutParams(new ViewGroup.LayoutParams(params));
//                                        wm.updateViewLayout(topView, params);
////                                        handlerNew.postDelayed(this, delay);
////                                        handler.postDelayed(runnableViewBottom, delay);
//                                    } else {
//                                        handlerNew.removeCallbacksAndMessages(null);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, delay);
//                    }

            //Split logic

//                    paramsTop.height = bottomView.getLayoutParams().height;
//                    topView.setLayoutParams(new ViewGroup.LayoutParams
//                            (paramsTop));
//                    wm.updateViewLayout(topView, paramsTop);
//
//
//                    return false;
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//        handlerNew.removeCallbacksAndMessages(null);
        removeView();
    }

    private void removeView() {
        try {
            if (bottomView != null && wm != null) {
                wm.removeView(bottomView);
                wm.removeView(topView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
