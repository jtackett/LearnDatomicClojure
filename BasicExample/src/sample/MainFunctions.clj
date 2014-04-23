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

(defn create-empty-db []
  (def uri "datomic:dev://localhost:4334/BalanceSheet")
		(d/delete-database uri)
		(d/create-database uri)
    (def conn (d/connect uri))
    (def schema (load-file "src/sample/schema.edn"))
			(d/transact conn schema))

(defn refresh-schema-db []
  (def uri "datomic:dev://localhost:4334/BalanceSheet")
    (d/create-database uri)
    (def conn (d/connect uri))
    (def schema (load-file "src/sample/schema.edn"))
      (d/transact conn schema))

(defn refresh-data[]
  (def data (load-file "src/sample/BSdata.edn"))
  (d/transact conn data))

(defn find-account-names []
  (d/q '[:find ?name :where [?eid :Account/name ?name]] (d/db conn))
  )

(defn find-account-names-values[]
(d/q '[:find ?Productstyle ?Productquantity 
       :where [?eid :Account/name ?Productstyle]
       [?eid :Account/value ?Productquantity]]
        (d/db conn)))
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


(comment http://docs.datomic.com/tutorial.html 
  in the advanced query section)


(comment FINALLY PASSING PARAMATERS!!!!!)

(defn get-account-id-by-name [n]
   (ffirst
      (d/q '[:find ?eid
               :in $ ?n
               :where 
                [?eid :Account/name ?n]]
               (d/db (d/connect uri)) n)))

(defn update-account-balance [account-id new-balance]
    (d/transact (d/connect uri) 
        [[:db/add account-id :Account/value new-balance]]))

(comment below is the proper call for updating a balance)

(update-account-balance
      (get-account-id-by-name "Cash") 
      125.50)

(defn get-account-value-by-name [n]
   (ffirst
      (d/q '[:find ?value
               :in $ ?n
               :where [?eid :Account/name ?n ]
                      [?eid :Account/value ?value]]
               (d/db (d/connect uri)) n)))



(comment the below doesn't work)

(defn increase-cash []

  (def c (ffirst(find-account-value)))

  (def i (+ c 100.0))

  (def cash-id (->> (d/q '[:find ?e :where 
               [?e :Account/name "Cash"]]
               (d/db conn)) ffirst))

  (def data ([[:db/add cash-id :Account/value i]]))

(d/transact conn data))









