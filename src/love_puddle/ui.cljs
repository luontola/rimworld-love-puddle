(ns love-puddle.ui
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom :as dom]))

;; Example data from https://youtu.be/X2amcS4Isu0?t=1420
(def francis-john-example-input-v1
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

;; Example data from https://youtu.be/X2amcS4Isu0?t=4124
(def francis-john-example-input-v2
  "FatManSlim, Jerk Dain
Jerk Dain, FatManSlim, Unbreakable, Simao, Valiant
Unbreakable, Jerk Dain, InTern Zero
Simao, Jerk Dain, Charlie
Valiant, Jerk Dain, Tree, D. Delair, Darrell, Charlie
Tree, Valiant
D. Delair, Valiant, Jeremy, Gus
Jeremy, D. Delair
Gus, D. Delair, Casey, Charlie, Tautvydas
Charlie, Gus, Simao, Valiant
Casey, Gus, Sarah, Ralph
Sarah, Casey, InTern Zero, Daniel Talty
Ralph, Casey, Darrell
InTern Zero, Sarah, Unbreakable
Darrell, Ralph, Pietromassimo, Valiant
Pietromassimo, Darrell, GreyGhost
GreyGhost, Pietromassimo
Tautvydas, Gus
Daniel Talty, Sarah
")

(defonce *data (r/atom {:input-text francis-john-example-input-v1}))

(defn parse-input-text [input]
  (let [parsed (->> (str/split-lines input)
                    (remove str/blank?)
                    (map (fn [line]
                           (let [[colonist & partners] (->> (str/split line #",")
                                                            (map str/trim))]
                             {:colonists [colonist]
                              :possible-pairs (map (fn [partner]
                                                     #{colonist partner})
                                                   partners)}))))
        result (reduce (fn [m1 m2]
                         (-> m1
                             (update :colonists concat (:colonists m2))
                             (update :possible-pairs concat (:possible-pairs m2))))
                       {:colonists []
                        :possible-pairs []}
                       parsed)]
    (-> result
        (update :possible-pairs #(distinct (remove nil? %))))))

(defn find-most-limited-pairs [pairs]
  (let [colonists (apply concat pairs)
        colonist->pair-count (reduce (fn [counts pair]
                                       (assert (= 2 (count pair)))
                                       (reduce #(update %1 %2 inc)
                                               counts
                                               pair))
                                     (zipmap colonists (repeat 0))
                                     pairs)
        min-pair-count (apply min (vals colonist->pair-count))
        most-limited-colonist? (->> colonist->pair-count
                                    (filter (fn [[_colonist pair-count]]
                                              (= min-pair-count pair-count)))
                                    (map first)
                                    (set))]
    (filter (fn [pair]
              (some most-limited-colonist? pair))
            pairs)))

(defn remove-paired-colonists [done-pairs all-possible-pairs]
  (let [done-colonists (set (apply concat done-pairs))]
    (remove (fn [pair]
              (some done-colonists pair))
            all-possible-pairs)))


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
