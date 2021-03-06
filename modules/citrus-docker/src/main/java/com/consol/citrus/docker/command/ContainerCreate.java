/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.docker.command;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.docker.client.DockerClient;
import com.consol.citrus.docker.message.DockerMessageHeaders;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 * @since 2.4
 */
public class ContainerCreate extends AbstractDockerCommand<CreateContainerResponse> {

    public static final String DELIMITER = ";";

    /**
     * Default constructor initializing the command name.
     */
    public ContainerCreate() {
        super("docker:container:create");
    }

    @Override
    public void execute(DockerClient dockerClient, TestContext context) {
        CreateContainerCmd command = dockerClient.getEndpointConfiguration().getDockerClient().createContainerCmd(getImageId(context));

        if (hasParameter("name")) {
            command.withName(getParameter("name", context));
        }

        if (hasParameter("attach-stderr")) {
            command.withAttachStderr(Boolean.valueOf(getParameter("attach-stderr", context)));
        }

        if (hasParameter("attach-stdin")) {
            command.withAttachStdin(Boolean.valueOf(getParameter("attach-stdin", context)));
        }

        if (hasParameter("attach-stdout")) {
            command.withAttachStdout(Boolean.valueOf(getParameter("attach-stdout", context)));
        }

        if (hasParameter("capability-add")) {
            if (getParameters().get("capability-add") instanceof Capability[]) {
                command.withCapAdd((Capability[]) getParameters().get("capability-add"));
            } else {
                command.withCapAdd(getCapabilities("capability-add", context));
            }
        }

        if (hasParameter("capability-drop")) {
            if (getParameters().get("capability-drop") instanceof Capability[]) {
                command.withCapAdd((Capability[]) getParameters().get("capability-drop"));
            } else {
                command.withCapDrop(getCapabilities("capability-drop", context));
            }
        }

        if (hasParameter("domain-name")) {
            command.withDomainName(getParameter("domain-name", context));
        }

        if (hasParameter("cmd")) {
            if (getParameters().get("cmd") instanceof Capability[]) {
                command.withCmd((String[]) getParameters().get("cmd"));
            } else {
                command.withCmd(StringUtils.delimitedListToStringArray(getParameter("cmd", context), DELIMITER));
            }
        }

        if (hasParameter("env")) {
            if (getParameters().get("env") instanceof Capability[]) {
                command.withEnv((String[]) getParameters().get("env"));
            } else {
                command.withEnv(StringUtils.delimitedListToStringArray(getParameter("env", context), DELIMITER));
            }
        }

        if (hasParameter("entrypoint")) {
            command.withEntrypoint(getParameter("entrypoint", context));
        }

        if (hasParameter("hostname")) {
            command.withHostName(getParameter("hostname", context));
        }

        if (hasParameter("port-specs")) {
            if (getParameters().get("port-specs") instanceof Capability[]) {
                command.withPortSpecs((String[]) getParameters().get("port-specs"));
            } else {
                command.withPortSpecs(StringUtils.delimitedListToStringArray(getParameter("port-specs", context), DELIMITER));
            }
        }

        if (hasParameter("exposed-ports")) {
            if (getParameters().get("exposed-ports") instanceof ExposedPort[]) {
                command.withExposedPorts((ExposedPort[]) getParameters().get("exposed-ports"));
            } else {
                command.withExposedPorts(getExposedPorts(context));
            }
        }

        if (hasParameter("volumes")) {
            if (getParameters().get("volumes") instanceof ExposedPort[]) {
                command.withVolumes((Volume[]) getParameters().get("volumes"));
            } else {
                command.withVolumes(getVolumes(context));
            }
        }

        if (hasParameter("working-dir")) {
            command.withWorkingDir(getParameter("working-dir", context));
        }

        CreateContainerResponse response = command.exec();
        context.setVariable(DockerMessageHeaders.CONTAINER_ID, response.getId());
        setCommandResult(response);

        if (!hasParameter("name")) {
            InspectContainerCmd inspect = dockerClient.getEndpointConfiguration().getDockerClient().inspectContainerCmd(response.getId());
            InspectContainerResponse inspectResponse = inspect.exec();
            context.setVariable(DockerMessageHeaders.CONTAINER_NAME, inspectResponse.getName().substring(1));
        }
    }

    /**
     * Gets the volume specs from comma delimited string.
     * @return
     */
    private Volume[] getVolumes(TestContext context) {
        String[] volumes = StringUtils.commaDelimitedListToStringArray(getParameter("volumes", context));
        Volume[] volumeSpecs = new Volume[volumes.length];

        for (int i = 0; i < volumes.length; i++) {
            volumeSpecs[i] = new Volume(volumes[i]);
        }

        return volumeSpecs;
    }

