package org.robinbird;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonProcessingExceptionForTest extends JsonProcessingException {

    public JsonProcessingExceptionForTest() {
        super("test");
    }

    public JsonProcessingExceptionForTest(String msg) {
        super(msg);
    }

}
