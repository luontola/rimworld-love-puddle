(ns love-puddle.ui-test
  (:require [clojure.test :refer [deftest is testing]]
            [love-puddle.ui :as ui]))

(deftest input-text->possible-pairs-test
  (testing "empty"
    (is (= {:colonists []
            :possible-pairs []}
           (ui/parse-input-text ""))))

  (testing "only lonely colonists"
    (is (= {:colonists ["A"]
            :possible-pairs []}
           (ui/parse-input-text "A")))
    (is (= {:colonists ["A" "B"]
            :possible-pairs []}
           (ui/parse-input-text "A\nB\n"))))

  (testing "paired colonists"
    (is (= {:colonists ["A" "B"]
            :possible-pairs [#{"A" "B"}]}
           (ui/parse-input-text "A, B\nB, A\n")))
    (is (= {:colonists ["A" "B" "C"]
            :possible-pairs [#{"A" "B"}
                             #{"A" "C"}]}
           (ui/parse-input-text "A, B, C\nB, A\nC, A\n")))
    (is (= {:colonists ["A" "B" "C" "D"]
            :possible-pairs [#{"A" "B"}
                             #{"A" "C"}
                             #{"C" "D"}]}
           (ui/parse-input-text "A, B, C\nB, A\nC, A, D\nD, C\n"))
        "removes duplicates but maintains priority order"))

  (testing "error: every colonist must be mentioned on its own row"
    (is (= {:colonists ["A"]
            :possible-pairs [#{"A" "B"}]
            :error "Colonist \"B\" was not mentioned on its own line."}
           (ui/parse-input-text "A, B")))
    (is (= {:colonists ["A"]
            :possible-pairs [#{"A" "B"}
                             #{"A" "C"}]
            :error "Colonist \"B\" was not mentioned on its own line."}
           (ui/parse-input-text "A, B, C")))))

(deftest find-most-limited-pairs-test
  (testing "empty"
    (is (= [] (ui/find-most-limited-pairs []))))

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

(deftest remove-colonists-from-pairs-test
  (testing "empty"
    (is (= [] (ui/remove-colonists-from-pairs [] [])))
    (is (= [#{"A" "B"}]
           (ui/remove-colonists-from-pairs []
                                           [#{"A" "B"}])))
    (is (= []
           (ui/remove-colonists-from-pairs ["A" "B"]
                                           []))))

  (testing "removes one colonist"
    (is (= [#{"C" "D"}]
           (ui/remove-colonists-from-pairs ["A"]
                                           [#{"A" "B"}
                                            #{"C" "D"}]))))

  (testing "removes multiple colonists"
    (is (= [#{"C" "D"}]
           (ui/remove-colonists-from-pairs ["A" "B"]
                                           [#{"A" "B"}
                                            #{"A" "C"}
                                            #{"B" "C"}
                                            #{"C" "D"}]))
        "removes all pairs which include A or B")))

(deftest solve-pairs-test
  (testing "empty"
    (is (= {:colonists []
            :pairs []
            :alone #{}}
           (ui/solve-pairs {:colonists []
                            :possible-pairs []}))))

  (testing "one pair"
    (is (= {:colonists ["A" "B"]
            :pairs [#{"A" "B"}]
            :alone #{}}
           (ui/solve-pairs {:colonists ["A" "B"]
                            :possible-pairs [#{"A" "B"}]}))))

  (testing "some left alone"
    (is (= {:colonists ["A" "B" "C"]
            :pairs [#{"A" "B"}]
            :alone #{"C"}}
           (ui/solve-pairs {:colonists ["A" "B" "C"]
                            :possible-pairs [#{"A" "B"}]}))))

  (testing "multiple pairs (recursion needed)"
    (is (= {:colonists ["A" "B" "C" "D" "E"]
            :pairs [#{"B" "C"} #{"A" "D"}]
            :alone #{"E"}}
           (ui/solve-pairs {:colonists ["A" "B" "C" "D" "E"]
                            :possible-pairs [#{"A" "B"}
                                             #{"A" "D"}
                                             #{"B" "C"}
                                             #{"D" "E"}]})))))

(deftest sort-solution-test
  (testing "sorts :pairs and :alone according to the order of :colonists"
    (is (= {:colonists ["A" "B" "C" "D" "E" "F"]
            :pairs [["A" "D"]
                    ["B" "C"]]
            :alone ["E" "F"]}
           (ui/sort-solution {:colonists ["A" "B" "C" "D" "E" "F"]
                              :pairs [#{"B" "C"} #{"A" "D"}]
                              :alone #{"F" "E"}})))))
