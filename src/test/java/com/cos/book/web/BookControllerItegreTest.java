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

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
	
	@BeforeEach //각각의 단위 테스트 전에 초기화할 함수를 적어준다. 
	public void init() {
		//entityManager.persist(new Book());
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
		//@Transactional 으로 DB의 increment 값까지 초기화가 안 되므로, null로 집어넣을 시 내가 예상한 id값과 다를 수 있음. 그래서 따로 초기화
		//매 시작마다 increment 값을 초기화시켜준다. DB마다 쿼리 문법이 다르므로 주의!    
		
//테스트 시마다 값을 집어넣는 게 귀찮으면 여기서 각각의 테스트가 실행되면서 자동으로 집어넣게끔 설정해주고 
//@AfterEach 로 테스트가 끝나면 삭제해주면 된다. 대신 테스트 로직이 꼬일 수 있으므로 난 안씀
//		List<Book> books = new ArrayList<>();       
//		books.add(new Book(null,"스프링부트 따라하기","코스"));
//		books.add(new Book(null,"리액트 따라하기","코스"));			
//		books.add(new Book(null,"JUnit 따라하기","코스"));			
//		bookRepository.saveAll(books);		
	}
	
	
	@AfterEach 
	public void end() {
		//bookRepository.deleteAll();
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
			
			List<Book> books = new ArrayList<>();
			books.add(new Book(null,"스프링부트 따라하기","코스"));
			books.add(new Book(null,"리액트 따라하기","코스"));			
			books.add(new Book(null,"JUnit 따라하기","코스"));			
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
			books.add(new Book(null,"스프링부트 따라하기","코스"));
			books.add(new Book(null,"리액트 따라하기","코스"));			
			books.add(new Book(null,"JUnit 따라하기","코스"));			
			bookRepository.saveAll(books);

			Book book = new Book(null, "c++ 따라하기", "코스");
			String content = new ObjectMapper().writeValueAsString(book); // json으로 변환시켜줌


			// when
			ResultActions resultAction = mockMvc.perform(put("/book/{id}", id)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(content) //내가 업데이트할 데이터  
					.accept(MediaType.APPLICATION_JSON_UTF8));

			// then
			resultAction.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("c++ 따라하기"))
			.andExpect(jsonPath("$.id").value(3L))			
					.andDo(MockMvcResultHandlers.print());
		}
		

		
		@Test
		public void delete_테스트() throws Exception {
			// given(테스트를 하기 위한 준비)
			Long id = 1L;
			List<Book> books = new ArrayList<>();
			books.add(new Book(null,"스프링부트 따라하기","코스"));	
			bookRepository.saveAll(books);

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
