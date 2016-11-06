(ns mosaic.subs
  (:require [re-frame.core :as re-frame]
            [mosaic.db :refer [get-selected-legend-tile]]))

(re-frame/reg-sub :saved-boards #(vals (:saved-boards %)))
(re-frame/reg-sub :is-dragging :is-draging)
(re-frame/reg-sub :tiles #(vals (:tiles %)))
(re-frame/reg-sub :legend #(vals (:legend %)))
(re-frame/reg-sub :selected-tile get-selected-legend-tile)
