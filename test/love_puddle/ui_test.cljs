(ns love-puddle.ui-test
  (:require [clojure.test :refer [deftest is testing]]
            [love-puddle.ui :as ui]))

(deftest foo-test
  (is (= 3 (ui/foo 1 2))))
