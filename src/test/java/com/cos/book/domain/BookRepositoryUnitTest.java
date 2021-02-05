package com.cos.book.domain;

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
	
}
