package org.robinbird.util;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.robinbird.exception.RobinbirdException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonObjectMapper {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    public static <T> T readValue(@NonNull final String jsonString, @NonNull final TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (final IOException e) {
            throw new RobinbirdException(Msgs.get(Msgs.Key.JSON_PROCESSING_ISSUE, jsonString), e);
        }
    }

    public static <T> String writeValueAsString(@NonNull final T value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            throw new RobinbirdException(Msgs.get(Msgs.Key.JSON_PROCESSING_ISSUE, value.toString()), e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(UUID.class, new UUIDDeserializer());

        mapper.registerModule(simpleModule);
        mapper.registerModule(new JodaModule());

        mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(dateFormat);

        return mapper;
    }
}
