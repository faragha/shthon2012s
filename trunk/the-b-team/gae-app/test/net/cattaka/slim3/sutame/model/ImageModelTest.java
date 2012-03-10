package net.cattaka.slim3.sutame.model;

import net.cattaka.slim3.sutame.model.ImageModel;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ImageModelTest extends AppEngineTestCase {

    private ImageModel model = new ImageModel();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
