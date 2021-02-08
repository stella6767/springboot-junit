package com.cos.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;




import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.cos.book.domain.Book;
import com.cos.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
Integre-통합 테스트(모든 Bean들을 똑같이 Ioc 올리고 테스트 하는 것)
WebEnvironment.MOCK = 실제 톰캣을 올리는 게 아니라, 다른 톰캣으로 테스트, MOCK가 붙으면 다 가짜
WebEnvironment.RANDOM_PORT = 실제 톰캣을 올려서 테스트
@AutoConfigureMockMvc = MockMvc를 Ioc에 등록해줌
@Transactional = 각각의 테스트함수가 종료될 때마다 트랜잭션을 rollback 해주는 어노테이션!! 데이터를 독립적으로 테스트 
*/
@Slf4j
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerItegreTest {

	private MockMvc mockMvc;

	protected RestDocumentationResultHandler document;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private EntityManager entityManager;

	@BeforeEach // 각각의 단위 테스트 전에 초기화할 함수를 적어준다.
	private void setup(WebApplicationContext webApplicationContext,
			RestDocumentationContextProvider restDocumentation) {
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();

		this.document = document("{class-name}/{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
				.apply(documentationConfiguration(restDocumentation)).alwaysDo(document).build();
	}

	@AfterEach
	public void end() {
		// bookRepository.deleteAll();
	}

	// BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception {
		log.info("save_테스트() 시작 ==========================================================");
		// given(테스트를 하기 위한 준비)
		Book book = new Book(null, "스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book);
		log.info(content);
		// when(bookService.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스"));
		// //스텁이 필요없다.

		// when(테스트 실행)
		ResultActions resultAction = mockMvc.perform(post("/book").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		// then(검증)
		resultAction.andExpect(status().isCreated())
		.andExpect(jsonPath("$.title")
				.value("스프링 따라하기"))
				.andDo(MockMvcResultHandlers.print())
				.andDo(document);

	}

	@Test
	public void findAll_테스트() throws Exception {
		// given
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "스프링부트 따라하기", "코스"));
		books.add(new Book(null, "리액트 따라하기", "코스"));
		books.add(new Book(null, "JUnit 따라하기", "코스"));
		bookRepository.saveAll(books);

		// when
		ResultActions resultAction = mockMvc.perform(get("/book").accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(3)))
				.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기")).andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void findById_테스트() throws Exception {
		// given
		Long id = 1L;

		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "스프링부트 따라하기", "코스"));
		books.add(new Book(null, "리액트 따라하기", "코스"));
		books.add(new Book(null, "JUnit 따라하기", "코스"));
		bookRepository.saveAll(books);

		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("스프링부트 따라하기"))
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void update_테스트() throws Exception {
		// given(테스트를 하기 위한 준비)

		Long id = 3L;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "스프링부트 따라하기", "코스"));
		books.add(new Book(null, "리액트 따라하기", "코스"));
		books.add(new Book(null, "JUnit 따라하기", "코스"));
		bookRepository.saveAll(books);

		Book book = new Book(null, "c++ 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book); // json으로 변환시켜줌

		// when
		ResultActions resultAction = mockMvc
				.perform(put("/book/{id}", id).contentType(MediaType.APPLICATION_JSON_UTF8).content(content) // 내가 업데이트할
																												// 데이터
						.accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("c++ 따라하기"))
				.andExpect(jsonPath("$.id").value(3L)).andDo(MockMvcResultHandlers.print())
				.andDo(document.document(
						//pathParameters(parameterWithName("id").description("id")),
						
						requestFields(fieldWithPath("id").description("번호"),
							       fieldWithPath("title").description("제목"),
		                           fieldWithPath("author").description("저자")   							
								),
						
                      responseFields(
                              fieldWithPath("id").description("번호"),
                              fieldWithPath("title").description("제목"),
                              fieldWithPath("author").description("저자")                                                                       
                      )
              ));
	}

	@Test
	public void delete_테스트() throws Exception {
		// given(테스트를 하기 위한 준비)
		Long id = 1L;
		List<Book> books = new ArrayList<>();
		books.add(new Book(null, "스프링부트 따라하기", "코스"));
		bookRepository.saveAll(books);

		// when
		ResultActions resultAction = mockMvc.perform(delete("/book/{id}", id));

		// then
		resultAction.andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

		MvcResult requestResult = resultAction.andReturn();
		String result = requestResult.getResponse().getContentAsString();

		assertEquals("ok", result);
	}

}
