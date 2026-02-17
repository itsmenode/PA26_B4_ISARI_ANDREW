public class Main {
    public static void main() {
        Canvas example1 = new Canvas(20);
        example1.Rectangle(255, 0);
        example1.printCanvas();
        Canvas example2 = new Canvas(20);
        example2.Circle(0, 255);
        example2.printCanvas();
        example1.convertMatrix();
        example1.printConvert();
    }
}
