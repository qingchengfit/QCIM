package com.tencent.qcloud.timchat.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by fb on 2017/4/26.
 */

public class ScrollLinearLayoutManager extends LinearLayoutManager{
        private float MILLISECONDS_PER_INCH = 0.03f;
        private Context contxt;

        public ScrollLinearLayoutManager(Context context) {
            super(context);
            this.contxt = context;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return ScrollLinearLayoutManager.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }


        public void setSpeedSlow() {
            //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
            MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.06f;
        }

        public void setSpeedFast() {
            MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.03f;
        }
}
