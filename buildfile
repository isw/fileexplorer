repositories.remote << 'http://mirrors.ibiblio.org/pub/mirrors/maven2/'

GUICE         = transitive('com.google.inject:guice:jar:3.0')
GUICE_SRVL    = 'com.google.inject.extensions:guice-servlet:jar:3.0'
GUICE_EXT     = 'com.google.inject.extensions:guice-multibindings:jar:3.0'
GSON          = transitive('com.google.code.gson:gson:jar:2.1')
JAVA_INJECT   = 'javax.inject:javax.inject:jar:1'
SNAKE_YAML    = 'org.yaml:snakeyaml:jar:1.13'
SERVLET_API   = 'org.jboss.spec.javax.servlet:jboss-servlet-api_3.0_spec:jar:1.0.2.Final'
JUNIT         = 'junit:junit:jar:4.11'

define 'fileexplorer' do
  project.version = '0.1.0'

    
  compile.using :target => '1.7', :lint => 'all', :deprecation => true
  compile.with GUICE, GUICE_SRVL, GUICE_EXT, GSON, JAVA_INJECT, SNAKE_YAML, SERVLET_API
  test.with JUNIT

  before_task = task do 
    puts '[BEFORE]' 
    # sh "cd src/main/webapp/xplorer && ./generate build"
  end
     
  package :war
end

task :package => [:qx]

task :qx do
  puts 'BUILD qooxdoo application'
  sh "cd src/main/webapp/xplorer && ./generate source-all"
end
