(comment
	Basic code for creating the Balance sheet database
  Great tutorial => https://www.youtube.com/watch?v=ao7xEwCjrWQ 

  Currently Noted:
      function for creating a data file refresh doesn't yet work (fix for ease)
      All schema and data insertion works (finish more)
      (create working queries so that klassner and show you
        what to do with them)
	)

((require '[datomic.api :as d])

  (ns example.core
  (:require [yelp.core :as yelp]))

;; Create connection with API information
(def msg-yelp-client (yelp/create 
  {:consumerKey "CONSUMER KEY" 
   :consumerSecret "CONSUMER SECRET" 
   :token "TOKEN" :tokenSecret "TOKEN SECRET"}))

;; Search for businesses in Beltsville containing the word "atomic"
(business-search msg-yelp-client 
  {:term "atomic" :location "Beltsville"})

(comment SETUP  ________________________________________)
(defn create-empty-db []
  (def uri "datomic:dev://localhost:4334/BusinessYelp")
		(d/delete-database uri)
		(d/create-database uri)
    (def conn (d/connect uri))
    (def schema (load-file "src/sample/schema.edn"))
			(d/transact conn schema))

(defn refresh-schema-db []
  (def uri "datomic:dev://localhost:4334/BusinessYelp")
    (d/create-database uri)
    (def conn (d/connect uri))
    (def schema (load-file "src/sample/schema.edn"))
      (d/transact conn schema))

(defn refresh-data[]
  (def data (load-file "src/sample/BusinessData.edn"))
  (d/transact conn data))


(comment QUERIES/TRANSACTIONS ________________________________________)


(defn add-business [nm city email]
  (d/transact conn
  [{:db/id #db/id [:db.part/user]
    :Business/nm nm
    :Business/city city
    :Business/email email]
  ))

(defn getBusinessNames []
  (d/q '[:find ?name 
         :where [?eid :Business/nm ?name]] (d/db conn)))

(defn get-business-ID [nm city email]
   (ffirst
      (d/q '[:find ?eid
               :in $ nm ?city ?email 
               :where 
                [?eid :Business/nm ?nm]
                [?eid :Business/city ?city]
                [?eid :Business/email ?email]]
               (d/db (d/connect uri)) nm city email)))




(comment Word Frequency Counter)
;; Create a word frequency map out of a large string s.
;; `s` is a long string containing a lot of words :)
(reduce #(assoc %1 %2 (inc (%1 %2 0)))
        {}
        (re-seq #"\w+" s))

; (This can also be done using the `frequencies` function.)



(comment Extra templetes for transactions)

(defn change-quantity [product-id q]
    (d/transact (d/connect uri) 
        [[:db/add product-id :Product/quantity q]])
  )


(change-quantity (get-product-ID "Skort" "A/L" "Camo") 50)

(comment ____________________________________________________)

(comment FINDING QUANTITY___________________________________)

(defn get-product-quantity [s sz pt]
   (ffirst
      (d/q '[:find ?q
               :in $ ?s ?sz ?pt
               :where 
                [?eid :Product/style ?s]
                [?eid :Product/size ?sz]
                [?eid :Product/pattern ?pt]
                [?eid :Product/quantity ?q]]
               (d/db (d/connect uri)) s sz pt)))















(comment Extra stuff__________________________________________)
(defn find-account-names []
  (d/q '[:find ?name 
         :where [?eid :Account/name ?name]] (d/db conn))
  )

(defn find-account-names-values[]
(d/q '[:find ?AccountName ?AccountValue 
       :where [?eid :Account/name ?AccountName]
       [?eid :Account/value ?AccountValue]]
        (d/db conn)))
)

(defn trns-initial []
  
  (d/transact conn [{:db/id #db/id [:db.part/user]
  :BalanceSheet/name "Straton Oakmont"
  :BalanceSheet/year 2013}])

  )

(defn find-BS-ID []
  (->> (d/q '[:find ?e :where 
          [?e :BalanceSheet/name "Straton Oakmont"]]
          (d/db conn)) ffirst)
  )


(defn find-cash-value[]
  (d/q '[:find ?AccountValue
         :where [?eid :Account/name "Cash" ]
         [?eid :Account/value ?AccountValue]]
         (d/db conn)))

(defn update-account-balance [account-id new-balance]
    (d/t (d/connect uri) 
        [[:db/add account-id :Account/balance new-balance]])))

(defn find-account-id [name]
   (ffirst
      (d/q '[:find ?eid
               :in $ ?name
               :where 
               [?eid :Account/name :name]]
               (d/db (d/connect uri)
               name))))

(comment Not working yet ----------------------- )

(defn increase-cash []

  (def c (ffirst(find-account-value)))

  (def i (+ c 100.0))

  (def cash-id (->> (d/q '[:find ?e :where 
               [?e :Account/name "Cash"]]
               (d/db conn)) ffirst))

  (def data ([[:db/add cash-id :Account/value i]]))

(d/transact conn data))









