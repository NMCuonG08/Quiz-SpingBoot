package com.example.Config;

import com.example.DTO.Response.ApiResponse;
import com.example.DTO.Response.PaginatedResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.example.Controller")
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply only when the converter is Jackson
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        int status = 200;
        if (response instanceof ServletServerHttpResponse) {
            status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        }

        // Error responses have an "error" key at root. Pass through if it's already
        // structured by ExceptionHandler.
        if (body instanceof Map && ((Map<?, ?>) body).containsKey("error")) {
            return body;
        }

        // Spring exception handlers deal with > 400. Let them pass untouched if not
        // matching above.
        if (status >= 400) {
            return body;
        }

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("success", true);
        output.put("statusCode", status);

        if (body instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) body;
            output.put("message", apiResponse.getMessage() != null ? apiResponse.getMessage() : "Success");
            output.put("data", apiResponse.getData());
            return output;
        }

        if (body instanceof PaginatedResponse) {
            PaginatedResponse<?> paginated = (PaginatedResponse<?>) body;
            output.put("message", paginated.getMessage() != null ? paginated.getMessage() : "Success");

            // Swap pagination into data
            Map<String, Object> dataMap = new LinkedHashMap<>();
            dataMap.put("items", paginated.getData());
            dataMap.put("pagination", paginated.getPagination());
            output.put("data", dataMap);
            return output;
        }

        output.put("message", "Success");
        output.put("data", body);
        return output;
    }
}
