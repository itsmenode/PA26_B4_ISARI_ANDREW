import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class BoundaryScanner {
    public static void main(String[] args) {
        // Input Example: java BoundaryScanner shape.txt
        if (args.length == 0) {
            System.out.println("Usage: java BoundaryScanner <filePath>");
            return;
        }
        String filePath = args[0];

        int top = -1, bottom = -1, left = -1, right = -1;

        try (FileReader fileReader = new FileReader(filePath); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                int j = 0;
                for (char car : line.toCharArray()) {
                    if (car == 'X') {
                        if (top == -1) {
                            top = i;
                        }
                        if (bottom < i) {
                            bottom = i;
                        }
                        if (left == -1 || left > j) {
                            left = j;
                        }
                        if (right == -1 || right < j) {
                            right = j;
                        }
                    }
                    j = j + 1;
                }
                i = i + 1;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        if (top == -1) {
            System.out.println("No shape found in the file.");
            return;
        }

        System.out.println("The border will be given using coordinates where X is the column and Y is the line\n");
        System.out.println("Top Left -> X: " + left + " Y: " + top + "\n");
        System.out.println("Top Right -> X: " + right + " Y: " + top + "\n");
        System.out.println("Bottom Left -> X: " + left + " Y: " + bottom + "\n");
        System.out.println("Bottom Right -> X: " + right + " Y: " + bottom + "\n");
    }
}