package com.batch.config;

import java.io.IOException;

import com.batch.dto.AreaBasedListResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class ItemsDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

	private JavaType targetType;

	public ItemsDeserializer() {
	}

	public ItemsDeserializer(JavaType targetType) {
		this.targetType = targetType;
	}

	@Override
	public AreaBasedListResponse.Items deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.readValueAsTree();

		if(node.isTextual() && node.textValue().isEmpty()) {
			return null;
		}

		ObjectMapper mapper = (ObjectMapper) p.getCodec();
		return mapper.treeToValue(node, targetType);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		if(property != null) {
			JavaType type = property.getType();
			return new ItemsDeserializer(type);
		}
		return this;
	}
}
