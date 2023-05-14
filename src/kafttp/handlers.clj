(ns kafttp.handlers
  (:require [ring.util.http-response :as response]
            [kafttp.storage :as s]))

(defn all-filters
  [_]
  (response/ok (s/get-all-filters)))

(defn filter-by-id
  [{{{:keys [id]} :path} :parameters}]
  (response/ok (first (s/get-filters-by-ids [id]))))

(defn create-filter [{{:keys [body]} :parameters}]
  (response/ok (s/save-filter body)))

(defn delete-filter
  [{{{:keys [id]} :path} :parameters}]
  (s/delete-filter id)
  (response/ok {:status "ok"}))

(defn filter-messages
  [{{{:keys [id]} :path} :parameters}]
  (response/ok (s/get-filter-messages id)))

(defn substring? [str sub-str]
  (.contains (.toLowerCase str) (.toLowerCase sub-str)))

(defn process-topic-messages [topic]
  (fn [messages]
    (let [topic-filters (->> (s/get-all-filters)
                             (filter #(= topic (:topic %))))]
      (doseq [m messages]
        (let [fids (->> (filter #(substring? (:message m) (:q %)) topic-filters)
                        (map :id))]
          (if-not (empty? fids)
            (s/save-message m fids)))))))

(comment
  (save-filter {:topic "books" :q "sicp"})
  (save-filter {:topic "films" :q "star wars"})

  (def messages [{:message "sicp", :timestamp 1683981878269}
                 {:message "star wars", :timestamp 1683981878269}])

  (let [topic-filters (->> (s/get-all-filters)
                           (filter #(= "books" (:topic %))))]
    (doseq [m messages]
      (let [fids (->> (filter #(= (:message m) (:q %)) topic-filters)
                      (map :id))]
        (if-not (empty? fids)
          (s/save-message m fids))))))