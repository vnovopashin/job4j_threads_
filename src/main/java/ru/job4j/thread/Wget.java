package ru.job4j.thread;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        validateUrl(url);
        validateSpeed(speed);
        this.url = url;
        this.speed = speed;
    }

    private static void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https"))) {
                throw new IllegalArgumentException("URL must use http or https scheme: " + url);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    private static void validateSpeed(int speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be positive, but was: " + speed);
        }
    }

    @Override
    public void run() {
        try {
            download();
        } catch (IOException e) {
            System.err.println("Download failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void download() throws IOException {
        URL downloadUrl = URI.create(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("Server returned HTTP " + responseCode
                    + " for URL " + url);
        }

        long fileSize = connection.getContentLengthLong();
        String fileName = resolveFileName();

        System.out.printf("Downloading: %s%n", url);
        System.out.printf("Saving to:   %s%n", fileName);
        System.out.printf("Speed limit: %s%n", formatBytes(speed) + "/s");
        if (fileSize > 0) {
            System.out.printf("File size:   %s%n%n", formatBytes(fileSize));
        } else {
            System.out.println("File size:   unknown");
        }

        long startTime = System.currentTimeMillis();

        try (InputStream input = new BufferedInputStream(connection.getInputStream());
             OutputStream output = new BufferedOutputStream(new FileOutputStream(fileName))) {

            byte[] buffer = new byte[8192];
            long bytesReadTotal = 0;
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                bytesReadTotal += bytesRead;

                throttle(bytesReadTotal, startTime);
                printProgress(bytesReadTotal, fileSize);
            }

            System.out.println();
            long elapsed = System.currentTimeMillis() - startTime;
            double avgSpeed = elapsed > 0 ? bytesReadTotal * 1000.0 / elapsed : 0;
            System.out.printf("Done: %s downloaded in %.2f s (avg %s/s)%n",
                    formatBytes(bytesReadTotal),
                    elapsed / 1000.0,
                    formatBytes((long) avgSpeed));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println();
            System.err.println("Download interrupted");
        } finally {
            connection.disconnect();
        }
    }

    private void throttle(long bytesReadTotal, long startTime) throws InterruptedException {
        long expectedTime = (bytesReadTotal * 1000L) / speed;
        long actualTime = System.currentTimeMillis() - startTime;
        long sleepTime = expectedTime - actualTime;
        if (sleepTime > 0) {
            Thread.sleep(sleepTime);
        }
    }

    private void printProgress(long bytesRead, long totalSize) {
        if (totalSize > 0) {
            int percent = (int) (bytesRead * 100 / totalSize);
            String bar = buildBar(percent, 30);
            System.out.printf("\r[%s] %3d%%  %s / %s",
                    bar, percent, formatBytes(bytesRead), formatBytes(totalSize));
        } else {
            System.out.printf("\rDownloaded: %s", formatBytes(bytesRead));
        }
    }

    private static String buildBar(int percent, int width) {
        int filled = width * percent / 100;
        StringBuilder sb = new StringBuilder(width);
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '=' : ' ');
        }
        return sb.toString();
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double value = bytes;
        String[] units = {"KB", "MB", "GB", "TB"};
        for (String unit : units) {
            value /= 1024;
            if (value < 1024) {
                return String.format("%.1f %s", value, unit);
            }
        }
        return String.format("%.1f PB", value);
    }

    private String resolveFileName() {
        String path = URI.create(url).getPath();
        if (path == null || path.isEmpty() || path.endsWith("/")) {
            return "downloaded_file";
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name.isEmpty() ? "downloaded_file" : name;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.err.println("Usage: java ru.job4j.thread.Wget <url> <speed_bytes_per_sec>");
            System.err.println("Example: java ru.job4j.thread.Wget https://example.com/file.zip 1048576");
            return;
        }

        String url = args[0];
        int speed;
        try {
            speed = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Speed must be an integer: " + args[1]);
            return;
        }

        Wget wget;
        try {
            wget = new Wget(url, speed);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
            return;
        }

        Thread thread = new Thread(wget);
        thread.start();
        thread.join();
    }
}
