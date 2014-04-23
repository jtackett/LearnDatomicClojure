(ns yelp.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:import [java.io PushbackReader]
           [org.scribe.builder ServiceBuilder]
           [org.scribe.builder.api DefaultApi10a]
           [org.scribe.model OAuthRequest Token Verb]))

(defrecord YelpClient [service token])

(def ^:private base-url "http://api.yelp.com/v2/")

(def ^:private YelpApi2
  (class
    (proxy [DefaultApi10a] []
      (getAccesTokenEndpoint [] nil)
      (getAuthorizationUrl [t] nil)
      (getRequestTokenEndpoint [] nil))))


(defn get-api-keys
  [filename]
  (with-open [r (io/reader filename)]
    (read (PushbackReader. r))))

(defn create-yelp-client
  "Build a Yelp client, given map from get-api-keys"
  [params]
  (let [service (.. (ServiceBuilder.)
                    (provider YelpApi2)
                    (apiKey (:consumerKey params))
                    (apiSecret (:consumerSecret params))
                    (build))
        token (Token. (:token params) (:tokenSecret params))]
    (->YelpClient service token)))


(def msg-yelp-client (create-yelp-client (get-api-keys "api-keys.clj")))


(defn business-lookup-by-id
  [client id & [params]]
  {:pre [(instance? YelpClient client)]}
  (let [req (OAuthRequest. (Verb/GET) (str base-url "business/" id))]
    (doseq [[k, v] params]
      (.addQuerystringParameter req (str k) (str v)))
    (.signRequest (:service client) (:token client) req)
    (-> req .send .getBody (json/read-str :key-fn keyword))))

(defn whitespace-to-plus
  [input-string]
  (clojure.string/replace input-string #"\s" "+"))

(defn set-term
  [params]
  (str "?term" "="
       (whitespace-to-plus (:term params))))

(defn map-to-url
  [params]
  (str
    (set-term params)
    (clojure.string/join
      (vec
        (map #(str "&" (name %) "=" (% params))
              (filter #(not= :term %) (keys params)))))))


(defn business-search
  "input map of form {:term _ :location _}"
  [client params]
  {:pre [(instance? YelpClient client)]}
  (let [req (OAuthRequest. (Verb/GET) (str base-url "search/" (map-to-url params)))]
    (doseq [[k, v] params]
      (.addQuerystringParameter req (str k) (str v)))
    (.signRequest (:service client) (:token client) req)
    (-> req .send .getBody (json/read-str :key-fn keyword))))