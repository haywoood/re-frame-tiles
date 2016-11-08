(ns mosaic.events
    (:require [re-frame.core :as re-frame]
              [mosaic.db :refer [default-db default-board get-selected-legend-tile]]
              [cljs-uuid-utils.core :as uuid]
              [hodgepodge.core :as hp]))

(defonce log (atom {}))

(re-frame/reg-fx
  :save-db
  (fn [db]
    (assoc! hp/local-storage :mosaic-db db)))

(re-frame/reg-event-fx
  :save-state
  (fn [{:keys [db]} [_]]
    {:save-db db}))

(re-frame/reg-event-db
  :save-board
  (fn [db [_]]
    (let [board-name (js/prompt "input a board name")
          id (uuid/uuid-string (uuid/make-random-uuid))
          board (:tiles db)
          new-db (update db :saved-boards
                   #(assoc % id {:id id :name board-name :board board}))]
      new-db)))

(re-frame/reg-event-db
  :delete-saved-board
  (fn [db [_ id]]
    (let [confirm? (js/confirm "Delete?")
          new-db (if confirm?
                   (update db :saved-boards #(dissoc % id))
                   db)]
      new-db)))

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
  [(re-frame/path :tiles)]
  (fn [_ _] default-board))

(re-frame/reg-event-db :initialize-db
  (fn [_ _] (or (:mosaic-db hp/local-storage) default-db)))

(re-frame/reg-event-db :select-tile tile-event)

(re-frame/reg-event-db :toggle-dragging toggle-dragging)

(re-frame/reg-event-db :select-legend-tile
  [(re-frame/path :selected-tile)
   (re-frame/->interceptor
     :id :swap-log
     :after (fn [context]
              (reset! log context)
              context))]
  (fn [_ [_ tile]] tile))

(re-frame/reg-event-db :load-saved-board
  (fn [db [_ id]]
    (let [board (get-in db [:saved-boards id :board])]
      (assoc db :tiles board))))

(re-frame/reg-event-db :preview-saved-board
  [(re-frame/path :board-preview-id)]
  (fn [_ [_ id]] id))

(re-frame/reg-event-db :remove-preview-saved-board
  [(re-frame/path :board-preview-id)]
  (fn [_ _] nil))
