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
            #{"C" "D"}]
           (ui/input-text->possible-pairs "A, B, C\nB, A\nC, A, D\nD, C\n"))
        "removes duplicates but maintains priority order")))

(deftest find-most-limited-pairs-test
  (testing "empty"
    (is (= [] (ui/find-most-limited-pairs []))))

  (testing "solo is more limiting that one pair"
    (is (= [#{"C"}
            #{"D"}]
           (ui/find-most-limited-pairs [#{"A" "B"}
                                        #{"C"}
                                        #{"D"}]))))

  (testing "one pair is more limiting that multiple pairs"
    (is (= [#{"A" "B"}]
           (ui/find-most-limited-pairs [#{"A" "B"}]))
        "last pair")
    (is (= [#{"B" "A"}
            #{"D" "C"}]
           (ui/find-most-limited-pairs [#{"A" "B"}
                                        #{"A" "C"}
                                        #{"C" "D"}]))
        "B and D have only one possible pair, whereas A and C have two possibilities")))
