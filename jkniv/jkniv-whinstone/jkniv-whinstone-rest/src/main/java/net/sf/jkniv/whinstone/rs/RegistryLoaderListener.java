package net.sf.jkniv.whinstone.rs;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.Packages;

/**
 * Register the data types from queries and reports when the web context is initialized
 * 
 * @author Alisson Gomes
 *
 */
public class RegistryLoaderListener implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(RegistryLoaderListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext sc = sce.getServletContext();
        
        String modelPackages = sce.getServletContext().getInitParameter("model.packages");
        registerModels(modelPackages);
        
        String reportPackages = sce.getServletContext().getInitParameter("report.packages");
        registerReports(reportPackages);

        String transformPackages = sce.getServletContext().getInitParameter("transform.packages");
        registerTransformers(transformPackages);

        String sqlContextName = sce.getServletContext().getInitParameter("fullname.sqlContext");
        if (sqlContextName != null)
            RestFulResources.registrySqlContext(sqlContextName);
    }
    
    private void registerModels(String modelPackages) 
    {
        Packages packages = new Packages(modelPackages);
        try
        {
            packages.scan();
            String[] classesNames = packages.asResources();
            
            for(String s : classesNames)
            {
                RestFulResources.registryModel(s);
            }
        }
        catch (IOException e)
        {
            LOG.error("[MODEL] Cannot registry classes from [{}] packages", modelPackages);
        }
    }

    private void registerReports(String reportPackages) 
    {
        Packages packages = new Packages(reportPackages);
        try
        {
            packages.onlyResource(".jasper");
            packages.scanResource();
            String[] resources = packages.asResources();
            
            for(String s : resources)
            {
                RestFulResources.registryReport(s);
            }
        }
        catch (IOException e)
        {
            LOG.error("[JASPER] Cannot registry reports from [{}] packages", reportPackages);
        }
    }

    private void registerTransformers(String transformPackages) 
    {
        Packages packages = new Packages(transformPackages);
        try
        {
            packages.scan();
            String[] classesNames = packages.asResources();
            
            for(String s : classesNames)
            {
                RestFulResources.registryTransformers(s);
            }
        }
        catch (IOException e)
        {
            LOG.error("[TRANSFORMER] Cannot registry classes from [{}] packages", transformPackages);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        RestFulResources.cleanup();
    }
    
}
