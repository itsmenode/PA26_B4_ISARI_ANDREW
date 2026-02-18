public class Matrix2D {
    public static void main(String[] args) {
        long startMilli = System.currentTimeMillis();
        long startNano = System.nanoTime();

        // --- Task 1 ---

        /*Canvas example1 = new Canvas(100);
        example1.Rectangle(255, 0);
        example1.printCanvas();
        Canvas example2 = new Canvas(100);
        example2.Circle(0, 255);
        example2.printCanvas();

        // --- Task 2 ---
        /*example1.convertMatrix();
        example1.printConvert();*/

        /* An int is variable is 4 bytes which means memory spent per matrix is n^2 * 4 bytes.
         * Having m matrices with all having the same size that entails the memory spent in total is m * n^2 * 4 bytes
         * The default JVM Heap is approximately between 256 MB & 1 GB, which means getting OutOfMemoryError: Java heap space when n is approximately 5000 and 10000*/


        //-Xms4G    Sets initial heap size to 4 GB
        //-Xmx4G    Sets maximum heap size to 4 GB

        int size = Integer.parseInt(args[0]);
        String shape = args[1];

        System.out.println(size + "\n\n\n\n\n");

        Canvas newShape = new Canvas(size);

        if (shape.equals("circle")) {
            newShape.Circle();
            newShape.printCanvas();
        }
        else if (shape.equals("rectangle")) {
            newShape.Rectangle();
            newShape.printCanvas();
        }
        else {
            System.out.println("[Error] unable to process the given shape. [Error]");
        }

        long finishMilli = System.currentTimeMillis();
        long timeElapsedMilli = finishMilli - startMilli;

        long finishNano = System.nanoTime();
        long timeElapsedNano = finishNano - startNano;

        System.out.println("Run-time in milliseconds: " + timeElapsedMilli);
        System.out.println("Run-time in nanoseconds: " + timeElapsedNano);


    }
}
