#Infinispan as Tomcat session storage.

Aim of this project is to implement session storage using Infinispan. Using Infinispan over default implementation has several advantages:
* session manager can share Infinispan configuration with application or event share the same instance of embedded Infinispan;
* Infinispan has much more options to configure distribution sessions over tomcat's cluster;
* At last, but not least, due to fact that Session manager can share IS configuration with an web-application, one may avoid one place of cluster configuration. Sometimes it is very important to keep distributed options as few as possible.
 
