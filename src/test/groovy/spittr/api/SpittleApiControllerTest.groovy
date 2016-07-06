package spittr.api

import com.google.gson.Gson
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spittr.Spittle
import spittr.data.SpittleRepository
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by Mac on 2016. 7. 7..
 */
class SpittleApiControllerTest extends Specification {
    MockMvc mockMvc
    SpittleRepository spittleRepository

    void setup() {
        def controller = new SpittleApiController()
        spittleRepository = Mock()
        controller.spittleRepository = spittleRepository
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    def "정한 최대수만큼 가져와 결과를 확인한다."() {
        when:
        def response = mockMvc.perform(get("/spittles"))

        then:
        1 * spittleRepository.findSpittles(9223372036854775807L, 20)
        response.andExpect(status().isOk())
    }

    def "특정리소스를 가져와 결과를 확인한다"() {
        given:
        def id = 1L

        when:
        def response = mockMvc.perform(get("/spittles/" + id))

        then:
        1 * spittleRepository.findOne(id)
        response.andExpect(status().isOk())
    }

    def "새로운 리소스를 생성하고 결과를 확인한다."() {
        given:
        def spittle = new Spittle(1L, "test", new Date(), 0.0, 0.0)
        def gson = new Gson()
        def json = gson.toJson(spittle, Spittle.class)

        spittleRepository.save(spittle) >> spittle

        when:
        def response = mockMvc.perform(post("/spittles")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andReturn()
        then:
        response.getResponse().getHeaderValue(Location) == "/spittles/1"
    }
}