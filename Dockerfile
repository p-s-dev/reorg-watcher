FROM java:9

ENV JAVA_CONF_DIR=$JAVA_HOME/conf

# To see work around for bug, uncomment this line...
RUN bash -c '([[ ! -d $JAVA_SECURITY_DIR ]] && ln -s $JAVA_HOME/lib $JAVA_HOME/conf) || (echo "Found java conf dir, package has been fixed, remove this hack"; exit -1)'

RUN mkdir -p /tmp/java-test

COPY build/libs/chainreorg-0.1.0.jar /tmp/java-test/

CMD java -jar /tmp/java-test/chainreorg-0.1.0.jar
