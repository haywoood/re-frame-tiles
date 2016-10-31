(ns mosaic.events
    (:require [re-frame.core :as re-frame]
              [mosaic.db :refer [default-db]]))

(defn toggle-dragging [db]
  (update db :is-dragging not))

(defn update-tile [tile new-tile]
  (merge tile (dissoc new-tile :id)))

(defmulti tile-event (fn [_ [_ _ action-type]] action-type))

(defmethod tile-event :hover
  [db [_ {:keys [id] :as tile} _]]
  (let [selected    (re-frame/subscribe [:selected-tile])
        is-dragging (re-frame/subscribe [:is-dragging])
        new-tile    (update-tile tile @selected)
        new-db      (assoc-in db [:tiles id] new-tile)]
    (if @is-dragging new-db db)))

(defmethod tile-event :key-down
  [db [_ {:keys [id] :as tile} _]]
  (let [selected (re-frame/subscribe [:selected-tile])
        new-tile (update-tile tile @selected)
        new-db   (assoc-in db [:tiles id] new-tile)]
    (toggle-dragging new-db)))

(re-frame/reg-event-db :initialize-db
  (fn [_ _] default-db))

(re-frame/reg-event-db :select-tile tile-event)

(re-frame/reg-event-db :toggle-dragging toggle-dragging)

(re-frame/reg-event-db :select-legend-tile
  (fn [db [_ tile]]
    (assoc db :selected tile)))
