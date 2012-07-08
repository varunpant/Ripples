package com.example.model;

public class Ripple {

    public static int height;

    public static int width;


    public static final int PERTURBATION = 200;
    int[][] oldState;
    int[][] swapPointer;
    int[][] currentState;

    private static int[] texture;

    public Ripple(int[] t) {
        this.oldState = new int[Ripple.width][Ripple.height];
        this.currentState = new int[Ripple.width][Ripple.height];
        texture = t.clone();
    }

    public void drawOffScreen(int[] offScreenRaster) {
        for (int y = 1; y < (Ripple.height - 1); y++) {
            for (int x = 1; x < (Ripple.width - 1); x++) {
                this.currentState[x][y] = ((
                        this.oldState[(x - 1)][y] +
                                this.oldState[(x + 1)][y] +
                                this.oldState[x][(y + 1)] +
                                this.oldState[x][(y - 1)] >> 1) - this.currentState[x][y]);
                this.currentState[x][y] -= (this.currentState[x][y] >> 7);

                int data = (short) (1024 - this.currentState[x][y]);

                int a = (x - Ripple.width) * data / 1024 + Ripple.width;
                int b = (y - Ripple.height) * data / 1024 + Ripple.height;

                if (a >= Ripple.width) a = (Ripple.width - 1);
                if (a < 0) a = 0;
                if (b >= Ripple.height) b = (Ripple.height - 1);
                if (b < 0) b = 0;

                int textOffset = x + y * Ripple.width;
                offScreenRaster[textOffset] = texture[(a + b * Ripple.width)];
            }
        }

        this.swapPointer = this.currentState;
        this.currentState = this.oldState;
        this.oldState = this.swapPointer;
    }

    public static void addRipple(int[][] pointer, int x, int y) {
        if ((x > (Ripple.height - 1)) || (x < 1) || (y > (Ripple.width - 1)) || (y < 1)) {
            return;
        }

        try {
            pointer[x][y] += 200;

            pointer[x][(y - 1)] += 200;
            pointer[x][(y + 1)] += 200;

            pointer[(x + 1)][y] += 200;
            pointer[(x + 1)][(y + 1)] += 200;
            pointer[(x + 1)][(y - 1)] += 200;

            pointer[(x - 1)][(y + 1)] += 200;
            pointer[(x - 1)][y] += 200;
            pointer[(x - 1)][(y - 1)] += 200;

            pointer[(x - 2)][(y + 2)] += 200;
            pointer[(x - 2)][(y + 1)] += 200;
            pointer[(x - 2)][y] += 200;
            pointer[(x - 2)][(y - 1)] += 200;
            pointer[(x - 2)][(y - 2)] += 200;

            pointer[(x + 2)][(y + 2)] += 200;
            pointer[(x + 2)][(y + 1)] += 200;
            pointer[(x + 2)][y] += 200;
            pointer[(x + 2)][(y - 1)] += 200;
            pointer[(x + 2)][(y - 2)] += 200;

            pointer[(x + 3)][(y + 3)] += 200;
            pointer[(x + 3)][(y + 2)] += 200;
            pointer[(x + 3)][(y + 1)] += 200;
            pointer[(x + 3)][y] += 200;
            pointer[(x + 3)][(y - 1)] += 200;
            pointer[(x + 3)][(y - 2)] += 200;
            pointer[(x + 3)][(y - 3)] += 200;
        } catch (Exception localException) {
        }
    }

    public void handleActionDown(int eventX, int eventY) {
        addRipple(this.currentState, eventX, eventY);
    }

}
