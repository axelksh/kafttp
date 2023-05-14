(ns kafttp.kafka
  (:import (java.time Duration)
           [org.apache.kafka.clients.consumer KafkaConsumer]
           [org.apache.kafka.common.serialization StringDeserializer]))

(def ^:private default-props
  {"bootstrap.servers"  "broker:29092"
   "key.deserializer"   StringDeserializer
   "value.deserializer" StringDeserializer
   "auto.offset.reset"  "earliest"
   "enable.auto.commit" "true"
   "group.id"           "default"})

(defn- records->map [records]
  (map (fn [r] {:message   (.value r)
                :timestamp (.timestamp r)}) records))

(defn subscribe-consumer [consumer topics]
  (.subscribe consumer topics))

(defn mk-consumer
  ([topics]
   (mk-consumer topics nil))
  ([topics props]
   (let [consumer (KafkaConsumer. (merge default-props props))]
     (subscribe-consumer consumer topics)
     consumer)))

(defn listen-messages
  [consumer handler]
  (.start
    (Thread.
      (fn []
        (while true
          (let [records (.poll consumer (Duration/ofMillis 3000))]
            (handler (records->map records))))))))

(comment
  (listen-messages (mk-consumer ["books"])
                   (fn [messages]
                     (doseq [m messages]
                       (prn "Message: " m))))

  (listen-messages (mk-consumer ["films"])
                   (fn [messages]
                     (doseq [m messages]
                       (prn "Message: " m))))
  )
