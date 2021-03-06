package co.rsk.cli.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MigratorTest {

    @Test
    public void migrateConfiguration() {
        Reader initialConfiguration = new StringReader("inline.config.name=\"value\"\n" +
                "nested {\n" +
                "  nested = {\n" +
                "    #test comment\n" +
                "    config = 13\n" +
                "  }\n" +
                "}\n" +
                "another.config=\"don't change\"");
        Properties migrationProperties = new Properties();
        migrationProperties.put("inline.config.name", "inline.config.new.name");
        migrationProperties.put("nested.nested.config", "nested.nested.new.config");
        migrationProperties.put("unknown.config", "none");

        String migratedConfiguration = Migrator.migrateConfiguration(initialConfiguration, migrationProperties);
        Config config = ConfigFactory.parseString(migratedConfiguration);
        assertThat(config.hasPath("inline.config.name"), is(false));
        assertThat(config.getString("inline.config.new.name"), is("value"));
        assertThat(config.hasPath("nested.nested.config"), is(false));
        assertThat(config.getInt("nested.nested.new.config"), is(13));
        assertThat(config.hasPath("unknown.config"), is(false));
        assertThat(config.getString("another.config"), is("don't change"));
    }
}