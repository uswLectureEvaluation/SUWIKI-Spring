//package usw.suwiki.loginTest;
//
//import org.assertj.core.api.Assert;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import usw.suwiki.config.BcryptPasswordEncoder;
//
//@Test
//
//public class loginTest {
//
//    @Autowired
//    private final BcryptPasswordEncoder passwordEncoder;
//    String input = "hello world";
//    String encoded = passwordEncoder.encode(input);
//
//    // true
//        Assert.assertTrue(passwordEncoder.matches(input, encoded));
//
//    // false
//        Assert.assertEquals(passwordEncoder.encode(input), passwordEncoder.encode(input));
//}
