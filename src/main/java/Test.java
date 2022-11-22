//import java.sql.Connection;
//import java.sql.DriverManager;
//
//public class Test {
//    public static void main(String[] args) {
//        Connection c = null;
//        try {
//            Class.forName("org.postgresql.Driver");
//            c = DriverManager.getConnection("jdbc:postgresql://10.16.4.246:5432/cs209_a2",
//                    "postgres", "123456");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.exit(0);
//        }
//        System.out.println("Opened database successfully");
//    }
//}