package tactics;

import java.lang.reflect.Field;

class Person {
    private String name;
    private int age;
    private String address;

    public Person(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

}


public class Demo {
    public static void main(String[] args) {
        // 创建一个Person对象
        Person person = new Person("Alice", 30, "123 Main St");

        // 获取Person类的Class对象
        Class<?> personClass = person.getClass();

        // 获取所有字段（包括私有字段）
        Field[] fields = personClass.getDeclaredFields();

        try {
            for (Field field : fields) {
                // 设置字段可访问（因为字段可能是私有的）
                field.setAccessible(true);

                // 获取字段名和字段值
                String fieldName = field.getName();
                Object fieldValue = field.get(person);

                // 打印字段名和字段值
                System.out.println(fieldName + ": " + fieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}