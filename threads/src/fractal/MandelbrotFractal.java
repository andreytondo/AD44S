package fractal;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class MandelbrotFractal {

    private static final double BAILOUT_RADIUS = 4.0; // Valor arbitrário para determinar se um ponto pertence ao conjunto de Mandelbrot
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int maxIterations;
    private final double zoom;
    private final double xOffset;
    private final double yOffset;

    public MandelbrotFractal() {
        this(FractalConfig.WIDTH,
                FractalConfig.HEIGHT,
                FractalConfig.MAX_ITERATIONS,
                FractalConfig.ZOOM,
                FractalConfig.X_OFFSET,
                FractalConfig.Y_OFFSET);
    }

    public MandelbrotFractal(int width, int height, int maxIterations,
                             double zoom, double xOffset, double yOffset) {
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;
        this.zoom = zoom;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Computa a cor de um pixel na imagem do fractal de Mandelbrot.
     */
    private int computeMandelbrot(int x, int y) {
        double zx = 0, zy = 0;
        double cX = (x - width / 2.0) / zoom + xOffset; // Mapeia o pixel para a região de interesse
        double cY = (y - height / 2.0) / zoom + yOffset; // Mapeia o pixel para a região de interesse

        int iteration = maxIterations;

        // Testa se o ponto pertence ao conjunto de Mandelbrot
        double zx2 = zx * zx;
        double zy2 = zy * zy;
        while (zx2 + zy2 < BAILOUT_RADIUS && iteration > 0) {
            zy = 2.0 * zx * zy + cY;
            zx = zx2 - zy2 + cX;
            zx2 = zx * zx;
            zy2 = zy * zy;
            iteration--;
        }

        // Calcula a cor do pixel
        if (iteration > 0) {
            double smoothColor = iteration - Math.log(Math.log(zx * zx + zy * zy)) / Math.log(2);
            int index = Math.min(255, Math.max(0, (int) smoothColor % 256));
            return FractalConfig.COLOR_PALETTE[index];
        }

        return 0;
    }

    private void compute(int x, int y) {
        image.setRGB(x, y, computeMandelbrot(x, y));
    }

    /**
     * Gera a imagem do fractal de Mandelbrot de forma paralela.
     */
    public void generate(int threads) {
        try (ForkJoinPool pool = new ForkJoinPool(threads)) {
            pool.submit(() -> IntStream.range(0, height).parallel().forEach(y -> {
                for (int x = 0; x < width; x++) {
                    compute(x, y);
                }
            })).join();
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}