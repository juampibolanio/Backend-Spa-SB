import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("lucas123");
        System.out.println(hash);
    }
}


//PROFESIONALES
//1234
//lucas123
//marce123
//pilar23
//diana123
