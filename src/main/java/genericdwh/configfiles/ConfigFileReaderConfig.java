package genericdwh.configfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigFileReaderConfig {
	@Bean
	public ConfigFileReader configFileReader() {
		return new ConfigFileReader("");
	}
}
