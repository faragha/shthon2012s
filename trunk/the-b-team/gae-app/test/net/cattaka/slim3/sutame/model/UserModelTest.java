package net.cattaka.slim3.sutame.model;

import net.cattaka.slim3.sutame.model.UserModel;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class UserModelTest extends AppEngineTestCase {

    private UserModel model = new UserModel();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
