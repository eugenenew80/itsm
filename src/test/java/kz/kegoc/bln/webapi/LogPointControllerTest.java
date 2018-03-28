package kz.kegoc.bln.webapi;

import kz.kegoc.bln.TestUtil;
import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.web.logPoint.LogPointRestController;
import kz.kegoc.bln.web.dto.LogPointDto;
import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LogPointRestController.class)
public class LogPointControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LogPointRepo logPointRepo;

    @MockBean
    private DozerBeanMapper mapper;

    @Test
    public void whenGetLogPointsThenReturnJsonArray() throws Exception {
        LogPoint logPoint = new LogPoint();
        logPoint.setId(1l);
        logPoint.setName("Точка 1");
        List<LogPoint> logPoints = Arrays.asList(logPoint);

        given(logPointRepo.findAll()).willReturn(logPoints);
        given(mapper.map(logPoint, LogPointDto.class)).willAnswer(new Answer<LogPointDto>() {
            public LogPointDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                LogPointDto logPointDto = new LogPointDto();
                logPointDto.setId(logPoint.getId());
                logPointDto.setName(logPoint.getName());
                return logPointDto;
            }
        });

        mvc.perform(get("/logPoints")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(logPoint.getName())));
    }


    @Test
    public void whenPostLogPointWhenCreateAndReturnJson() throws Exception {
        LogPoint logPoint = new LogPoint();
        LogPointDto logPointDto = new LogPointDto();
        logPointDto.setName("Точка 1");

        given(logPointRepo.save(logPoint)).willAnswer((Answer<LogPoint>) invocationOnMock -> {
            logPoint.setId(1l);
            return logPoint;
        });

        given(mapper.map(logPointDto, LogPoint.class)).willAnswer((Answer<LogPoint>) invocationOnMock -> {
            logPoint.setId(logPointDto.getId());
            logPoint.setName(logPointDto.getName());
            return logPoint;
        });

        given(mapper.map(logPoint, LogPointDto.class)).willAnswer((Answer<LogPointDto>) invocationOnMock -> {
            logPointDto.setId(logPoint.getId());
            logPointDto.setName(logPoint.getName());
            return logPointDto;
        });

        mvc.perform(post("/logPoints")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(logPointDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is(logPointDto.getName())));
    }
}
