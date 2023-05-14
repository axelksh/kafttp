(ns kafttp.storage
  (:require [clojure.set :refer [union]])
  (:import [java.util UUID]))

(def ^:private filters (ref []))     ;; a single Atom could be used for both filters and messages
                                     ;; but it wouldn't be so interesting :)
(def ^:private messages (ref []))

(defn- gen-uuid []
  (.toString (UUID/randomUUID)))

(defn- find-by-ids [ids collection]
  (let [ids-set (into #{} ids)]
    (reduce (fn [acc el]
              (if (contains? ids-set (:id el))
                (union acc [el])
                acc)) [] collection)))

(defn- update-in-collection [collection ids key-seq update-fn]
  (let [ids-set (into #{} ids)]
    (reduce (fn [acc el]
              (let [updated (if (contains? ids-set (:id el))
                              (update-in el key-seq update-fn)
                              el)]
                (union acc [updated]))) [] collection)))

(defn get-all-filters []
  @filters)

(defn get-filters-by-ids [ids]
  (find-by-ids ids @filters))

(defn save-filter [filter]
  (let [filter (merge filter
                      {:id (gen-uuid)})]
    (dosync (alter filters #(union % [filter])))
    filter))

(defn delete-filter [id]
  (dosync (alter filters (fn [fs]
                           (remove (fn [f] (= id (:id f))) fs)))))

(defn save-message [message filter-ids]
  (let [message-id (gen-uuid)
        message (merge message {:id message-id})]
    (dosync
      (alter messages #(union % [message]))
      (alter filters (fn [fs] (update-in-collection fs filter-ids [:message-ids] #(union % [message-id])))))
    message))

(defn get-messages-by-ids [ids]
  (find-by-ids ids @messages))

(defn get-filter-messages [filter-id]
  (-> (filter #(= filter-id (:id %)) @filters)
      first
      :message-ids
      (get-messages-by-ids)))

(comment
  (save-filter {:topic "books" :q "sicp"})

  (save-filter {:topic "books" :q "code complete"})

  (get-filters-by-ids ["4bc787e5-83df-4d14-af24-bfdf1d2e6352"])

  (save-message {:message "I have read sicp"} ["4bc787e5-83df-4d14-af24-bfdf1d2e6352"])

  (save-message {:message "I have read code complete"} ["f6e78421-8657-44e1-a7e6-5c9b1636bb05"])

  (get-filter-messages "4bc787e5-83df-4d14-af24-bfdf1d2e6352"))

