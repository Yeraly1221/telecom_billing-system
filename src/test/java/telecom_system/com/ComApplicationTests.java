package telecom_system.com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ComApplicationTests {

	Calculator underTest = new Calculator();

	@Test
	void contextLoads() {
		//given
		int numberOne = 20;
		int numberTwo = 30;

		//when
		int result = underTest.add(numberOne, numberTwo);

		//then
		assertThat(result).isEqualTo(50);
	}


	class Calculator {
		int add(int a, int b){
			return a + b;
		}
	}

}
