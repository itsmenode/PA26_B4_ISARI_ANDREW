void main() {

    //--- Step 1 ---
    IO.println("Hello World!\n\n");

    //--- Step 2 ---
    String[] languages = {"C", "C++", "C#", "Python", "Go", "Rust", "JavaScript", "PHP", "Swift", "Java"};

    //--- Step 3 ---
    int n = (int) (Math.random() * 1_000_000);
    n *= 3;

    String temp;
    temp = "10101";
    n += Integer.parseInt(temp, 2);

    temp = "FF";
    n += Integer.parseInt(temp, 16);

    n *= 6;
    IO.println(n + "\n\n");

    //--- Step 4 ---
    int result = n;
    while (result > 10) {
        int sum = 0;
        while (result != 0) {
            sum = sum + result % 10;
            result = result / 10;
        }
        result = sum;
    }

    IO.println(result + "\n\n");

    //--- Step 5 ---
    IO.println("Willy-nilly, this semester I will learn " + languages[result]);


}
