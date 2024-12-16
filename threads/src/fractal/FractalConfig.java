package fractal;

public class FractalConfig {

    public static final int WIDTH = 3840; // 4K
    public static final int HEIGHT = 2160; // 4K
    public static final int MAX_ITERATIONS = 2_000;
    public static final double ZOOM = 800;
    public static final double X_OFFSET = -0.74364388703; // Centrado na área de interesse ("Seahorse Valley")
    public static final double Y_OFFSET = 0.13182590421;
    public static final String OUTPUT_DIRECTORY = "fractals/";
    public static final String IMAGE_NAME = "mandelbrot";

    public static final int[] COLOR_PALETTE = generateColorPalette();

    private static int[] generateColorPalette() {
        int[] palette = new int[256];
        for (int i = 0; i < 256; i++) {
            int r = (int) (Math.sin(0.024 * i + 0) * 127 + 128);
            int g = (int) (Math.sin(0.024 * i + 2) * 127 + 128);
            int b = (int) (Math.sin(0.024 * i + 4) * 127 + 128);
            palette[i] = (r << 16) | (g << 8) | b;
        }
        return palette;
    }
}
