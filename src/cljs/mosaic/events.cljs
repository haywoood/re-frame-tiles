(ns mosaic.events
    (:require [re-frame.core :as re-frame]
              [mosaic.db :refer [default-db default-board get-selected-legend-tile]]
              [cljs-uuid-utils.core :as uuid]
              [hodgepodge.core :as hp]))

(re-frame/reg-fx
  :save-db
  (fn [db]
    (assoc! hp/local-storage :mosaic-db db)))

(re-frame/reg-event-fx
  :save-state
  (fn [{:keys [db]} [_]]
    {:save-db db}))

(re-frame/reg-event-fx
  :save-board
  (fn [world [_]]
    (let [db (:db world)
          board-name (js/prompt "input a board name")
          id (uuid/uuid-string (uuid/make-random-uuid))
          board (get-in world [:db :tiles])
          new-db (update db :saved-boards
                   #(assoc % id {:id id :name board-name :board board}))]
      { :db new-db
        :save-db new-db})))

(re-frame/reg-event-fx
  :delete-saved-board
  (fn [world [_ id]]
    (let [db (:db world)
          new-db (update db :saved-boards #(dissoc % id))]
      {:db new-db
       :save-db new-db})))

(defn toggle-dragging [db]
  (update db :is-dragging not))

(defn update-tile [tile new-tile]
  (merge tile (dissoc new-tile :id)))

(defmulti tile-event (fn [_ [_ _ action-type]] action-type))

(defmethod tile-event :hover
  [db [_ {:keys [id] :as tile} _]]
  (let [selected    (get-selected-legend-tile db)
        is-dragging (:is-dragging db)
        new-tile    (update-tile tile selected)
        new-db      (assoc-in db [:tiles id] new-tile)]
    (if is-dragging new-db db)))

(defmethod tile-event :key-down
  [db [_ {:keys [id] :as tile} _]]
  (let [selected (get-selected-legend-tile db)
        new-tile (update-tile tile selected)
        new-db   (assoc-in db [:tiles id] new-tile)]
    (toggle-dragging new-db)))

(re-frame/reg-event-db :clear-board
  (fn [db _] (assoc db :tiles default-board)))

(re-frame/reg-event-db :initialize-db
  (fn [_ _] (or (:mosaic-db hp/local-storage) default-db)))

(re-frame/reg-event-db :select-tile tile-event)

(re-frame/reg-event-db :toggle-dragging toggle-dragging)

(re-frame/reg-event-db :select-legend-tile
  (fn [db [_ tile]]
    (assoc db :selected-tile tile)))
