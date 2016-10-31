(ns mosaic.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :next-id
 (fn [db]
   (inc (:id db))))

(re-frame/reg-sub
  :is-dragging
  (fn [db]
    (:is-dragging db)))

(re-frame/reg-sub
  :tiles
  (fn [db]
    (vals (:tiles db))))

(re-frame/reg-sub
  :legend
  (fn [db]
    (vals (:legend db))))

(re-frame/reg-sub
  :selected-tile
  (fn [{:keys [legend selected]}]
    (or selected (first (vals legend)))))
