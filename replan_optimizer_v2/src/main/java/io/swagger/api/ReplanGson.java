package io.swagger.api;

import com.google.gson.*;
import entities.PriorityLevel;
import logic.SolverNRP;

import java.lang.reflect.Type;

/**
 * Initializes a Gson instance customized for JSON (de)serialization of all the necessary Replan entities.
 * See <a href="https://github.com/google/gson">Gson documentation</a> for further details on custom (de)serializers.
 *
 * @author kredes
 */
public class ReplanGson {

    private static final Gson gson;

    static {
        JsonSerializer<PriorityLevel> prioritySerializer = new JsonSerializer<PriorityLevel>() {
            @Override
            public JsonElement serialize(PriorityLevel src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject priority = new JsonObject();
                priority.add("level", new JsonPrimitive(src.getLevel()));
                priority.add("score", new JsonPrimitive(src.getScore()));

                return priority;
            }
        };

        /*
            We accept two different JSON representations for a priority:
                1. {"level": int, "score": int}
                2. a single int representing the level

            "score" is ignored anyway as the scores are defined in the Priority enum and cannot be changed
         */
        JsonDeserializer<PriorityLevel> priorityDeserializer = new JsonDeserializer<PriorityLevel>() {
            @Override
            public PriorityLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json instanceof JsonObject) {
                    int level = json.getAsJsonObject().get("level").getAsInt();
                    return PriorityLevel.fromValues(level, level);
                } else {
                    int level = json.getAsInt();
                    return PriorityLevel.getPriorityByLevel(level);
                }
            }
        };

        JsonDeserializer<SolverNRP.AlgorithmType> algorithmTypeDeserializer = new JsonDeserializer<SolverNRP.AlgorithmType>() {
            @Override
            public SolverNRP.AlgorithmType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return SolverNRP.AlgorithmType.fromName(json.getAsString());
            }
        };


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PriorityLevel.class, prioritySerializer);
        gsonBuilder.registerTypeAdapter(PriorityLevel.class, priorityDeserializer);
        gsonBuilder.registerTypeAdapter(SolverNRP.AlgorithmType.class, algorithmTypeDeserializer);

        gson = gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }
}
