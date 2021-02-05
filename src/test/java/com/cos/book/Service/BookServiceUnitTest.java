package com.cos.book.Service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cos.book.domain.BookRepository;
import com.cos.book.service.BookService;

/*
 단위테스트(서비스 관련 Bean만 IOC에 띄우면 됨)
 BoardRepository => 가짜 객체로 만들 수 있음.
 
 */

@ExtendWith(MockitoExtension.class)//환경을 따로 만듬
public class BookServiceUnitTest {
	
	@InjectMocks//BookService 객체가 만들어질 때 BookServiceUnitTest(해당 파일)에 @Mock로 등록된 모든 애들을 주입받는다.
	private BookService bookService;
	@Mock
	private BookRepository bookRepository;
	
	
	
	
	
}
