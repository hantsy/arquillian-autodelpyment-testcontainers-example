package com.example.it;

import com.example.GreetingMessage;
import com.example.GreetingResource;
import com.example.GreetingService;
import com.example.JaxrsActivator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.DeploymentConfiguration;
import org.jboss.arquillian.container.test.api.DeploymentConfiguration.DeploymentContentBuilder;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.spi.client.deployment.AutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GreetingResourceDeployment implements AutomaticDeployment {
    private final Logger LOGGER = Logger.getLogger(GreetingResourceDeployment.class.getName());
    private WebArchive  deploymentCache;

    @Override
    public DeploymentConfiguration generateDeploymentScenario(TestClass testClass) {
        if (skipAutoDeployment(testClass)) {
            LOGGER.log(Level.INFO, "found @Deployment in the test class, skip autodeployment here.");
            return null;
        }

        if( deploymentCache== null) {
            deploymentCache = ShrinkWrap.create(WebArchive.class)
                    .addClass(GreetingMessage.class)
                    .addClass(GreetingService.class)
                    .addClass(GreetingResource.class)
                    .addClass(JaxrsActivator.class)
                    // Enable CDI (Optional since Java EE 7.0)
                    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

            LOGGER.log(Level.INFO, "generated deployment files: {}", deploymentCache.toString(true));
        }

        return new DeploymentContentBuilder(deploymentCache)
                .withDeployment()
                .withTestable(isTestable(testClass))// client test.
                .build()
                .get();
    }

    private boolean skipAutoDeployment(TestClass tc) {
        return tc.getMethod(Deployment.class) != null;
    }

    private boolean isTestable(TestClass tc) {
        return tc.getMethod(RunAsClient.class) != null;
    }
}