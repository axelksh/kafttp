(defproject kafttp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/reitit "0.5.15"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]
                 [metosin/jsonista "0.2.6"]
                 [metosin/ring-http-response "0.9.3"]
                 [org.apache.kafka/kafka-clients "3.4.0"]]
  :main ^:skip-aot kafttp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
