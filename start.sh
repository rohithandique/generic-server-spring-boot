#!/bin/sh
exec java \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.rmi.port=9010 \
  -Dspring.profiles.active=prod \
  -Dspring.security.user.roles=ADMIN,USER,ACTUATOR \
  -jar generic-server-spring-boot.jar