package net.rollanddeath.smp.core.scripting.lint;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class ResourceScriptLintTest {

    private static final String[] RESOURCE_YAMLS = {
        "modifiers.yml",
        "items.yml",
        "mobs.yml",
        "scripts.yml",
    };

    @Test
    void resourcesHaveValidScripts() {
        Yaml yaml = new Yaml();
        List<ScriptLintIssue> allIssues = new ArrayList<>();

        for (String name : RESOURCE_YAMLS) {
            Object root = loadYamlResource(yaml, name);
            if (root == null) {
                fail("No se pudo cargar el resource YAML en tests: " + name);
            }

            allIssues.addAll(ScriptLinter.lintObject(name, root));
        }

        List<ScriptLintIssue> errors = allIssues.stream()
            .filter(i -> i.severity() == ScriptLintSeverity.ERROR)
            .toList();

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("ScriptLinter encontró errores en resources:\n");
            for (ScriptLintIssue i : errors) {
                sb.append("- [").append(i.source()).append("] ")
                    .append(i.path()).append(": ")
                    .append(i.message());
                if (i.token() != null) sb.append(" (token=").append(i.token()).append(")");
                if (i.suggestion() != null) sb.append(" (suggestion=").append(i.suggestion()).append(")");
                sb.append('\n');
            }
            fail(sb.toString());
        }
    }

    private static Object loadYamlResource(Yaml yaml, String resourceName) {
        ClassLoader cl = ResourceScriptLintTest.class.getClassLoader();
        try (InputStream in = cl.getResourceAsStream(resourceName)) {
            if (in == null) return null;
            return yaml.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
