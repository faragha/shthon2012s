package net.cattaka.slim3.sutame.service;

import net.cattaka.slim3.sutame.service.UserService;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class UserServiceTest extends AppEngineTestCase {

    private UserService service = new UserService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
