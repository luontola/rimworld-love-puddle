(defproject rimworld-love-puddle "0.1.0-SNAPSHOT"

  :dependencies [[com.google.javascript/closure-compiler-unshaded "v20220803"]
                 [medley "1.4.0"]
                 [mhuebert/kitchen-async "0.1.0"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.60"]
                 [reagent "1.1.1"]
                 [thheller/shadow-cljs "2.20.2"]]
  :managed-dependencies [[org.clojure/core.async "1.5.648"]]
  :pedantic? :abort
  :global-vars {*warn-on-reflection* true}

  :main ^:skip-aot kata
  :target-path "target/%s"
  :javac-options ["--release" "11"]
  :jvm-opts ["--illegal-access=deny"
             "-XX:-OmitStackTraceInFastThrow"]

  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]}
  :plugins [[lein-ancient "0.7.0"]]

  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[lambdaisland/kaocha "1.70.1086"]
                                  [nrepl "1.0.0"]
                                  [org.clojure/test.check "1.1.1"]]}
             :kaocha {}
             :cljs {:global-vars {*warn-on-reflection* false}}})
