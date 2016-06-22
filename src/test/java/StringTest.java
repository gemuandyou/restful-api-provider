/**
 * Created by Gemu on 2/26/2016 0026.
 */
public class StringTest {

    public static void changeStr(String a) {
        a += ".........";
    }

    public static void changePerson(Person p) {
        p.setName(p.getName() + "......");
    }

    public static void main(String[] args) {
        String a = "abc";
        System.out.println(a);
        changeStr(a);
        System.out.println(a);

        Person p = new Person("Gemu");
        System.out.println(p);
        changePerson(p);
        System.out.println(p);
    }
}

class Person {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }
}
