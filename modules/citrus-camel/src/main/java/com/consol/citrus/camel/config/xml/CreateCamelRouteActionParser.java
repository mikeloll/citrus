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

package com.consol.citrus.camel.config.xml;

import com.consol.citrus.camel.actions.CreateCamelRouteAction;
import org.apache.camel.spring.CamelRouteContextFactoryBean;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBException;

/**
 * Bean definition parser for creating Camel routes action in test case.
 * 
 * @author Christoph Deppisch
 * @since 2.4
 */
public class CreateCamelRouteActionParser extends AbstractCamelRouteActionParser {

    @Override
    public void parse(BeanDefinitionBuilder beanDefinition, Element element, ParserContext parserContext) {
        // now lets parse the routes with JAXB
        Binder<Node> binder;
        try {
            binder = getJaxbContext().createBinder();
            Object value = binder.unmarshal(DomUtils.getChildElementByTagName(element, "routeContext"));

            if (value instanceof CamelRouteContextFactoryBean) {
                CamelRouteContextFactoryBean factoryBean = (CamelRouteContextFactoryBean) value;
                beanDefinition.addPropertyValue("routes", factoryBean.getRoutes());
            }
        } catch (JAXBException e) {
            throw new BeanDefinitionStoreException("Failed to create the JAXB binder", e);
        }
    }

    @Override
    protected Class<?> getBeanDefinitionClass() {
        return CreateCamelRouteAction.class;
    }
}