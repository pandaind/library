package com.library.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.library.apigateway.config.TestGrpcClientConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-web-test.properties")
@Import(TestGrpcClientConfig.class)
public class SimpleGraphQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSimpleGraphQLQuery() throws Exception {
        String simpleQuery = """
            {
                "query": "{ testQuery }"
            }
            """;
            
        MvcResult result = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(simpleQuery))
                .andExpect(status().isOk())
                .andReturn();
                
        String content = result.getResponse().getContentAsString();
        System.out.println("Simple GraphQL test response: " + content);
        System.out.println("Response headers: " + result.getResponse().getHeaderNames());
        System.out.println("Content-Type: " + result.getResponse().getContentType());
    }
}