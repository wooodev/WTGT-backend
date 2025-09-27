package com.swyp.catsgotogedog;

import com.swyp.catsgotogedog.content.repository.ContentElasticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CatsgotogedogApplicationTests {

	@MockitoBean(reset = MockReset.AFTER)
	ContentElasticRepository contentElasticRepository;


	@Test
	void contextLoads() {
	}

}
