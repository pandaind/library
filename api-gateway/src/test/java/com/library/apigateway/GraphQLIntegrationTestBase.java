package com.library.apigateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.apigateway.config.TestGrpcClientConfig;
import com.library.bookservice.grpc.BookServiceGrpc;
import com.library.userservice.grpc.UserServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
    classes = {ApiGatewayApplication.class})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-web-test.properties")
@Import(TestGrpcClientConfig.class)
public abstract class GraphQLIntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BookServiceGrpc.BookServiceBlockingStub bookServiceBlockingStub;

    @Autowired
    protected UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    
    // Aliases for backward compatibility
    protected BookServiceGrpc.BookServiceBlockingStub bookServiceStub;
    protected UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    protected static final String GRAPHQL_ENDPOINT = "/graphql";

    @BeforeEach
    void setUp() {
        // Set up aliases for backward compatibility
        bookServiceStub = bookServiceBlockingStub;
        userServiceStub = userServiceBlockingStub;
        // Reset mocks before each test
        reset(bookServiceStub, userServiceStub);
    }

    protected MvcResult executeGraphQLQuery(String query) throws Exception {
        return executeGraphQLQuery(query, null);
    }

    protected MvcResult executeGraphQLQuery(String query, Map<String, Object> variables) throws Exception {
        GraphQLRequest request = new GraphQLRequest();
        request.setQuery(query);
        request.setVariables(variables);

        String requestBody = objectMapper.writeValueAsString(request);

        return mockMvc.perform(post(GRAPHQL_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    protected static class GraphQLRequest {
        private String query;
        private Map<String, Object> variables;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Map<String, Object> variables() {
            return variables;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }
    }

    protected String extractData(MvcResult result, String path) throws Exception {
        String content = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(content, Map.class);
        
        String[] pathParts = path.split("\\.");
        Object current = response;
        
        for (String part : pathParts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }
        
        return current != null ? current.toString() : null;
    }

    protected boolean hasErrors(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        if (content == null || content.trim().isEmpty()) {
            return true; // Empty response indicates an error
        }
        try {
            Map<String, Object> response = objectMapper.readValue(content, Map.class);
            return response.containsKey("errors");
        } catch (Exception e) {
            return true; // JSON parsing error indicates an error
        }
    }
}