package io.jenkins.tools.pluginmodernizer.core.extractor;

import io.jenkins.tools.pluginmodernizer.core.model.JDK;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.openrewrite.groovy.GroovyIsoVisitor;
import org.openrewrite.groovy.tree.G;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor for a Jenkinsfile
 */
public class JenkinsfileVisitor extends GroovyIsoVisitor<PluginMetadata> {

    /**
     * LOGGER.
     */
    public static final Logger LOG = LoggerFactory.getLogger(JenkinsfileVisitor.class);

    private final Map<String, Object> variableMap = new HashMap<>();

    @Override
    public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations v, PluginMetadata pluginMetadata) {
        LOG.debug("Visiting variable declarations {}", v);
        J.VariableDeclarations variableDeclarations = super.visitVariableDeclarations(v, pluginMetadata);

        for (J.VariableDeclarations.NamedVariable variable : variableDeclarations.getVariables()) {
            variableMap.put(variable.getSimpleName(), variable.getInitializer());
        }

        return variableDeclarations;
    }

    @Override
    public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, PluginMetadata pluginMetadata) {
        LOG.debug("Visiting method invocation {}", method);
        method = super.visitMethodInvocation(method, pluginMetadata);
        if ("buildPlugin".equals(method.getSimpleName())) {
            List<Expression> args = method.getArguments();

            List<Integer> jdkVersions =
                    args.stream().flatMap(this::extractJdkVersions).distinct().toList();

            LOG.info("JDK versions found: {}", jdkVersions);

            jdkVersions.forEach(jdkVersion -> pluginMetadata.addJdk(JDK.get(jdkVersion)));
        }

        return method;
    }

    private Stream<Integer> extractJdkVersions(Expression arg) {
        if (arg instanceof G.MapEntry) {
            return Stream.of(arg)
                    .map(G.MapEntry.class::cast)
                    .filter(entry -> "configurations".equals(((J.Literal) entry.getKey()).getValue()))
                    .map(entry -> resolveConfigurations(entry.getValue()))
                    .filter(value -> value instanceof G.ListLiteral)
                    .flatMap(value -> ((G.ListLiteral) value).getElements().stream())
                    .filter(expression -> expression instanceof G.MapLiteral)
                    .flatMap(expression -> ((G.MapLiteral) expression).getElements().stream())
                    .filter(mapExpr -> mapExpr instanceof G.MapEntry)
                    .map(G.MapEntry.class::cast)
                    .filter(mapEntry -> "jdk".equals(((J.Literal) mapEntry.getKey()).getValue()))
                    .map(mapEntry -> Integer.parseInt(
                            ((J.Literal) mapEntry.getValue()).getValue().toString()));
        } else {
            Expression resolvedArg = resolveVariable(arg);
            return Stream.of(resolvedArg)
                    .filter(resolved -> resolved instanceof G.MapLiteral)
                    .flatMap(resolved -> ((G.MapLiteral) resolved).getElements().stream())
                    .filter(entry -> entry instanceof G.MapEntry)
                    .map(G.MapEntry.class::cast)
                    .filter(entry -> "configurations".equals(((J.Literal) entry.getKey()).getValue()))
                    .map(entry -> resolveConfigurations(entry.getValue()))
                    .filter(value -> value instanceof G.ListLiteral)
                    .flatMap(value -> ((G.ListLiteral) value).getElements().stream())
                    .filter(expression -> expression instanceof G.MapLiteral)
                    .flatMap(expression -> ((G.MapLiteral) expression).getElements().stream())
                    .filter(mapExpr -> mapExpr instanceof G.MapEntry)
                    .map(G.MapEntry.class::cast)
                    .filter(mapEntry -> "jdk".equals(((J.Literal) mapEntry.getKey()).getValue()))
                    .map(mapEntry -> Integer.parseInt(
                            ((J.Literal) mapEntry.getValue()).getValue().toString()));
        }
    }

    private Expression resolveVariable(Expression expression) {
        if (expression instanceof J.Identifier) {
            String variableName = ((J.Identifier) expression).getSimpleName();
            if (variableMap.containsKey(variableName)) {
                return (Expression) variableMap.get(variableName);
            }
        }
        return expression;
    }

    private Expression resolveConfigurations(Expression entry) {
        return entry instanceof G.ListLiteral ? entry : resolveVariable(entry);
    }
}
