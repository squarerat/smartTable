package com.smarttable.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.smarttable.component.IComponent;
import com.smarttable.component.ITableTitle;
import com.smarttable.component.TableProvider;
import com.smarttable.component.TableTitle;
import com.smarttable.component.XSequence;
import com.smarttable.component.YSequence;
import com.smarttable.data.TableInfo;
import com.smarttable.data.column.Column;
import com.smarttable.data.format.selected.ISelectFormat;
import com.smarttable.data.style.FontStyle;
import com.smarttable.data.table.PageTableData;
import com.smarttable.data.table.TableData;
import com.smarttable.listener.OnColumnClickListener;
import com.smarttable.listener.OnTableChangeListener;
import com.smarttable.matrix.MatrixHelper;
import com.smarttable.utils.DensityUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SmartTable<T> extends View implements OnTableChangeListener {

    private XSequence<T> xAxis;
    private YSequence<T> yAxis;
    private ITableTitle tableTitle;
    private TableProvider<T> provider;
    private Rect showRect;
    private Rect tableRect;
    private TableConfig config;
    private TableParser<T> parser;
    private AtomicReference<TableData<T>> tableDataAtomic = new AtomicReference<>();
    private int defaultHeight = 300;
    private int defaultWidth = 300;
    private TableMeasurer<T> measurer;
    private AnnotationParser<T> annotationParser;
    protected Paint paint;
    private MatrixHelper matrixHelper;
    private boolean isExactly = true;
    private AtomicBoolean isNotifying = new AtomicBoolean(false);
    private boolean isYSequenceRight;
    private OnScrollListener scrollListener;

    private final Object lock = new Object();

    public SmartTable(Context context) {
        super(context);
        init();
    }

    public SmartTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmartTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        FontStyle.setDefaultTextSpSize(getContext(), 13);
        config = new TableConfig();
        config.dp10 = DensityUtils.dp2px(getContext(), 10);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        showRect = new Rect();
        tableRect = new Rect();
        xAxis = new XSequence<>();
        yAxis = new YSequence<>();
        parser = new TableParser<>();
        provider = new TableProvider<>();
        config.setPaint(paint);
        measurer = new TableMeasurer<>();
        tableTitle = new TableTitle();
        tableTitle.setDirection(IComponent.TOP);
        matrixHelper = new MatrixHelper(getContext());
        matrixHelper.setOnTableChangeListener(this);
        matrixHelper.register(provider);
        matrixHelper.setOnInterceptListener(provider.getOperation());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isNotifying.get()) {
            final TableData<T> tableData = tableDataAtomic.get();
            setScrollY(0);
            showRect.set(getPaddingLeft(), getPaddingTop(),
                    getWidth() - getPaddingRight(),
                    getHeight() - getPaddingBottom());
            if (tableData != null) {
                Rect rect = tableData.getTableInfo().getTableRect();
                if (rect != null) {
                    if (config.isShowTableTitle()) {
                        measurer.measureTableTitle(tableData, tableTitle, showRect);
                    }
                    tableRect.set(rect);
                    Rect scaleRect = matrixHelper.getZoomProviderRect(showRect, tableRect,
                            tableData.getTableInfo());
                    if (config.isShowTableTitle()) {
                        tableTitle.onMeasure(scaleRect, showRect, config);
                        tableTitle.onDraw(canvas, showRect, tableData.getTableName(), config);
                    }
                    drawGridBackground(canvas, showRect, scaleRect);
                    if (config.isShowYSequence()) {
                        yAxis.onMeasure(scaleRect, showRect, config);
                        if (isYSequenceRight) {
                            canvas.save();
                            canvas.translate(showRect.width(), 0);
                            yAxis.onDraw(canvas, showRect, tableData, config);
                            canvas.restore();
                        } else {
                            yAxis.onDraw(canvas, showRect, tableData, config);
                        }
                    }
                    if (config.isShowXSequence()) {
                        xAxis.onMeasure(scaleRect, showRect, config);
                        xAxis.onDraw(canvas, showRect, tableData, config);
                    }
                    if (isYSequenceRight) {
                        canvas.save();
                        canvas.translate(-yAxis.getWidth(), 0);
                        provider.onDraw(canvas, scaleRect, showRect, tableData, config);
                        canvas.restore();
                    } else {
                        provider.onDraw(canvas, scaleRect, showRect, tableData, config);
                    }
                }
            }
        }
    }

    private void drawGridBackground(Canvas canvas, Rect showRect, Rect scaleRect) {
        config.getContentGridStyle().fillPaint(paint);
        if (config.getTableGridFormat() != null) {
            config.getTableGridFormat().drawTableBorderGrid(canvas, Math.max(showRect.left, scaleRect.left),
                    Math.max(showRect.top, scaleRect.top),
                    Math.min(showRect.right, scaleRect.right),
                    Math.min(scaleRect.bottom, showRect.bottom), paint);
        }
    }

    public TableConfig getConfig() {
        return config;
    }

    public PageTableData<T> setData(List<T> data) {
        if (annotationParser == null) {
            annotationParser = new AnnotationParser<>(config.dp10);
        }
        PageTableData<T> tableData = annotationParser.parse(data);
        if (tableData != null) {
            setTableData(tableData);
        }
        return tableData;
    }


    public void setTableData(TableData<T> tableData) {
        if (tableData != null) {
            tableDataAtomic.set(tableData);
            notifyDataChanged();
        }
    }

    public void updateData(final List<T> data) {
        if (tableDataAtomic.get() == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    TableData<T> tableData = tableDataAtomic.get();
                    if (tableData == null) {
                        return;
                    }
                    tableData.setT(data);
                    parser.sort(tableData);
                    for (Column childColumn : tableData.getChildColumns()) {
                        try {
                            childColumn.fillData(tableData.getT());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    postInvalidate();
                }
            }
        }).start();
    }

    public ITableTitle getTableTitle() {
        return tableTitle;
    }

    public void notifyDataChanged() {
        if (tableDataAtomic.get() == null) {
            return;
        }
        config.setPaint(paint);

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    final TableData<T> tableData = tableDataAtomic.get();
                    if (tableData == null) {
                        return;
                    }
                    isNotifying.set(true);
                    parser.parse(tableData);
                    TableInfo info = measurer.measure(tableData, config);
                    xAxis.setHeight(info.getTopHeight());
                    yAxis.setWidth(info.getyAxisWidth());
                    requestReMeasure();
                    isNotifying.set(false);
                    postInvalidate();
                }
            }
        }).start();
    }

    public void addData(final List<T> t, final boolean isFoot) {
        if (tableDataAtomic.get() == null) {
            return;
        }
        if (t != null && t.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        final TableData<T> tableData = tableDataAtomic.get();
                        if (tableData == null) {
                            return;
                        }
                        isNotifying.set(true);
                        parser.addData(tableData, t, isFoot);
                        measurer.measure(tableData, config);
                        requestReMeasure();
                        isNotifying.set(false);
                        postInvalidate();
                    }
                }
            }).start();
        }
    }

    @Override
    public void invalidate() {
        if (!isNotifying.get()) {
            super.invalidate();
        }
    }

    private void requestReMeasure() {

        final TableData<T> tableData = tableDataAtomic.get();
        if (!isExactly && getMeasuredHeight() != 0 && tableData != null) {
            if (tableData.getTableInfo().getTableRect() != null) {
                int defaultHeight = tableData.getTableInfo().getTableRect().height()
                        + getPaddingTop();
                int defaultWidth = tableData.getTableInfo().getTableRect().width();
                int[] realSize = new int[2];
                getLocationInWindow(realSize);
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
                int screenWidth = dm.widthPixels;
                int screenHeight = dm.heightPixels;
                int maxWidth = screenWidth - realSize[0];
                int maxHeight = screenHeight - realSize[1];
                defaultHeight = Math.min(defaultHeight, maxHeight);
                defaultWidth = Math.min(defaultWidth, maxWidth);
                if (this.defaultHeight != defaultHeight
                        || this.defaultWidth != defaultWidth) {
                    this.defaultHeight = defaultHeight;
                    this.defaultWidth = defaultWidth;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            requestLayout();
                        }
                    });

                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        requestReMeasure();
    }

    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            isExactly = false;
            result = defaultWidth;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            isExactly = false;
            result = defaultHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return matrixHelper.handlerTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        matrixHelper.onDisallowInterceptEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onTableChanged(float scale, float translateX, float translateY) {
        final TableData<T> tableData = tableDataAtomic.get();
        if (tableData != null) {
            config.setZoom(scale);
            tableData.getTableInfo().setZoom(scale);
            invalidate();
        }
    }

    @Override
    public void onScrolled(float dx, float dy) {
        if (scrollListener != null) {
            scrollListener.onScrollChanged(this, dx, dy);
        }
    }

    public OnColumnClickListener getOnColumnClickListener() {
        return provider.getOnColumnClickListener();
    }

    public void setOnColumnClickListener(OnColumnClickListener onColumnClickListener) {
        this.provider.setOnColumnClickListener(onColumnClickListener);
    }

    public void setSortColumn(Column column, boolean isReverse) {
        final TableData<T> tableData = tableDataAtomic.get();
        if (tableData != null && column != null) {
            column.setReverseSort(isReverse);
            tableData.setSortColumn(column);
            setTableData(tableData);
        }
    }

    public Rect getShowRect() {
        return showRect;
    }

    public TableProvider<T> getProvider() {
        return provider;
    }

    public TableData<T> getTableData() {
        return tableDataAtomic.get();
    }

    public void setZoom(boolean zoom) {
        matrixHelper.setCanZoom(zoom);
        invalidate();
    }

    public void setZoom(boolean zoom, float maxZoom, float minZoom) {
        matrixHelper.setCanZoom(zoom);
        matrixHelper.setMinZoom(minZoom);
        matrixHelper.setMaxZoom(maxZoom);
        invalidate();
    }

    public MatrixHelper getMatrixHelper() {
        return matrixHelper;
    }

    public void setSelectFormat(ISelectFormat selectFormat) {
        this.provider.setSelectFormat(selectFormat);
    }

    @Override
    public int computeHorizontalScrollRange() {
        final int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int scrollRange = matrixHelper.getZoomRect().right;
        final int scrollX = -matrixHelper.getZoomRect().right;
        final int overScrollRight = Math.max(0, scrollRange - contentWidth);
        if (scrollX < 0) {
            scrollRange -= scrollX;
        } else if (scrollX > overScrollRight) {
            scrollRange += scrollX - overScrollRight;
        }
        return scrollRange;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (direction < 0) {
            return matrixHelper.getZoomRect().top != 0;
        } else {
            return matrixHelper.getZoomRect().bottom > matrixHelper.getOriginalRect().bottom;
        }
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return Math.max(0, -matrixHelper.getZoomRect().top);
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {

        final int contentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        int scrollRange = matrixHelper.getZoomRect().bottom;
        final int scrollY = -matrixHelper.getZoomRect().left;
        final int overScrollBottom = Math.max(0, scrollRange - contentHeight);
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overScrollBottom) {
            scrollRange += scrollY - overScrollBottom;
        }

        return scrollRange;
    }

    @Override
    public int computeVerticalScrollOffset() {

        return Math.max(0, -matrixHelper.getZoomRect().left);
    }

    @Override
    public int computeVerticalScrollExtent() {

        return super.computeVerticalScrollExtent();

    }

    public XSequence<T> getXSequence() {
        return xAxis;
    }

    public YSequence getYSequence() {
        return yAxis;
    }

    public OnScrollListener getScrollListener() {
        return scrollListener;
    }

    public void setScrollListener(OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final TableData<T> tableData = tableDataAtomic.get();
        if (tableData != null && getContext() != null) {
            if (((Activity) getContext()).isFinishing()) {
                release();
            }
        }
    }

    private void release() {
        matrixHelper.unRegisterAll();
        annotationParser = null;
        measurer = null;
        provider = null;
        matrixHelper = null;
        final TableData<T> tableData = tableDataAtomic.get();
        if (tableData != null) {
            tableData.clear();
            tableDataAtomic.set(null);
        }
        xAxis = null;
        yAxis = null;
    }

    public boolean isYSequenceRight() {
        return isYSequenceRight;
    }

    public void setYSequenceRight(boolean YSequenceRight) {
        isYSequenceRight = YSequenceRight;
    }

    public interface OnScrollListener {
        void onScrollChanged(SmartTable table, float dx, float dy);
    }

}

