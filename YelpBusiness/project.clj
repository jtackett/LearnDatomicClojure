(defproject sample "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] 
  				       [com.datomic/datomic-pro "0.9.4609"]
  				       [datomic-schema "1.0.2"]
                 [org.clojure/data.json "0.2.4"]
                 [org.scribe/scribe "1.3.5"]]
  :source-paths ["src"]
  :main ^:skip-aot sample.core
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  )
