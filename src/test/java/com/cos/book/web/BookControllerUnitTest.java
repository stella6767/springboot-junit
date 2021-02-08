package com.cos.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.cos.book.domain.Book;
import com.cos.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//단위 테스트(Controller 관련로직만 띄우기) Filter, ControllerAdvice 등등

@Slf4j
@WebMvcTest // Controller 관련 로직만 테스트한다고 Junit에게 알리는 역할
public class BookControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean // loc 환경에 bean 등록됨
	private BookService bookService;

	// BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception { // 그냥 함수를 실행하면 오류가 뜬다. 왜냐, 컨트롤러는 서비스가 메모리에 떠야 작동이 되는데
		log.info("save_테스트() 시작 =========================================================="); // 현재 서비스가 없기 때문에 그래서
																								// @MockBean으로 가짜서비스 객체를
																								// 만들어줌
		// given(테스트를 하기 위한 준비)
		Book book = new Book(null, "스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book); // json으로 변환시켜줌
		log.info(content);
		when(bookService.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스")); // 가짜로 결과값을 집어넣어줌, 스텁
		// 컨트롤러 관련로직만 테스트하기 때문에 실제 DB와 아무 상관이 없이 그냥 로직만 테스트

		// when(테스트 실행)
		ResultActions resultAction = mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		// then(검증)
		resultAction.andExpect(status().isCreated())
		.andExpect(jsonPath("$.title")
				.value("스프링 따라하기"))// 나는 이 결과를 기대한다.만약 틀리면 에런 																										
		.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void findAll_테스트() throws Exception {
		// given
		List<Book> books = new ArrayList<>();
		books.add(new Book(1L, "스프링부트 따라하기", "코스"));
		books.add(new Book(2L, "리액트 따라하기", "코스"));
		when(bookService.모두가져오기()).thenReturn(books);

		// when
		ResultActions resultAction = mockMvc.perform(get("/book").accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk())
		.andExpect(jsonPath("$", Matchers.hasSize(2)))
				.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void findById_테스트() throws Exception {
		// given
		Long id = 1L;
		when(bookService.한건가져오기(id)).thenReturn(new Book(1L, "자바 공부하기", "쌀"));

		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("자바 공부하기"))
				.andDo(MockMvcResultHandlers.print());

	}
	
	

	@Test
	public void update_테스트() throws Exception {
		// given(테스트를 하기 위한 준비)

		Long id = 1L;
		Book book = new Book(null, "c++ 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(book); // json으로 변환시켜줌

		when(bookService.수정하기(id, book)).thenReturn(new Book(1L, "c++ 따라하기", "코스"));

		// when
		ResultActions resultAction = mockMvc.perform(put("/book/{id}", id)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("c++ 따라하기"))
				.andDo(MockMvcResultHandlers.print());
	}
	
	
	
	@Test
	public void delete_테스트() throws Exception {
		// given(테스트를 하기 위한 준비)
		Long id = 1L;

		when(bookService.삭제하기(id)).thenReturn("ok");

		// when
		ResultActions resultAction = mockMvc.perform(delete("/book/{id}", id));

		// then
		resultAction.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print());

		MvcResult requestResult = resultAction.andReturn();
		String result = requestResult.getResponse().getContentAsString();
		
		assertEquals("ok", result);
	}

}
