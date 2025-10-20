package com.surest.member_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceApplicationTests {

    @Test
    void shouldStartMemberServiceApplicationSuccessfully() {
        assertDoesNotThrow(() -> MemberServiceApplication.main(new String[]{}));
    }

	@Test
	void contextLoads() {
	}

}
