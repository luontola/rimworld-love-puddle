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
    ; TODO: warning that B not listed
    #_(is (= {:colonists ["A"]
              :possible-pairs [#{"A" "B"}]
              :error "Colonist \"B\" was not mentioned on its own line."}
             (ui/parse-input-text "A, B")))
    ; TODO: warning that B and C not listed
    #_(is (= {:colonists ["A"]
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

(deftest remove-paired-colonists-test
  (testing "empty"
    (is (= [] (ui/remove-paired-colonists [] [])))
    (is (= [#{"A" "B"}]
           (ui/remove-paired-colonists []
                                       [#{"A" "B"}])))
    (is (= []
           (ui/remove-paired-colonists [#{"A" "B"}]
                                       []))))

  (testing "removes done pairs"
    (is (= [#{"C" "D"}]
           (ui/remove-paired-colonists [#{"A" "B"}]
                                       [#{"A" "B"}
                                        #{"C" "D"}]))))

  (testing "removes colonists which are part of done pairs"
    (is (= [#{"C" "D"}]
           (ui/remove-paired-colonists [#{"A" "B"}]
                                       [#{"A" "B"}
                                        #{"A" "C"}
                                        #{"B" "C"}
                                        #{"C" "D"}]))
        "done pair; removes all pairs which include A or B")
    (is (= [#{"B" "C"}]
           (ui/remove-paired-colonists [#{"A"}
                                        #{"D"}]
                                       [#{"A" "B"}
                                        #{"A" "C"}
                                        #{"B" "C"}
                                        #{"C" "D"}]))
        "done solo colonists; removes all pairs which include A or D")))
