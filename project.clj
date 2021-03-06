(defproject dame "0.1.2"
  :description "A Clojure implementation of the german checker game called Dame (Lady)"
  :url "https://github.com/MikeHardIce/Dame"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clojure2d "1.4.3"]
                 [strigui "0.0.1-alpha8"]]
  :keep-non-project-classes true
  :main dame.core
  :aot [dame.core]
  :repl-options {:init-ns dame.core}
  :profiles {:uberjar {:aot [dame.core] }})
