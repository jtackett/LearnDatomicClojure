(ns sample.core 
	(:require [sample.utils :as u] ))

(defn -main
  "I don't do a whole lot."
  []
  (prn (identity u/evens-only)))
