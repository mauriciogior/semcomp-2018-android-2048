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

package mauriciogior.a2048;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mauriciogior.a2048.helpers.SwipeHelper;

public class GameActivity extends Activity implements SwipeHelper.SwipeListener {

    private GridLayout mGridLayout;
    private TextView mTextScore;
    private int grid[][] = new int[4][4];
    private SwipeHelper swipeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mGridLayout = (GridLayout) findViewById(R.id.gridLayout);
        mTextScore = (TextView) findViewById(R.id.textScore);
        swipeHelper = new SwipeHelper(this, this);

        resetGrid();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeHelper.onTouch(event);
        return super.onTouchEvent(event);
    }

    private void updateScore() {
        int score = 0;
        for (int row=0; row<grid.length; row++) {
            for (int col=0; col<grid[row].length; col++) {
                score += grid[row][col];
            }
        }
        mTextScore.setText("" + score);
    }

    private List<Pair<Integer, Integer>> getOpenSpaces() {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();

        for (int row=0; row<grid.length; row++) {
            for (int col=0; col<grid[row].length; col++) {
                if (grid[row][col] == 0) {
                    pairs.add(new Pair<Integer, Integer>(row, col));
                }
            }
        }

        return pairs;
    }

    private boolean hasOpenSpaces() {
        return getOpenSpaces().size() > 0;
    }

    private Pair<Integer, Integer> findOpenSpace() {
        if (!hasOpenSpaces()) return null;
        List<Pair<Integer, Integer>> spaces = getOpenSpaces();
        int which = (int) (Math.random() * spaces.size());

        return spaces.get(which);
    }

    private void addRandomBlockToGrid() {
        Pair<Integer, Integer> space = findOpenSpace();
        if (space == null) {
            gameOver();
            return;
        }
        int row = space.first;
        int col = space.second;
        int n = (int) (Math.random() * 4) == 3 ? 4 : 2;

        grid[row][col] = n;
    }

    private void gameOver() {
        resetGrid();
    }

    private void resetGrid() {
        grid = new int[4][4];
        // Adds two random blocks
        addRandomBlockToGrid();
        addRandomBlockToGrid();

        drawGrid();
    }

    private int getColorForPosition(int n) {
        if (n == 0) n = 1;
        return Color.argb((int) (Math.log(n * 2) / Math.log(4096) * 100), 200, 200, 0);
    }

    private TextView buildBlock(int row, int col, int n) {
        TextView block = new TextView(this);

        // We create a layout params for GridLayout
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row, 1, 1f);
        params.columnSpec = GridLayout.spec(col, 1, 1f);
        params.width = 0;
        params.height = 0;
        params.setGravity(Gravity.FILL);

        params.setMargins(12, 12, 12, 12);
        block.setLayoutParams(params);
        block.setGravity(Gravity.CENTER);
        block.setBackgroundColor(getColorForPosition(n));
        block.setTextSize(24);

        if (n > 0) block.setText("" + n);

        return block;
    }

    private void drawGrid() {
        mGridLayout.removeAllViews();

        // Iterate over all positions in the grid
        for (int row=0; row<grid.length; row++) {
            for (int col=0; col<grid[row].length; col++) {
                mGridLayout.addView(buildBlock(row, col, grid[row][col]));
            }
        }

        updateScore();
        if (!canMove()) {
            gameOver();
        }
    }

    private boolean canMove() {
        for (int row=0; row<grid.length; row++) {
            for (int col=0; col<grid[row].length; col++) {
                int n = grid[row][col];
                if (row > 0) {
                    if (grid[row-1][col] == 0 || grid[row-1][col] == n) return true;
                }
                if (col > 0) {
                    if (grid[row][col-1] == 0 || grid[row][col-1] == n) return true;
                }
                if (row < grid.length - 2) {
                    if (grid[row+1][col] == 0 || grid[row+1][col] == n) return true;
                }
                if (col < grid.length - 2) {
                    if (grid[row][col+1] == 0 || grid[row][col+1] == n) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void swipeUp() {
        boolean moved = false;
        int[][] sums = new int[4][4];

        for (int col=0; col<grid.length; col++) {
            for (int loop=0; loop<4; loop++) {
                int n = grid[0][col];
                for (int row = 1; row < grid.length; row++) {
                    int m = grid[row][col];
                    if (m > 0) {
                        // If upper row is empty, go up
                        if (n == 0) {
                            grid[row - 1][col] = m;
                            grid[row][col] = 0;
                            moved = true;
                        } else if (n == m && sums[row][col] != 1) {
                            grid[row - 1][col] = n * 2;
                            grid[row][col] = 0;
                            sums[row - 1][col] = 1;
                            moved = true;
                        }
                    }

                    // Sets new n
                    n = m;
                }
            }
        }

        if (moved) addRandomBlockToGrid();
        drawGrid();
    }

    @Override
    public void swipeDown() {
        boolean moved = false;
        int[][] sums = new int[4][4];

        for (int col=0; col<grid.length; col++) {
            for (int loop=0; loop<4; loop++) {
                int n = grid[3][col];
                for (int row = grid.length - 2; row >= 0; row--) {
                    int m = grid[row][col];
                    if (m > 0) {
                        // If upper row is empty, go up
                        if (n == 0) {
                            grid[row + 1][col] = m;
                            grid[row][col] = 0;
                            moved = true;
                        } else if (n == m && sums[row][col] != 1) {
                            grid[row + 1][col] = n * 2;
                            grid[row][col] = 0;
                            sums[row + 1][col] = 1;
                            moved = true;
                        }
                    }

                    // Sets new n
                    n = m;
                }
            }
        }

        if (moved) addRandomBlockToGrid();
        drawGrid();
    }

    @Override
    public void swipeLeft() {
        boolean moved = false;
        int[][] sums = new int[4][4];

        for (int row=0; row<grid.length; row++) {
            for (int loop=0; loop<4; loop++) {
                int n = grid[row][0];
                for (int col = 1; col < grid.length; col++) {
                    int m = grid[row][col];
                    if (m > 0) {
                        // If upper row is empty, go up
                        if (n == 0) {
                            grid[row][col - 1] = m;
                            grid[row][col] = 0;
                            moved = true;
                        } else if (n == m && sums[row][col] != 1) {
                            grid[row][col - 1] = n * 2;
                            grid[row][col] = 0;
                            sums[row][col - 1] = 1;
                            moved = true;
                        }
                    }

                    // Sets new n
                    n = m;
                }
            }
        }

        if (moved) addRandomBlockToGrid();
        drawGrid();
    }

    @Override
    public void swipeRight() {
        boolean moved = false;
        int[][] sums = new int[4][4];

        for (int row=0; row<grid.length; row++) {
            for (int loop=0; loop<4; loop++) {
                int n = grid[row][3];
                for (int col = grid.length - 2; col >= 0; col--) {
                    int m = grid[row][col];
                    if (m > 0) {
                        // If upper row is empty, go up
                        if (n == 0) {
                            grid[row][col + 1] = m;
                            grid[row][col] = 0;
                            moved = true;
                        } else if (n == m && sums[row][col] != 1) {
                            grid[row][col + 1] = n * 2;
                            grid[row][col] = 0;
                            sums[row][col + 1] = 1;
                            moved = true;
                        }
                    }

                    // Sets new n
                    n = m;
                }
            }
        }

        if (moved) addRandomBlockToGrid();
        drawGrid();
    }
}
