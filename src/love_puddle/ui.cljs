(ns love-puddle.ui
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom :as dom]))

(def francis-john-example-input
  "FatManSlim, Jerk Dain
Jerk Dain, FatManSlim, Unbreakable, Simao, Valiant
Unbreakable, Jerk Dain
Simao, Jerk Dain
Valiant, Jerk Dain, Tree, D. Delair
Tree, Valiant
D. Delair, Valiant, Jeremy, Gus
Jeremy, D. Delair
Gus, D. Delair, Casey, Charlie
Charlie, Gus
Casey, Gus, Sarah, Ralph
Sarah, Casey, InTern Zero
Ralph, Casey, Darrell
InTern Zero, Sarah
Darrell, Ralph, Pietromassimo
Pietromassimo, Darrell, GreyGhost
GreyGhost, Pietromassimo
Daniel Talty
Tautvydas
")

(defonce *data (r/atom {:input-text francis-john-example-input}))

(defn input-text->possible-pairs [input]
  (->> (str/split-lines input)
       (remove str/blank?)
       (mapcat (fn [line]
                 (let [[colonist & partners] (->> (str/split line #",")
                                                  (map str/trim))]
                   (if (some? partners)
                     (map (fn [partner]
                            #{colonist partner})
                          partners)
                     [#{colonist}]))))))

(defn app []
  [:<>
   [:header
    [:h1 "RimWorld Love Puddle Calculator"]]
   [:main
    [:p "Enter your RimWorld colony and their relationships into the following text box.
         Each row should start with the name of the colonist, followed by their partners and lovers.
         All names on the same line must be separated by a comma."]
    [:textarea {:rows 25
                :cols 80}
     (str (:input-text @*data))]]])


(defn init! []
  (if-some [root (.getElementById js/document "root")]
    (dom/render [app] root)))

(init!)

(defn ^:dev/before-load stop [])

(defn ^:dev/after-load start []
  (init!))
