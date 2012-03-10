package net.cattaka.slim3.sutame.service;

import net.cattaka.slim3.sutame.service.ImageService;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ImageServiceTest extends AppEngineTestCase {

    private ImageService service = new ImageService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
