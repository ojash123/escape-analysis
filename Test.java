// Test.java
public class Test {
    private String name;

    public Test(String name) {
        this.name = name;
    }
    public void testMethod(int x) {
    if (x > 0) {
        System.out.println("Positive");
    } else {
        System.out.println("Non-positive");
    }
    return;
}
}