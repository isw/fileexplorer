package de.innuendo.fileexplorer.setup;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebListener;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import de.innuendo.fileexplorer.config.ClassBinding;
import de.innuendo.fileexplorer.config.ConfigException;
import de.innuendo.fileexplorer.config.Configuration;
import de.innuendo.fileexplorer.config.ServiceBinding;
import de.innuendo.fileexplorer.filter.EncodingFilter;
import de.innuendo.fileexplorer.service.api.IComponent;
import de.innuendo.fileexplorer.servlets.JSONCall;

@WebListener
public class ExplorerConfig extends GuiceServletContextListener {
  @SuppressWarnings("unchecked")
  Map<String,?> loadConfig(String path) {
    Yaml y = new Yaml ();
    try {
      return (Map<String,?>)y.load(new FileInputStream(path));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  String getConfigPath(String key) {
	  String val = System.getProperty("xplorerconfig");
	  if (val == null) {
		  return System.getenv("xplorerconfig");
	  }
	  return val;
  }

  
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new ServletModule() {

      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      protected void configureServlets() {
        filter("/json").through(EncodingFilter.class);
        //serve("*.test").with(
        //    de.innuendo.fileexplorer.servlets.TestServlet.class);
        serve("/json").with(JSONCall.class);
        
        bind(Gson.class).toInstance(new Gson());

        MapBinder<String, IComponent> mapbinder = MapBinder.newMapBinder(
            this.binder(), String.class, IComponent.class);
        Multibinder<IComponent> components = Multibinder.newSetBinder(this.binder(), IComponent.class);
        
        try {
          Configuration services = new Configuration (ExplorerConfig.this.loadConfig(ExplorerConfig.this.getConfigPath("services")));
          
          List<ClassBinding> bindings = (List<ClassBinding>)services.getList("ClassBindings");
          for (ClassBinding b : bindings) {
            try {
              bind (b.getFromClass()).to(b.getToClass());
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
          List<ServiceBinding> serv = (List<ServiceBinding>)services.getList("ServiceBindings");
          for (ServiceBinding sb : serv) {
            try {
              if (sb.getImplementations().size() == 1) {
                IComponent comp = sb.getImplementations().get(0);
                bind (sb.getInterface()).toInstance(comp);
                //mapbinder.addBinding(comp.getComponentName()).toInstance(comp);
              }
//              else {
                Multibinder mb = Multibinder.newSetBinder(this.binder(), sb.getInterface());              
                for (IComponent sbimpl : sb.getImplementations()) {
                  components.addBinding().toInstance(sbimpl);
                  mb.addBinding().toInstance(sbimpl);
                  mapbinder.addBinding(sbimpl.getComponentName()).toInstance(sbimpl);
                }
//              }
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
          /*
          List<IRemoteService> json = (List<IRemoteService>)services.getList("JsonServices");
          for (IRemoteService js : json) {
            js.setConfiguration(config);
            mapbinder.addBinding(js.getName()).toInstance(js);
          }*/
        } catch (ConfigException e) {
          throw new RuntimeException (e);
        }
      }
    });
  }
}
