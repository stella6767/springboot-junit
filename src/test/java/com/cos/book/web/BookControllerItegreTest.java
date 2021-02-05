package com.cos.book.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

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
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookControllerItegreTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach
	public void init() {
		//entityManager.persist(new Book());
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();//매 시작마다 increment 값을 초기화시켜준다.
		//DB마다 쿼리 문법이 다르므로 주의!
	}
	
	
	
	//BDDMockito 패턴 given, when, then
		@Test
		public void save_테스트() throws Exception {
			log.info("save_테스트() 시작 ==========================================================");
			// given(테스트를 하기 위한 준비)
			Book book = new Book(null,"스프링 따라하기","코스");
			String content = new ObjectMapper().writeValueAsString(book);
			log.info(content);
			//when(bookService.저장하기(book)).thenReturn(new Book(1L, "스프링 따라하기", "코스")); //스텁이 필요없다.
			
			
			//when(테스트 실행)
			ResultActions resultAction = mockMvc.perform(post("/book")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(content)
					.accept(MediaType.APPLICATION_JSON_UTF8));
			
			//then(검증)
			resultAction
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("스프링 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	
		}
		
		
		@Test
		public void findAll_테스트() throws Exception{
			//given
			List<Book> books = new ArrayList<>();
			books.add(new Book(null,"스프링부트 따라하기","코스"));
			books.add(new Book(null,"리액트 따라하기","코스"));			
			books.add(new Book(null,"JUnit 따라하기","코스"));			
			bookRepository.saveAll(books);
			
			//when
			ResultActions resultAction = mockMvc.perform(get("/book")
					.accept(MediaType.APPLICATION_JSON_UTF8));
			
			//then
			resultAction
			    .andExpect(status().isOk())
			    .andExpect(jsonPath("$",Matchers.hasSize(3) ))
			    .andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
			    .andDo(MockMvcResultHandlers.print());	
		}
		
		

		@Test
		public void findById_테스트() throws Exception {
			// given
			Long id = 1L;

			// when
			ResultActions resultAction = mockMvc.perform(get("/book/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

			// then
			resultAction.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("자바 공부하기"))
					.andDo(MockMvcResultHandlers.print());

		}

		
		
		
		
		
		
}
