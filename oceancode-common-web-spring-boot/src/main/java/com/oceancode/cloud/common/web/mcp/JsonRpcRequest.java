package com.oceancode.cloud.common.web.mcp;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonRpcRequest(String jsonrpc, JsonNode id, String method, JsonNode params) {
}
