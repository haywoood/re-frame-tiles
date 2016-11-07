(ns mosaic.subs
  (:require [re-frame.core :as re-frame]
            [mosaic.db :refer [get-selected-legend-tile]]))

(re-frame/reg-sub :saved-boards #(into [] (reverse (vals (:saved-boards %)))))
(re-frame/reg-sub :is-dragging :is-draging)
(re-frame/reg-sub :legend #(vals (:legend %)))
(re-frame/reg-sub :selected-tile get-selected-legend-tile)
(re-frame/reg-sub :board-preview-id :board-preview-id)
(re-frame/reg-sub
  :tiles
  (fn [{:keys [board-preview-id tiles saved-boards]}]
    (if (nil? board-preview-id)
      (vals tiles)
      (vals (get-in saved-boards [board-preview-id :board])))))
