(ns love-puddle.ui-test
  (:require [clojure.test :refer [deftest is testing]]
            [love-puddle.ui :as ui]))

(deftest input-text->possible-pairs-test
  (testing "empty"
    (is (= [] (ui/input-text->possible-pairs ""))))

  (testing "only lonely colonists"
    (is (= [#{"A"}]
           (ui/input-text->possible-pairs "A")))
    (is (= [#{"A"}
            #{"B"}]
           (ui/input-text->possible-pairs "A\nB\n"))))

  (testing "paired colonists"
    (is (= [#{"A" "B"}]
           (ui/input-text->possible-pairs "A, B")))
    (is (= [#{"A" "B"}
            #{"A" "C"}]
           (ui/input-text->possible-pairs "A, B, C")))
    (is (= [#{"A" "B"}
            #{"A" "C"}
            #{"B" "A"}
            #{"C" "A"}]
           (ui/input-text->possible-pairs "A, B, C\nB, A\nC, A")))))
