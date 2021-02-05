package com.cos.book.domain;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 적어야 스프링 IOC에 빈으로 등록이 되는데...!!!
// JPARepository 를 extends 하면 생략가능 
// JPARepository는 기본적인 CRUD 함수도 같이 제공해줌
public interface BookRepository extends JpaRepository<Book, Long> {

}
