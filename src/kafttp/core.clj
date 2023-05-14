(ns kafttp.core
  (:gen-class)
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [malli.util :as mu]
            [kafttp.api :as api]
            [kafttp.kafka :as k]
            [kafttp.handlers :as h]))

(def app
  (ring/ring-handler
    (ring/router
      api/routes
      {:exception pretty/exception
       :data      {:coercion   (reitit.coercion.malli/create
                                 {:error-keys       #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                                  :compile          mu/closed-schema
                                  :strip-extra-keys true
                                  :default-values   true
                                  :options          nil})
                   :muuntaja   m/instance
                   :middleware [swagger/swagger-feature
                                parameters/parameters-middleware
                                muuntaja/format-negotiate-middleware
                                muuntaja/format-response-middleware
                                exception/exception-middleware
                                muuntaja/format-request-middleware
                                coercion/coerce-response-middleware
                                coercion/coerce-request-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/"
         :config {:validatorUrl     nil
                  :urls             [{:name "swagger", :url "swagger.json"}]
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(defn start-web []
  (jetty/run-jetty #'app {:port 3000, :join? false})
  (prn "server running in port 3000"))

(defn subscribe-queue []
  (k/listen-messages
    (k/mk-consumer ["books"])
    (h/process-topic-messages "books"))

  (k/listen-messages
    (k/mk-consumer ["films"])
    (h/process-topic-messages "films")))

(defn -main []
  (start-web)
  (subscribe-queue))
