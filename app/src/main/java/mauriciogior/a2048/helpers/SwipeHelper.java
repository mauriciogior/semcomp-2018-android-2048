/*
 * Copyright (c) 2018 $name.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mauriciogior.a2048.helpers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeHelper {
    private GestureDetector gestureDetector;

    public void onTouch(MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
    }

    public SwipeHelper(Context context, final SwipeListener swipeListener) {
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x = e2.getX() - e1.getX();
                float y = e2.getY() - e1.getY();

                if (swipeListener == null) return false;

                // Horizontal movement
                if (Math.abs(x) > Math.abs(y)) {
                    if (Math.abs(x) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (x > 0) swipeListener.swipeRight();
                        else swipeListener.swipeLeft();
                    }
                } else {
                    if (Math.abs(y) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (y > 0) swipeListener.swipeDown();
                        else swipeListener.swipeUp();
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public static interface SwipeListener {
        void swipeUp();
        void swipeDown();
        void swipeLeft();
        void swipeRight();
    }
}
