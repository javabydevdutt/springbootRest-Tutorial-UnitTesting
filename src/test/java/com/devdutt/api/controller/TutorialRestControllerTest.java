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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    //------------------------------------------------------------------------------------------------------------------------------//
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

    //------------------------------------------------------------------------------------------------------------------------------//

    @Test
    public void shouldReturnTutorial() throws Exception {
        long id = 1L;
        Tutorial tutorial = new Tutorial(id, "Springboot", "Description-1", true);
        when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
        mockMvc.perform(get("/apis/tutorials/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(tutorial.getTitle()))
                .andExpect(jsonPath("$.description").value(tutorial.getDescription()))
                .andExpect(jsonPath("$.published").value(tutorial.isPublished())).andDo(print());
    }

    @Test
    public void shouldReturnNotFoundTutorial() throws Exception {
        long id = 1L;
        when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/apis/tutorials/{id}", id)).andExpect(status().isNotFound()).andDo(print());
    }

    //------------------------------------------------------------------------------------------------------------------------------//

    @Test
    public void shouldUpdateTutorial() throws Exception {
        long id = 1L;
        Tutorial tutorial = new Tutorial(id, "Springboot", "Description-1", false);
        Tutorial updateTutorial = new Tutorial(id, "updated", "updated", true);

        when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updateTutorial);

        mockMvc.perform(put("/apis/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateTutorial))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTutorial.getTitle()))
                .andExpect(jsonPath("$.description").value(updateTutorial.getDescription()))
                .andExpect(jsonPath("$.published").value(updateTutorial.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundUpdateTutorial() throws Exception {
        long id = 1L;
        Tutorial updatedtutorial = new Tutorial(id, "Updated", "Updated", true);
        when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updatedtutorial);

        mockMvc.perform(put("/apis/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedtutorial))).andExpect(status().isNotFound()).andDo(print());
    }

    //------------------------------------------------------------------------------------------------------------------------------//
    @Test
    public void shouldDeleteTutorials() throws Exception {
        long id = 1L;
        doNothing().when(tutorialRepository).deleteById(id);
        mockMvc.perform(delete("/apis/tutorials/{id}", id)).andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    public void shouldDeleteAllTutorials() throws Exception {
        doNothing().when(tutorialRepository).deleteAll();
        mockMvc.perform(delete("/apis/tutorials/")).andExpect(status().isNoContent()).andDo(print());
    }
}
