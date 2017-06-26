package io.swagger;

import com.google.gson.*;
import entities.PriorityLevel;
import logic.SolverNRP;

import java.lang.reflect.Type;

/**
 * Created by kredes on 12/06/2017.
 */
public class ReplanGson {
    private static Gson gson;

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

        JsonDeserializer<PriorityLevel> priorityDeserializer = new JsonDeserializer<PriorityLevel>() {
            @Override
            public PriorityLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                int level = json.getAsJsonObject().get("level").getAsInt();
                return PriorityLevel.fromValues(level, level);
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
