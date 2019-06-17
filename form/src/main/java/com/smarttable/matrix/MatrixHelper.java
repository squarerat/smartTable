package com.smarttable.matrix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.smarttable.component.IComponent;
import com.smarttable.data.TableInfo;
import com.smarttable.listener.Observable;
import com.smarttable.listener.OnTableChangeListener;
import com.smarttable.listener.TableClickObserver;

import java.util.List;


public class MatrixHelper extends Observable<TableClickObserver> implements ITouch, ScaleGestureDetector.OnScaleGestureListener {

    private float maxZoom = 5;
    private float minZoom = 1;
    private float zoom = minZoom;
    private int translateX;
    private int translateY;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private boolean isCanZoom = false;
    private boolean isScale;
    private Rect originalRect;
    private Rect zoomRect;
    private float mDownX, mDownY;
    private int pointMode;
    private Scroller scroller;
    private int mMinimumVelocity;
    private boolean isFling;
    private OnTableChangeListener listener;
    private float flingRate = 1f;
    private Rect scaleRect = new Rect();
    private boolean isZooming;
    private boolean isAutoFling = false;
    private OnInterceptListener onInterceptListener;
    int touchSlop;

    public MatrixHelper(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new OnTableGestureListener());
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        scroller = new Scroller(context);
        zoomRect = new Rect();
        originalRect = new Rect();
    }

    @Override
    public boolean handlerTouchEvent(MotionEvent event) {
        if (isCanZoom) {
            mScaleGestureDetector.onTouchEvent(event);
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onDisallowInterceptEvent(View view, MotionEvent event) {
        ViewParent parent = view.getParent();
        if (zoomRect == null || originalRect == null) {
            parent.requestDisallowInterceptTouchEvent(false);
            return;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pointMode = 1;

                mDownX = event.getX();
                mDownY = event.getY();
                if (originalRect.contains((int) mDownX, (int) mDownY)) {
                    parent.requestDisallowInterceptTouchEvent(true);
                } else {
                    parent.requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                pointMode += 1;
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointMode > 1) {
                    parent.requestDisallowInterceptTouchEvent(true);
                    return;
                }
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                boolean isDisallowIntercept = true;
                if (Math.abs(disX) > Math.abs(disY)) {
                    if ((disX > 0 && toRectLeft()) || (disX < 0 && toRectRight())) {
                        isDisallowIntercept = false;
                    }
                } else {
                    if ((disY > 0 && toRectTop()) || (disY < 0 && toRectBottom())) {
                        isDisallowIntercept = false;
                    }
                }
                parent.requestDisallowInterceptTouchEvent(isDisallowIntercept);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointMode -= 1;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                pointMode = 0;
                parent.requestDisallowInterceptTouchEvent(false);
        }
    }

    private boolean toRectLeft() {
        return translateX <= 0;
    }

    private boolean toRectRight() {
        return translateX >= zoomRect.width() - originalRect.width();
    }

    private boolean toRectBottom() {
        int height = zoomRect.height() - originalRect.height();
        return translateY >= height;
    }

    private boolean toRectTop() {
        return translateY <= 0;
    }

    private void notifyViewChanged() {
        if (listener != null) {
            listener.onTableChanged(zoom, translateX, translateY);
        }
    }

    @Override
    public void notifyObservers(List<TableClickObserver> observers) {

    }

    private int tempTranslateX;

    private int tempTranslateY;

    private float tempZoom = minZoom;

    class OnTableGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (onInterceptListener == null || !onInterceptListener.isIntercept(e1, distanceX, distanceY)) {

                translateX += distanceX;
                translateY += distanceY;

                if (listener != null) {
                    listener.onScrolled(distanceX, distanceY);
                }

                notifyViewChanged();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (Math.abs(velocityX) > mMinimumVelocity || Math.abs(velocityY) > mMinimumVelocity) {
                scroller.setFinalX(0);
                scroller.setFinalY(0);
                tempTranslateX = translateX;
                tempTranslateY = translateY;
                scroller.fling(0, 0, (int) velocityX, (int) velocityY, -50000, 50000
                        , -50000, 50000);
                isFling = true;
                startFilingAnim(false);
            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            isFling = false;

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isCanZoom) {
                float oldZoom = zoom;
                if (isScale) {
                    zoom = zoom / 1.5f;
                    if (zoom < minZoom) {
                        zoom = minZoom;
                        isScale = false;
                    }
                } else {
                    zoom = zoom * 1.5f;
                    if (zoom > maxZoom) {
                        zoom = maxZoom;
                        isScale = true;
                    }
                }
                float factor = zoom / oldZoom;
                resetTranslate(factor);
                notifyViewChanged();
            }

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            notifyViewChanged();
            for (TableClickObserver observer : observables) {
                observer.onClick(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            notifyViewChanged();
            for (TableClickObserver observer : observables) {
                observer.onLongClick(e.getX(), e.getY());
            }
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        tempZoom = this.zoom;
        isZooming = true;
        return true;
    }

    private boolean isScaleMax;
    private boolean isScaleMin;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float oldZoom = zoom;
        boolean isScaleEnd = false;
        float scale = detector.getScaleFactor();
        if (scale > 1 && isScaleMax) {
            isScaleMin = false;
            return true;
        } else if (scale < 1 && isScaleMin) {
            isScaleMax = false;
            return true;
        }
        this.zoom = tempZoom * scale;
        if (zoom >= maxZoom) {
            isScaleMax = true;
            this.zoom = maxZoom;
            isScaleEnd = true;
        } else if (this.zoom <= minZoom) {
            isScaleMin = true;
            this.zoom = minZoom;
            isScaleEnd = true;
        } else {
            isScaleMin = false;
            isScaleMax = false;
        }
        float factor = zoom / oldZoom;
        resetTranslate(factor);
        notifyViewChanged();
        return isScaleEnd;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isZooming = false;
    }

    private Point startPoint = new Point(0, 0);
    private Point endPoint = new Point();
    private TimeInterpolator interpolator = new DecelerateInterpolator();
    private PointEvaluator evaluator = new PointEvaluator();

    private void startFilingAnim(boolean doubleWay) {

        int scrollX = Math.abs(scroller.getFinalX());
        int scrollY = Math.abs(scroller.getFinalY());
        if (doubleWay) {
            endPoint.set((int) (scroller.getFinalX() * flingRate),
                    (int) (scroller.getFinalY() * flingRate));
        } else {
            if (scrollX > scrollY) {
                endPoint.set((int) (scroller.getFinalX() * flingRate), 0);
            } else {
                endPoint.set(0, (int) (scroller.getFinalY() * flingRate));
            }
        }
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(evaluator, startPoint, endPoint);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isFling) {
                    Point point = (Point) animation.getAnimatedValue();
                    translateX = tempTranslateX - point.x;
                    translateY = tempTranslateY - point.y;
                    notifyViewChanged();
                } else {
                    animation.cancel();
                }
            }
        });
        int duration = (int) (Math.max(scrollX, scrollY) * flingRate) / 2;
        valueAnimator.setDuration(duration > 300 ? 300 : duration);
        valueAnimator.start();
    }

    private void resetTranslate(float factor) {

        translateX = (int) (translateX * factor);
        translateY = (int) (translateY * factor);
    }

    public Rect getZoomProviderRect(Rect showRect, Rect providerRect, TableInfo tableInfo) {
        originalRect.set(showRect);
        int showWidth = showRect.width();
        int showHeight = showRect.height();
        int offsetX = (int) (showWidth * (zoom - 1)) / 2;
        int offsetY = (int) (showHeight * (zoom - 1)) / 2;
        if (!isAutoFling) {
            int oldw = providerRect.width();
            int oldh = providerRect.height();
            int newWidth = (int) (oldw * zoom);
            int newHeight = (int) (oldh * zoom);

            if (zoom > 1) {
                newWidth -= (int) (tableInfo.getyAxisWidth() * (zoom - 1));
                newHeight -= (int) (tableInfo.getTopHeight() * (zoom - 1));
            }


            if (tableInfo.getTitleDirection() == IComponent.TOP
                    || tableInfo.getTitleDirection() == IComponent.BOTTOM) {
                newHeight -= (int) (tableInfo.getTableTitleSize() * (zoom - 1));
            } else {
                newWidth -= (int) (tableInfo.getTableTitleSize() * (zoom - 1));
            }
            int minTranslateX = -offsetX;
            int maxTranslateX = newWidth - showWidth - offsetX;
            int minTranslateY = -offsetY;
            int maxTranslateY = newHeight - showHeight - offsetY;
            boolean isFullShowX = false, isFullShowY = false;

            if (maxTranslateX > minTranslateX) {
                if (translateX < minTranslateX) {
                    translateX = minTranslateX;

                } else if (translateX > maxTranslateX) {
                    translateX = maxTranslateX;
                }
            } else {
                isFullShowX = true;
            }
            if (maxTranslateY > minTranslateY) {
                if (translateY < minTranslateY) {
                    translateY = minTranslateY;
                } else if (translateY > maxTranslateY) {
                    translateY = maxTranslateY;
                }
            } else {
                isFullShowY = true;
            }
            scaleRect.left = providerRect.left - offsetX - translateX + showRect.left;
            scaleRect.top = providerRect.top - offsetY - translateY + showRect.top;
            if (isFullShowX) {
                if (isZooming) {
                    scaleRect.left = scaleRect.left < showRect.left ? showRect.left : scaleRect.left;
                    scaleRect.left = scaleRect.left > showRect.right - newWidth ? showRect.right - newWidth : scaleRect.left;
                } else {
                    scaleRect.left = showRect.left;
                    translateX = minTranslateX;
                }
            }
            if (isFullShowY) {
                if (isZooming) {
                    scaleRect.top = scaleRect.top < showRect.top ? showRect.top : scaleRect.top;
                    scaleRect.top = scaleRect.top > showRect.bottom - newHeight ? showRect.bottom - newHeight : scaleRect.top;
                } else {
                    scaleRect.top = showRect.top;
                    translateY = minTranslateY;
                }
            }
            scaleRect.right = scaleRect.left + newWidth;
            scaleRect.bottom = scaleRect.top + newHeight;
            zoomRect.set(scaleRect);
        } else {
            translateX = providerRect.left - zoomRect.left - offsetX;
            translateY = providerRect.top - zoomRect.top - offsetY;
            scaleRect.set(zoomRect);
        }
        return scaleRect;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public Rect getZoomRect() {
        return zoomRect;
    }

    public Rect getOriginalRect() {
        return originalRect;
    }

    public boolean isCanZoom() {
        zoom = 1f;
        return isCanZoom;

    }

    public OnTableChangeListener getOnTableChangeListener() {
        return listener;
    }

    public void setOnTableChangeListener(OnTableChangeListener onTableChangeListener) {
        this.listener = onTableChangeListener;
    }

    public void setCanZoom(boolean canZoom) {
        isCanZoom = canZoom;
        if (!isCanZoom) {
            zoom = 1;
        }
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(float minZoom) {

        if (minZoom < 0) {
            minZoom = 0.1f;
        }
        this.minZoom = minZoom;
    }

    public void setMaxZoom(float maxZoom) {
        if (maxZoom < 1) {
            maxZoom = 1;
        }
        this.maxZoom = maxZoom;
    }

    public void reset() {
        this.zoom = 1;
        this.translateX = 0;
        this.translateY = 0;
        notifyViewChanged();
    }

    public void flingLeft(int duration) {
        final int width = zoomRect.width();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(zoomRect.left, 0).setDuration(duration);
        valueAnimator.addListener(animatorListenerAdapter);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                zoomRect.left = (int) animation.getAnimatedValue();
                zoomRect.right = zoomRect.left + width;
                notifyViewChanged();
            }
        });
        valueAnimator.start();
    }

    public void flingRight(int duration) {
        final int width = zoomRect.width();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(zoomRect.right,
                originalRect.right).setDuration(duration);
        valueAnimator.addListener(animatorListenerAdapter);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                zoomRect.right = (int) animation.getAnimatedValue();
                zoomRect.left = zoomRect.right - width;
                notifyViewChanged();
            }
        });
        valueAnimator.start();
    }

    public void flingTop(int duration) {
        final int height = zoomRect.height();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(zoomRect.top, 0).setDuration(duration);
        valueAnimator.addListener(animatorListenerAdapter);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                zoomRect.top = (int) animation.getAnimatedValue();
                zoomRect.bottom = zoomRect.top + height;
                notifyViewChanged();
            }
        });
        valueAnimator.start();
    }

    public void flingBottom(int duration) {
        final int height = zoomRect.height();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(zoomRect.bottom,
                originalRect.bottom).setDuration(duration);
        valueAnimator.addListener(animatorListenerAdapter);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                zoomRect.bottom = (int) animation.getAnimatedValue();
                zoomRect.top = zoomRect.bottom - height;
                notifyViewChanged();
            }
        });
        valueAnimator.start();
    }

    private AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            isAutoFling = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isAutoFling = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isAutoFling = false;
        }
    };

    public float getZoom() {
        return zoom;
    }

    public float getFlingRate() {
        return flingRate;
    }

    public void setFlingRate(float flingRate) {
        this.flingRate = flingRate;
    }

    public OnInterceptListener getOnInterceptListener() {
        return onInterceptListener;
    }

    public void setOnInterceptListener(OnInterceptListener onInterceptListener) {
        this.onInterceptListener = onInterceptListener;
    }

    public interface OnInterceptListener {
        boolean isIntercept(MotionEvent e1, float distanceX, float distanceY);
    }

}
