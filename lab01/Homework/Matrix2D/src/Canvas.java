public class Canvas {
    private int size;
    private int[][] matrix;
    private String[][] matrixUnicode;

    public Canvas(int size) {
        this.size = size;
        this.matrix = new int[this.size][this.size];
        this.matrixUnicode = new String[this.size][this.size];
    }

    public void setBackground(int selectedColor) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = selectedColor;
            }
        }
    }

    public void printCanvas() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j] == 255 ? "X" : " ");
            }
            System.out.print("\n");
        }
        System.out.println("\n\n\n\n");
    }

    public void Rectangle(int backgroundColor, int shapeColor) {
        setBackground(backgroundColor);
        for (int i = 0; i < size; i++) {
            matrix[0][i] = shapeColor;
            matrix[size - 1][i] = shapeColor;
        }
        for (int i = 1; i < size - 1; i++) {
            matrix[i][0] = shapeColor;
            matrix[i][size - 1] = shapeColor;
        }
    }

    public void Rectangle() {
        Rectangle(0, 255);
    }

    public void Circle(int backgroundColor, int shapeColor) {
        setBackground(backgroundColor);

        double center = (size - 1) / 2.0;
        double radius = (size - 1) / 2.0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double distance = Math.sqrt(Math.pow(i - center, 2) + Math.pow(j - center, 2));

                if (Math.abs(distance - radius) < 0.6) {
                    matrix[i][j] = shapeColor;
                }
            }
        }
    }

    public void Circle() {
        Circle(0, 255);
    }

    public int minimization(int number) {
        while (number > 10) {
            int sum = 0;
            while (number != 0) {
                sum = sum + number % 10;
                number = number / 10;
            }
            number = sum;
        }

        return number;
    }

    public char toUnicode(int code) {
        return (char) code;
    }

    public void convertMatrix(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int code = minimization(matrix[i][j]) + 0x2580;
                matrixUnicode[i][j] = String.valueOf(toUnicode(code));
            }
        }
    }

    public void printConvert() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrixUnicode[i][j]);
            }
            System.out.print("\n");
        }
        System.out.println("\n\n\n\n");
    }
}
