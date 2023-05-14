(ns kafttp.api
  (:require [reitit.swagger :as swagger]
            [kafttp.handlers :as h]))

(def routes
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title       "Kafttp API"
                            :version     "0.0.1"}}
           :handler (swagger/create-swagger-handler)}}]

   ["/filters"
    [""
     {:get  {:summary   "Returns filters"
             :responses {200 {:body any?}}
             :handler   h/all-filters}

      :post {:summary    "Creates filter"
             :parameters {:body [:map
                                 [:topic
                                  {:description "Topic name"}
                                  string?]
                                 [:q
                                  {:description "String value"}
                                  string?]]}
             :responses  {200 {:body any?}}
             :handler    h/create-filter}}]

    ["/:id"
     ["" {:get    {:summary    "Returns filter by ID"
                   :parameters {:path [:map
                                       [:id
                                        {:description "Filter ID"}
                                        string?]]}
                   :responses  {200 {:body any?}}
                   :handler    h/filter-by-id}

          :delete {:summary    "Deletes filter by ID"
                   :parameters {:path [:map
                                       [:id
                                        {:description "Filter ID"}
                                        string?]]}
                   :responses  {200 {:body any?}}
                   :handler    h/delete-filter}}]

     ["/messages" {:get {:summary    "Returns the messages for a given filter"
                         :parameters {:path [:map
                                             [:id
                                              {:description "Filter ID"}
                                              string?]]}
                         :responses  {200 {:body any?}}
                         :handler    h/filter-messages}}]]]])