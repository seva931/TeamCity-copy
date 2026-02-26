package jupiter.extension;

import api.models.Agent;
import api.models.AgentsResponse;
import api.requests.steps.AgentSteps;
import configs.Config;
import jupiter.annotation.WithAgent;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class AgentExtension implements
        ExecutionCondition,
        ParameterResolver,
        BeforeEachCallback,
        AfterEachCallback {

    private static final ReentrantLock AGENT_LOCK = new ReentrantLock(true);
    ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(AgentExtension.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Not a test method context");
        }

        WithAgent anno = AnnotationSupport
                .findAnnotation(context.getRequiredTestMethod(), WithAgent.class)
                .orElse(null);

        if (anno == null) {
            return ConditionEvaluationResult.enabled("@WithAgent not present");
        }

        AgentsResponse allAgents = AgentSteps.getAllAgents();

        String[] configKeys = anno.configKeys();
        String[] requiredNames = new String[configKeys.length];

        try {
            for (int i = 0; i < configKeys.length; i++) {
                requiredNames[i] = resolveAgentNameFromConfig(configKeys[i]);
            }
        } catch (ExtensionConfigurationException e) {
            return ConditionEvaluationResult.disabled(e.getMessage());
        }

        Set<String> availableNames = allAgents.getAgent().stream()
                .map(Agent::getName)
                .collect(Collectors.toSet());

        List<String> missing = Arrays.stream(requiredNames)
                .filter(name -> !availableNames.contains(name))
                .toList();

        if (!missing.isEmpty()) {
            return ConditionEvaluationResult.disabled(
                    "Не найдены требуемые агенты: " + missing +
                            ". Доступные: " + availableNames
            );
        }

        return ConditionEvaluationResult.enabled("Требуемые агенты доступны");
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithAgent.class).isEmpty()) {
            return;
        }
        AGENT_LOCK.lock();
        context.getStore(NAMESPACE).put(context.getUniqueId() + ":locked", true);

        try {
            WithAgent anno = AnnotationSupport
                    .findAnnotation(context.getRequiredTestMethod(), WithAgent.class)
                    .orElseThrow(() -> new ParameterResolutionException("@WithAgent is required"));

            String[] configKeys = anno.configKeys();
            String[] agentNames = new String[configKeys.length];
            for (int i = 0; i < configKeys.length; i++) {
                agentNames[i] = resolveAgentNameFromConfig(configKeys[i]);
            }

            AgentsResponse allAgents = AgentSteps.getAllAgents();
            Agent[] agents = new Agent[agentNames.length];

            for (int i = 0; i < agentNames.length; i++) {
                agents[i] = findByName(allAgents, agentNames[i]);
                AgentSteps.authorizeAgent(agents[i].getId());
                AgentSteps.enableAgent(agents[i].getId());
            }

            context.getStore(NAMESPACE).put(context.getUniqueId(), agents);
        } catch (Exception e) {
            AGENT_LOCK.unlock(); // важно, чтобы не повис lock при ошибке в beforeEach
            context.getStore(NAMESPACE).remove(context.getUniqueId() + ":locked");
            throw e;
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Agent[] agents = context.getStore(NAMESPACE).get(context.getUniqueId(), Agent[].class);

        if (agents != null) {
            for (Agent a : agents) {
                long agentId = a.getId();

                AgentSteps.enableAgent(agentId);
                AgentSteps.authorizeAgent(agentId);
            }
        }
        Boolean isLocked = context.getStore(NAMESPACE).get(context.getUniqueId() + ":locked", Boolean.class);
        if (Boolean.TRUE.equals(isLocked)) {
            AGENT_LOCK.unlock();
        }
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        Agent[] agents = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Agent[].class);
        if (agents == null) {
            throw new ParameterResolutionException("Agents are not initialized in beforeEach");
        }
        return agents;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), WithAgent.class).isPresent()
                && parameterContext.getParameter().getType().equals(Agent[].class);
    }

    private String resolveAgentNameFromConfig(String configKey) {
        String value = Config.getProperty(configKey);
        if (value == null || value.isBlank()) {
            throw new ExtensionConfigurationException(
                    "Не задано имя агента в config: key='" + configKey + "'");
        }
        return value.trim();
    }

    private static Agent findByName(AgentsResponse response, String expectedName) {
        String name = expectedName.trim();

        return response.getAgent().stream()
                .filter(a -> name.equals(a.getName()))
                .findFirst()
                .orElseThrow(() -> new ExtensionConfigurationException(
                        "Агент с именем '" + name + "' не найден. Доступные: " +
                                response.getAgent().stream().map(Agent::getName).toList()));
    }
}
