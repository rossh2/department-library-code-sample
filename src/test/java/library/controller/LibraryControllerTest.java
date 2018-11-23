package library.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LibraryControllerTest {

    private final String PREFIX = "/library";

    private final String davidHareBookJson = "{" +
            "\"author\": \"David Hare\"," +
            "\"title\": \"Algorithmics — The Spirit of Computing\"," +
            "\"isbn\": 9783642272653" +
            "}";

    @Autowired
    private MockMvc mvc;

    @Test
    public void searchByAuthor() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(PREFIX + "/searchByAuthor?authorName=Thomas H Cormen")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", equalTo("Thomas H Cormen")));
    }

    @Test
    public void searchByIsbn() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(PREFIX + "/searchByISBN?isbn=9781133187790")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", equalTo(9781133187790L)));
    }

    @Test
    public void borrowBook() throws Exception {
        performBorrow()
                .andExpect(status().isOk());

        // Clean up
        performReturn();
    }

    @Test
    public void borrowBook_whenAlreadyBorrowed() throws Exception {
        performBorrow()
                .andExpect(status().isOk());

        performBorrow()
                .andExpect(status().isBadRequest());

        // Clean up
        performReturn();
    }

    @Test
    public void returnBook() throws Exception {
        performBorrow()
                .andExpect(status().isOk());

        performReturn()
                .andExpect(status().isOk());
    }

    @Test
    public void returnBook_whenNotBorrowed() throws Exception {
        performReturn()
                .andExpect(status().isBadRequest());
    }

    private ResultActions performBorrow() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(PREFIX + "/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(davidHareBookJson));
    }

    private ResultActions performReturn() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(PREFIX + "/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(davidHareBookJson));
    }
}