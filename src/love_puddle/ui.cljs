(ns love-puddle.ui
  (:require [clojure.set :as set]
            [clojure.string :as str]
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

(defonce *data (r/atom {:input-text francis-john-example-input-v2}))

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
        result (-> (reduce (fn [m1 m2]
                             (-> m1
                                 (update :colonists concat (:colonists m2))
                                 (update :possible-pairs concat (:possible-pairs m2))))
                           {:colonists []
                            :possible-pairs []}
                           parsed)
                   (update :possible-pairs #(distinct (remove nil? %))))
        listed-colonists (set (:colonists result))
        paired-colonists (set (apply concat (:possible-pairs result)))
        unlisted-colonists (set/difference paired-colonists listed-colonists)]
    (cond-> result
      (not (empty? unlisted-colonists)) (assoc :error (str "Colonist \"" (first (sort unlisted-colonists))
                                                           "\" was not mentioned on its own line.")))))

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

(defn remove-colonists-from-pairs [colonists pairs]
  (let [colonists (set colonists)]
    (remove (fn [pair]
              (some colonists pair))
            pairs)))

(defn- solve-pairs-impl [state]
  (if (empty? (:possible-pairs state))
    {:colonists (:colonists state)
     :pairs (:done-pairs state)
     :alone (:not-paired state)}
    (let [pairs (find-most-limited-pairs (:possible-pairs state))
          solutions (doall (map (fn recursion [pair]
                                  (-> state
                                      (update :done-pairs conj pair)
                                      (update :not-paired #(set/difference % pair))
                                      (update :possible-pairs #(remove-colonists-from-pairs pair %))
                                      (solve-pairs-impl)))
                                pairs))]
      (->> solutions
           (sort-by #(count (:alone %)))
           (first)))))

(defn solve-pairs [state]
  (solve-pairs-impl (-> state
                        (assoc :done-pairs [])
                        (assoc :not-paired (set (:colonists state))))))

(defn sort-solution [solution]
  (let [colonist-priority (zipmap (:colonists solution) (range))
        pair-priority (fn [[colonist-1 colonist-2]]
                        (min (colonist-priority colonist-1)
                             (colonist-priority colonist-2)))]
    (-> solution
        (update :pairs (fn [pairs]
                         (->> pairs
                              (map #(sort-by colonist-priority %))
                              (sort-by pair-priority))))
        (update :alone #(sort-by colonist-priority %)))))

(defn- calculate-solution [data]
  (let [state (parse-input-text (:input-text data))]
    (if (:error state)
      state
      (sort-solution (solve-pairs state)))))


(defn app []
  [:<>
   [:header
    [:h1 "RimWorld Love Puddle Calculator"]
    [:p.tagline "Because Francis John had a swinger colony "
     [:a {:href "https://youtu.be/X2amcS4Isu0?t=4124"}
      "too big for a spreadsheet"]
     "."]]
   [:main
    [:p "Enter your RimWorld colony and their relationships into the following text box.
         Each row should start with the name of the colonist, followed by their partners and lovers.
         All names on the same line must be separated by a comma.
         The algorithm will try to give priority to those first in the list."]
    [:p [:textarea {:rows 25
                    :cols 80
                    :value (str (:input-text @*data))
                    :on-change (fn [event]
                                 (swap! *data assoc :input-text (str (-> event .-target .-value))))}]]
    [:p [:button {:type "button"
                  :disabled (:calculating @*data)
                  :on-click (fn [_]
                              (swap! *data assoc :calculating true)
                              (swap! *data dissoc :solution)
                              (js/setTimeout (fn []
                                               (swap! *data assoc :solution (calculate-solution @*data))
                                               (swap! *data dissoc :calculating))
                                             100))}
         "Calculate solution"]]

    (when-some [solution (:solution @*data)]
      (if (:error solution)
        [:h2 "Error: " (str (:error solution))]
        [:<>
         [:h2 "Solution"]
         #_[:p {:style {:white-space "pre-line"}}
            (str solution)]
         [:h3 "Same bed (" (count (:pairs solution)) " beds)"]
         (into [:ul]
               (for [pair (:pairs solution)]
                 [:li (str/join " ❤️ " pair)]))
         [:h3 "Alone (" (count (:alone solution)) " beds)"]
         (into [:ul]
               (for [alone (:alone solution)]
                 [:li (str alone)]))]))]
   [:footer
    [:p [:a {:href "https://github.com/luontola/rimworld-love-puddle"}
         "Source code"]]]])


(defn init! []
  (if-some [root (.getElementById js/document "root")]
    (dom/render [app] root)))

(init!)

(defn ^:dev/before-load stop [])

(defn ^:dev/after-load start []
  (init!))