    /**
     * Gets the capabilities added.
     * @return
     */
    private Capability[] getCapabilities(String addDrop, TestContext context) {
        String[] capabilities = StringUtils.commaDelimitedListToStringArray(getParameter(addDrop, context));
        Capability[] capAdd = new Capability[capabilities.length];

        for (int i = 0; i < capabilities.length; i++) {
            capAdd[i] = Capability.valueOf(capabilities[i]);
        }

        return capAdd;
    }

    /**
     * Construct set of exposed ports from comma delimited list of ports.
     * @return
     */
    private ExposedPort[] getExposedPorts(TestContext context) {
        String[] ports = StringUtils.commaDelimitedListToStringArray(getParameter("exposed-ports", context));
        ExposedPort[] exposedPorts = new ExposedPort[ports.length];

        for (int i = 0; i < ports.length; i++) {
            if (ports[i].startsWith("udp:")) {
                exposedPorts[i] = ExposedPort.udp(Integer.valueOf(ports[i].substring("udp:".length())));
            } else if (ports[i].startsWith("tcp:")) {
                exposedPorts[i] = ExposedPort.tcp(Integer.valueOf(ports[i].substring("tcp:".length())));
            } else {
                exposedPorts[i] = ExposedPort.tcp(Integer.valueOf(ports[i]));
            }
        }

        return exposedPorts;
    }

    /**
     * Sets the image id parameter.
     * @param id
     * @return
     */
    public ContainerCreate image(String id) {
        getParameters().put(IMAGE_ID, id);
        return this;
    }

    /**
     * Sets the image name parameter.
     * @param name
     * @return
     */
    public ContainerCreate name(String name) {
        getParameters().put("name", name);
        return this;
    }

    /**
     * Sets the attach-stderr parameter.
     * @param attachStderr
     * @return
     */
    public ContainerCreate attachStdErr(Boolean attachStderr) {
        getParameters().put("attach-stderr", attachStderr);
        return this;
    }

    /**
     * Sets the attach-stdin parameter.
     * @param attachStdin
     * @return
     */
    public ContainerCreate attachStdIn(Boolean attachStdin) {
        getParameters().put("attach-stdin", attachStdin);
        return this;
    }

    /**
     * Sets the attach-stdout parameter.
     * @param attachStdout
     * @return
     */
    public ContainerCreate attachStdOut(Boolean attachStdout) {
        getParameters().put("attach-stdout", attachStdout);
        return this;
    }

    /**
     * Adds capabilities as command parameter.
     * @param capabilities
     * @return
     */
    public ContainerCreate addCapability(Capability ... capabilities) {
        getParameters().put("capability-add", capabilities);
        return this;
    }

    /**
     * Drops capabilities as command parameter.
     * @param capabilities
     * @return
     */
    public ContainerCreate dropCapability(Capability ... capabilities) {
        getParameters().put("capability-drop", capabilities);
        return this;
    }

    /**
     * Sets the domain-name parameter.
     * @param domainName
     * @return
     */
    public ContainerCreate domainName(String domainName) {
        getParameters().put("domain-name", domainName);
        return this;
    }

    /**
     * Adds commands as command parameter.
     * @param commands
     * @return
     */
    public ContainerCreate cmd(String ... commands) {
        getParameters().put("cmd", commands);
        return this;
    }

    /**
     * Adds environment variables as command parameter.
     * @param envVars
     * @return
     */
    public ContainerCreate env(String ... envVars) {
        getParameters().put("env", envVars);
        return this;
    }

    /**
     * Sets the entrypoint parameter.
     * @param entrypoint
     * @return
     */
    public ContainerCreate entryPoint(String entrypoint) {
        getParameters().put("entrypoint", entrypoint);
        return this;
    }

    /**
     * Sets the hostname parameter.
     * @param hostname
     * @return
     */
    public ContainerCreate hostName(String hostname) {
        getParameters().put("hostname", hostname);
        return this;
    }

    /**
     * Adds port-specs variables as command parameter.
     * @param portSpecs
     * @return
     */
    public ContainerCreate portSpecs(String ... portSpecs) {
        getParameters().put("port-specs", portSpecs);
        return this;
    }

    /**
     * Adds exposed-ports variables as command parameter.
     * @param exposedPorts
     * @return
     */
    public ContainerCreate exposedPorts(ExposedPort ... exposedPorts) {
        getParameters().put("exposed-ports", exposedPorts);
        return this;
    }

    /**
     * Adds volumes variables as command parameter.
     * @param volumes
     * @return
     */
    public ContainerCreate volumes(Volume ... volumes) {
        getParameters().put("volumes", volumes);
        return this;
    }

    /**
     * Sets the working-dir parameter.
     * @param workingDir
     * @return
     */
    public ContainerCreate workingDir(String workingDir) {
        getParameters().put("working-dir", workingDir);
        return this;
    }
}
