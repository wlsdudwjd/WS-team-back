package com.example.itsme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.itsme.config.TestFirebaseConfig.class)
class ItsmeApplicationTests {

	@Test
	void contextLoads() {
	}

}
