(ns mini-jmx.core
  (:import [java.lang.management ManagementFactory]
           [javax.management.remote JMXServiceURL JMXConnectorServerFactory JMXConnector]
           [java.rmi.registry LocateRegistry]))

(defn spawn-connector-server
  ([env rmi-general-port]
     (spawn-connector-server env rmi-general-port rmi-general-port))
  ([env rmi-registry-port rmi-export-port]
     ;; create the RMI registry
     (LocateRegistry/createRegistry rmi-registry-port)
     ;; spawn connector server
     (let [mbs (ManagementFactory/getPlatformMBeanServer)
           env (if-let [credential-seq (get env JMXConnector/CREDENTIALS)]
                 (assoc env JMXConnector/CREDENTIALS
                        (into-array String credential-seq))
                 env)
           url (JMXServiceURL. "service:jmx:rmi://localhost:3000/jndi/rmi://localhost:3000/jmxrmi")
           cs  (JMXConnectorServerFactory/newJMXConnectorServer url env mbs)]
       (.start cs)
       cs)))
