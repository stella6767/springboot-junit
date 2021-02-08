package com.cos.book.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

//단위테스트(DB 관련 Bean이 IOC에 등록되면 됨)
//replace = Replace.ANY = 가짜 데이터베이스 띄워서 테스트, Replace.None은 실제 DB로 테스트

@Transactional
@AutoConfigureTestDatabase(replace = Replace.ANY)
@DataJpaTest // Repository들을 다 IOC 등록해둠.
public class BookRepositoryUnitTest {
	
	@Autowired
	private BookRepository bookRepository;
	
	@Test
	public void save_테스트() {
		//BDDMocikto 방식
		//given		
		Book book = new Book(null,"책제목1","책저자1");
		//when
		Book bookEntity = bookRepository.save(book);		
		//then
		assertEquals("책제목1", bookEntity.getTitle());  //왼쪽인자가 내가 기대하는 값, 오른쪽인자가 실제 값 = 둘이 동일하면 true 리턴(테스트 성공!)		
	}
	
}
