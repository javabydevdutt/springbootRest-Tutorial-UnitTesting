package com.devdutt.api.controller;

import com.devdutt.api.model.Tutorial;
import com.devdutt.api.repo.TutorialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TutorialRestController.class)
public class TutorialRestControllerTest {

    @MockBean
    private TutorialRepository tutorialRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateTutorial() throws Exception {
        Tutorial tutorial = new Tutorial(1, "Spring Boot", "Description", true);

        mockMvc.perform(post("/apis/tutorials").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial))).andExpect(status().isCreated()).andDo(print());
    }
}
