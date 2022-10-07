(ns kata.ui-test
  (:require [clojure.test :refer [deftest is testing]]
            [kata.ui :as ui]))

(deftest foo-test
  (is (= 3 (ui/foo 1 2))))
