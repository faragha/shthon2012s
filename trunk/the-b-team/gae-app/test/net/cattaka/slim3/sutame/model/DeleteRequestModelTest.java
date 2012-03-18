package net.cattaka.slim3.sutame.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DeleteRequestModelTest extends AppEngineTestCase {

    private DeleteRequestModel model = new DeleteRequestModel();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
