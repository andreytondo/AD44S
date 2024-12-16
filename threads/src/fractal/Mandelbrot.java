package fractal;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class Mandelbrot {

    public static void main(String[] args) {
        try {
            MandelbrotFractal fractal = new MandelbrotFractal();
            benchmark(fractal);
        } catch (IOException e) {
            System.err.println("Error during Mandelbrot execution: " + e.getMessage());
        }
    }

    private static void benchmark(MandelbrotFractal fractal) throws IOException {
        int[] threadCounts = {1, 2, 4, 6, 8, 16};
        double[] times = new double[threadCounts.length];

        System.out.println("Aquecendo a JVM...");
        warmUp(fractal);

        for (int i = 0; i < threadCounts.length; i++) {
            int threads = threadCounts[i];
            System.out.printf("Executando com %d thread(s)...%n", threads);
            times[i] = benchmarkStrategy(threads, fractal);
        }

        showSpeedUps(times[0], times);
    }

    private static void warmUp(MandelbrotFractal fractal) {
        for (int i = 0; i < 10; i++) {
            fractal.generate(1);
        }
    }

    private static void showSpeedUps(double baseTime, double[] times) {
        for (int i = 0; i < times.length; i++) {
            System.out.printf("Speed-up %d threads: %.2f%n", i + 1, baseTime / times[i]);
        }
    }

    private static double benchmarkStrategy(int threads, MandelbrotFractal fractal) throws IOException {
        long startTime = System.nanoTime();

        fractal.generate(threads);

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedSeconds = elapsedTime / 1_000_000_000.0;

        System.out.printf("Tempo para %d threads: %.2fs%n", threads, elapsedSeconds);

        // Salva a imagem sem medir o tempo da geração
        saveImage(fractal.getImage(), FractalConfig.IMAGE_NAME + "_" + threads);

        return elapsedSeconds;
    }

    private static void saveImage(RenderedImage image, String filename) throws IOException {
        File outputDir = new File(FractalConfig.OUTPUT_DIRECTORY);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + FractalConfig.OUTPUT_DIRECTORY);
        }

        File outputFile = new File(outputDir, filename + ".png");
        ImageIO.write(image, "png", outputFile);
    }
}