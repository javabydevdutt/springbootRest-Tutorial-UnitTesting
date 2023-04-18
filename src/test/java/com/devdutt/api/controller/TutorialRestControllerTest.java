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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    public void shouldReturnListOfTutorials() throws Exception {
        List<Tutorial> tutorials = new ArrayList<>(
                Arrays.asList(new Tutorial(1, "Springboot", "Description-1", true),
                        new Tutorial(2, "Springboot", "Description-2", true),
                        new Tutorial(3, "Springboot", "Description-3", false)
                ));
        when(tutorialRepository.findAll()).thenReturn(tutorials);
        mockMvc.perform(get("/apis/tutorials")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(tutorials.size())).andDo(print());
    }//method

    @Test
    public void shouldReturnListOfTutorialsWithFilter() throws Exception {
        List<Tutorial> tutorials = new ArrayList<>(
                Arrays.asList(new Tutorial(1, "Springboot", "Description-1", true),
                        new Tutorial(2, "Springboot", "Description-2", true),
                        new Tutorial(3, "Springboot", "Description-3", false)
                ));
        String title = "Springboot";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);

        when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
        mockMvc.perform(get("/apis/tutorials").params(paramsMap)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tutorials.size())).andDo(print());
    }

    @Test
    public void shouldReturnNoContentWhenFilter() throws Exception {
        String title = "BezKoder";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);
        List<Tutorial> tutorials = Collections.emptyList();

        when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
        mockMvc.perform(get("/apis/tutorials").params(paramsMap)).andExpect(status().isNoContent()).andDo(print());
    }
}
