(ns kata.ui
  (:require ["@js-temporal/polyfill$Temporal.Instant" :as Instant]
            [kitchen-async.promise :as p]
            [reagent.core :as r]
            [reagent.dom :as dom]))

(defn foo [a b]
  (p/promise [_resolve _reject])
  (Instant/from "2000-01-01T12:00:00Z")
  (+ a b))


(defonce *data (r/atom "World"))

(defn app []
  [:<>
   [:header
    [:h1 "New ClojureScript Project"]]
   [:main
    [:p "Hello " @*data]]])


(defn init! []
  (if-some [root (.getElementById js/document "root")]
    (dom/render [app] root)))

(init!)

(defn ^:dev/before-load stop [])

(defn ^:dev/after-load start []
  (init!))
